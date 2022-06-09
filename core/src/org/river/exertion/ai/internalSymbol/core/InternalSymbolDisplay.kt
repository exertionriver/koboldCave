package org.river.exertion.ai.internalSymbol.core

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

class InternalSymbolDisplay(val entity : Telegraph) : Telegraph {

    var symbolDisplay = mutableSetOf<SymbolInstance>()
    var circularity = mutableSetOf<Pair<SymbolInstance, SymbolInstance>>()

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.INT_SYMBOL_SPAWN.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_SYMBOL_DESPAWN.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_SYMBOL_MODIFY.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_SYMBOL_MODIFIED.id())
    }

    @Suppress("NewApi")
    fun despawn(symbolMessage : SymbolMessage) {
        when {
            //despawn all of a symboltype
            (symbolMessage.symbol != null) ->
                if (symbolMessage.symbolDisplayType == null)
                    symbolDisplay.removeIf { it.symbolObj == symbolMessage.symbol }
                //despawn all of a symboltype and displaytype
                else
                    symbolDisplay.removeIf { it.symbolObj == symbolMessage.symbol && it.displayType == symbolMessage.symbolDisplayType }
            //despawn a particular instance
            (symbolMessage.symbolInstance != null) ->
                symbolDisplay.removeIf { it == symbolMessage.symbolInstance }
        }
    }

    fun spawn(symbolMessage : SymbolMessage) {
        when {
            //spawn of a symboltype
            (symbolMessage.symbol != null) ->
                if (symbolMessage.symbolDisplayType != null)
                    symbolDisplay.add(symbolMessage.symbol!!.spawn())
                //spawn of a symboltype and display type
                else
                    symbolDisplay.add(symbolMessage.symbol!!.spawn().apply { this.displayType = symbolMessage.symbolDisplayType!! })
            //spawn a particular instance in the display
            (symbolMessage.symbolInstance != null) ->
                symbolDisplay.add(symbolMessage.symbolInstance!!)
        }
    }

    fun update(symbolMessage : SymbolMessage) {
        if (symbolMessage.symbolInstance != null)
            if (symbolMessage.symbolDisplayType != null)
                symbolDisplay.filter { it == symbolMessage.symbolInstance!! && it.displayType == symbolMessage.symbolDisplayType }.forEach {
                    it.cycles = symbolMessage.symbolInstance!!.cycles
                    it.position = symbolMessage.symbolInstance!!.position
                    it.consumeCapacity = symbolMessage.symbolInstance!!.consumeCapacity
                    it.handleCapacity = symbolMessage.symbolInstance!!.handleCapacity
                    it.possessCapacity = symbolMessage.symbolInstance!!.possessCapacity
                }
            else symbolDisplay.filter { it == symbolMessage.symbolInstance!! }.forEach {
                it.cycles = symbolMessage.symbolInstance!!.cycles
                it.position = symbolMessage.symbolInstance!!.position
                it.consumeCapacity = symbolMessage.symbolInstance!!.consumeCapacity
                it.handleCapacity = symbolMessage.symbolInstance!!.handleCapacity
                it.possessCapacity = symbolMessage.symbolInstance!!.possessCapacity
            }
    }
    /*
    SymbolModifyAction(FoodSymbol, HungerSymbol, SymbolDisplayType.PRESENT, SymbolModifierType.CYCLE_TO_POSITION, .1f),
    SymbolModifyAction(MomentElapseSymbol, HungerSymbol, SymbolDisplayType.PRESENT, SymbolModifierType.POSITION_TO_POSITION, -.001f),
    SymbolModifyAction(HungerSymbol, FoodSymbol, SymbolDisplayType.ABSENT, SymbolModifierType.POSITION_TO_POSITION, -.1f, 1f)
    */
    //to do: check for circularity
    fun propagateUpdate(symbolMessage : SymbolMessage) {
        if (symbolMessage.symbolInstance != null) {
            symbolDisplay.flatMap { it.symbolObj.symbolActions }.filter { it.symbolActionType == SymbolActionType.MODIFY && (it as SymbolModifyAction).sourceSymbol == symbolMessage.symbolInstance!!.symbolObj }.forEach { symbolAction ->
                symbolDisplay.filter {it.symbolObj == (symbolAction as SymbolModifyAction).targetSymbol && it.displayType == symbolAction.targetDisplayType}.forEach { targetSymbolInstance ->
                    if (circularity.contains(Pair(symbolMessage.symbolInstance!!, targetSymbolInstance)) ) throw Exception("circularity detected: ${symbolMessage.symbolInstance}, $targetSymbolInstance")
                    else {
                        circularity.add(Pair(symbolMessage.symbolInstance!!, targetSymbolInstance))
                        symbolAction.execute(entity, SymbolMessage(symbolInstance = symbolMessage.symbolInstance, targetSymbolInstance = targetSymbolInstance, targetSymbolDisplayType = (symbolAction as SymbolModifyAction).targetDisplayType))
                    }
                }
            }
        }
    }

    @Suppress("NewApi")
    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_SYMBOL_SPAWN.id()) {
                this.spawn(msg.extraInfo as SymbolMessage)
            }
            if (msg.message == MessageChannel.INT_SYMBOL_DESPAWN.id()) {
                this.despawn(msg.extraInfo as SymbolMessage)
            }
            if (msg.message == MessageChannel.INT_SYMBOL_MODIFY.id()) {
                this.update(msg.extraInfo as SymbolMessage)
            }
            if (msg.message == MessageChannel.INT_SYMBOL_MODIFIED.id()) {
                this.propagateUpdate(msg.extraInfo as SymbolMessage)
            }
        }
        return true
    }
}