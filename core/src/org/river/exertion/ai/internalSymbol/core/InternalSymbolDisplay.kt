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
            (symbolMessage.symbol != null) ->
                if (symbolMessage.symbolDisplayType != null)
                    //despawn all instances of a symbolObj and displaytype
                    symbolDisplay.removeIf { it.symbolObj == symbolMessage.symbol && it.displayType == symbolMessage.symbolDisplayType }
                else
                    //despawn all instances of a symbolObj
                    symbolDisplay.removeIf { it.symbolObj == symbolMessage.symbol }
            (symbolMessage.symbolInstance != null) ->
                //despawn a particular instance of a symbolObj
                symbolDisplay.removeIf { it == symbolMessage.symbolInstance }
        }
    }

    fun spawn(symbolMessage : SymbolMessage) {
        when {
            (symbolMessage.symbol != null) ->
                if (symbolMessage.symbolDisplayType != null && symbolDisplay.none { it.symbolObj == symbolMessage.symbol && it.displayType == symbolMessage.symbolDisplayType })
                    //spawn instance of a symboltype if obj not already spawned
                    symbolDisplay.add(symbolMessage.symbol!!.spawn().apply { this.displayType = symbolMessage.symbolDisplayType!! })
                else if (symbolDisplay.none { it.symbolObj == symbolMessage.symbol })
                    //spawn insance of a symboltype and display type is obj not already spawned
                    symbolDisplay.add(symbolMessage.symbol!!.spawn())
            //spawn a particular instance in the display, multiple are allowed for PRESENT, single allowed for ABSENT
            (symbolMessage.symbolInstance != null) ->
                if (symbolMessage.symbolInstance!!.displayType == SymbolDisplayType.PRESENT || symbolDisplay.none { it.symbolObj == symbolMessage.symbolInstance!!.symbolObj && it.displayType == symbolMessage.symbolInstance!!.displayType })
                    symbolDisplay.add(symbolMessage.symbolInstance!!)
        }
    }

    fun update(symbolMessage : SymbolMessage) {
        if (symbolMessage.symbolInstance != null && symbolDisplay.any { it == symbolMessage.symbolInstance }) {
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
            //spawn / despawn / etc. as needed
            symbolMessage.symbolInstance!!.symbolObj.symbolActions.filter { it.symbolActionType != SymbolActionType.MODIFY}.forEach {
                it.execute(entity, SymbolMessage(symbolInstance = symbolMessage.symbolInstance!!))
            }
        }
    }
    /*
    SymbolModifyAction(FoodSymbol, HungerSymbol, SymbolDisplayType.PRESENT, SymbolModifierType.CYCLE_TO_POSITION, .1f),
    SymbolModifyAction(MomentElapseSymbol, HungerSymbol, SymbolDisplayType.PRESENT, SymbolModifierType.POSITION_TO_POSITION, -.001f),
    SymbolModifyAction(HungerSymbol, FoodSymbol, SymbolDisplayType.ABSENT, SymbolModifierType.POSITION_TO_POSITION, -.1f, 1f)
    */
    fun propagateUpdate(symbolMessage : SymbolMessage) {
        if (symbolMessage.symbolInstance != null && symbolDisplay.any { it == symbolMessage.symbolInstance } && symbolMessage.symbolInstance!!.displayType == SymbolDisplayType.PRESENT) {
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