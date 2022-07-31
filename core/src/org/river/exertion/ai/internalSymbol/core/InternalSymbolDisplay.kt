package org.river.exertion.ai.internalSymbol.core

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState.Companion.merge
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.messaging.FacetMessage
import org.river.exertion.ai.messaging.FocusMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

@Suppress("NewApi")
class InternalSymbolDisplay(val entity : Telegraph) : Telegraph {

    var symbolDisplay = mutableSetOf<SymbolInstance>()
    var circularity = mutableSetOf<Pair<SymbolInstance, SymbolInstance>>()

    init {
        MessageChannel.INT_SYMBOL_SPAWN.enableReceive(this)
        MessageChannel.INT_SYMBOL_DESPAWN.enableReceive(this)
        MessageChannel.INT_SYMBOL_MODIFY.enableReceive(this)
        MessageChannel.INT_SYMBOL_MODIFIED.enableReceive(this)
    }

    fun mergeAndUpdateFacets() {
        val internalFacetStates = mutableSetOf<InternalFacetInstancesState>()

        symbolDisplay.filter { it.displayType == SymbolDisplayType.PRESENT }.forEach {
            if (it.currentFacetState.isNotEmpty() )
                internalFacetStates.add(InternalFacetInstancesState(entity, it.currentFacetState))
        }

        val compositeFacetStates = internalFacetStates.merge(entity).internalState

        MessageChannel.INT_FACET_MODIFY.send(entity, FacetMessage(internalFacets = compositeFacetStates))
    }

    //despawn all instances of this symbol
    fun despawn(symbol : IPerceivedSymbol) {
        //remove all plans related to symbolObj
        symbolDisplay.filter { it.symbolObj == symbol && it.displayType == SymbolDisplayType.ABSENT }.forEach { absentSymbolInstance ->
            MessageChannel.INT_REMOVE_FOCUS_PLAN.send(entity, FocusMessage(absentSymbolInstance = absentSymbolInstance) )
        }
        //despawn all instances of a symbolObj
        symbolDisplay.removeIf { it.symbolObj == symbol }
    }

    //despawn all instances of this symbol for a particular display type
    fun despawn(symbol : IPerceivedSymbol, symbolDisplayType: SymbolDisplayType) {
        if (symbolDisplayType == SymbolDisplayType.ABSENT)
        //remove all plans related to symbolObj if displayType is ABSENT
            symbolDisplay.filter { it.symbolObj == symbol && it.displayType == SymbolDisplayType.ABSENT }.forEach { absentSymbolInstance ->
                MessageChannel.INT_REMOVE_FOCUS_PLAN.send(entity, FocusMessage(absentSymbolInstance = absentSymbolInstance) )
            }
        //despawn all instances of a symbolObj and displaytype
        symbolDisplay.removeIf { it.symbolObj == symbol && it.displayType == symbolDisplayType }
    }

    //despawn an actual instance of this symbol
    fun despawn(symbolInstance : SymbolInstance) {
        if (symbolInstance.displayType == SymbolDisplayType.ABSENT)
        //remove all plans related to ABSENT symbolInstance
            MessageChannel.INT_REMOVE_FOCUS_PLAN.send(entity, FocusMessage(absentSymbolInstance = symbolInstance) )
        //despawn a particular instance of a symbolObj
        symbolDisplay.removeIf { it == symbolInstance }
    }

    @Suppress("NewApi")
    fun despawnHandler(symbolMessage : SymbolMessage) {
        when {
            (symbolMessage.symbol != null) ->
                if (symbolMessage.symbolDisplayType != null) {
                    despawn(symbolMessage.symbol!!, symbolMessage.symbolDisplayType!!)
                } else {
                    despawn(symbolMessage.symbol!!)
                }
            (symbolMessage.symbolInstance != null) -> {
                despawn(symbolMessage.symbolInstance!!)
            }
        }
    }

    fun spawnHandler(symbolMessage : SymbolMessage) {
        when {
            (symbolMessage.symbol != null) ->
                if (symbolMessage.symbolDisplayType != null && symbolDisplay.none { it.symbolObj == symbolMessage.symbol && it.displayType == symbolMessage.symbolDisplayType }) {
                    //spawn instance of a symboltype if obj not already spawned
                    val spawnSymbol = symbolMessage.symbol!!.spawn().apply { this.displayType = symbolMessage.symbolDisplayType!! }
                    symbolDisplay.add(spawnSymbol)
                    //if spawn symbol display type is ABSENT, add focus
                    if (symbolMessage.symbolDisplayType == SymbolDisplayType.ABSENT)
                        symbolMessage.symbolInstance!!.symbolObj.focusSatisfiers.forEach {
                            MessageChannel.INT_ADD_FOCUS_PLAN.send(entity, FocusMessage(satisfierFocus = it, absentSymbolInstance = spawnSymbol))
                        }
                } else if (symbolDisplay.none { it.symbolObj == symbolMessage.symbol })
                //spawn insance of a symboltype and display type is obj not already spawned
                    symbolDisplay.add(symbolMessage.symbol!!.spawn())
            //spawn a particular instance in the display, multiple are allowed for PRESENT, single allowed for ABSENT
            (symbolMessage.symbolInstance != null) ->
                if (symbolMessage.symbolInstance!!.displayType == SymbolDisplayType.PRESENT)
                    symbolDisplay.add(symbolMessage.symbolInstance!!)
                //ABSENT symbol
                else if (symbolDisplay.none { it.symbolObj == symbolMessage.symbolInstance!!.symbolObj && it.displayType == symbolMessage.symbolInstance!!.displayType }) {
                    symbolDisplay.add(symbolMessage.symbolInstance!!)
                    //add first satisfier to focus
                    symbolMessage.symbolInstance!!.symbolObj.focusSatisfiers.forEach {
                        MessageChannel.INT_ADD_FOCUS_PLAN.send(entity, FocusMessage(satisfierFocus = it, absentSymbolInstance = symbolMessage.symbolInstance!!))
                    }
                }
        }

    }
    fun spawn(symbolMessage : SymbolMessage) {
        when {
            (symbolMessage.symbol != null) ->
                if (symbolMessage.symbolDisplayType != null && symbolDisplay.none { it.symbolObj == symbolMessage.symbol && it.displayType == symbolMessage.symbolDisplayType }) {
                    //spawn instance of a symboltype if obj not already spawned
                    val spawnSymbol = symbolMessage.symbol!!.spawn().apply { this.displayType = symbolMessage.symbolDisplayType!! }
                    symbolDisplay.add(spawnSymbol)
                    //if spawn symbol display type is ABSENT, add focus
                    if (symbolMessage.symbolDisplayType == SymbolDisplayType.ABSENT)
                        symbolMessage.symbolInstance!!.symbolObj.focusSatisfiers.forEach {
                            MessageChannel.INT_ADD_FOCUS_PLAN.send(entity, FocusMessage(satisfierFocus = it, absentSymbolInstance = spawnSymbol))
                        }
                } else if (symbolDisplay.none { it.symbolObj == symbolMessage.symbol })
                    //spawn insance of a symboltype and display type is obj not already spawned
                    symbolDisplay.add(symbolMessage.symbol!!.spawn())
            //spawn a particular instance in the display, multiple are allowed for PRESENT, single allowed for ABSENT
            (symbolMessage.symbolInstance != null) ->
                if (symbolMessage.symbolInstance!!.displayType == SymbolDisplayType.PRESENT)
                    symbolDisplay.add(symbolMessage.symbolInstance!!)
                //ABSENT symbol
                else if (symbolDisplay.none { it.symbolObj == symbolMessage.symbolInstance!!.symbolObj && it.displayType == symbolMessage.symbolInstance!!.displayType }) {
                    symbolDisplay.add(symbolMessage.symbolInstance!!)
                    //add first satisfier to focus
                    symbolMessage.symbolInstance!!.symbolObj.focusSatisfiers.forEach {
                        MessageChannel.INT_ADD_FOCUS_PLAN.send(entity, FocusMessage(satisfierFocus = it, absentSymbolInstance = symbolMessage.symbolInstance!!))
                    }
                }
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
            if (symbolMessage.symbolInstance!!.cycles == 0f && symbolMessage.symbolInstance!!.displayType == SymbolDisplayType.PRESENT) {
                despawnHandler(symbolMessage)
            }
        }
    }

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

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_SYMBOL_SPAWN.id()) {
                this.spawn(MessageChannel.INT_SYMBOL_SPAWN.receiveMessage(msg.extraInfo))
            }
            if (msg.message == MessageChannel.INT_SYMBOL_DESPAWN.id()) {
                this.despawnHandler(MessageChannel.INT_SYMBOL_DESPAWN.receiveMessage(msg.extraInfo))
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