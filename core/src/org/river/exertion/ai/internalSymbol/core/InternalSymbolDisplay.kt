package org.river.exertion.ai.internalSymbol.core

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState.Companion.merge
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.messaging.*

@Suppress("NewApi")
class InternalSymbolDisplay(val entity : Telegraph) : Telegraph {

    var symbolDisplay = mutableSetOf<SymbolInstance>()
    var circularity = mutableSetOf<Pair<SymbolInstance, SymbolInstance>>()

    init {
        MessageChannel.INT_SYMBOL_SPAWN.enableReceive(this)
        MessageChannel.INT_SYMBOL_DESPAWN.enableReceive(this)
        MessageChannel.INT_SYMBOL_MODIFY.enableReceive(this)
        MessageChannel.INT_SYMBOL_ADD_ORNAMENT.enableReceive(this)
        MessageChannel.INT_SYMBOL_REMOVE_ORNAMENT.enableReceive(this)
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
    private fun despawn(symbol : IPerceivedSymbol) {
        //remove all plans related to symbolObj
        symbolDisplay.filter { it.symbolObj == symbol && it.displayType == SymbolDisplayType.ABSENT }.forEach { absentSymbolInstance ->
            MessageChannel.INT_REMOVE_FOCUS_PLAN.send(entity, FocusMessage(absentSymbolInstance = absentSymbolInstance) )
        }
        //despawn all instances of a symbolObj
        symbolDisplay.removeIf { it.symbolObj == symbol }
    }

    //despawn all instances of this symbol for a particular display type
    private fun despawn(symbol : IPerceivedSymbol, symbolDisplayType: SymbolDisplayType) {
        if (symbolDisplayType == SymbolDisplayType.ABSENT)
        //remove all plans related to symbolObj if displayType is ABSENT
            symbolDisplay.filter { it.symbolObj == symbol && it.displayType == SymbolDisplayType.ABSENT }.forEach { absentSymbolInstance ->
                MessageChannel.INT_REMOVE_FOCUS_PLAN.send(entity, FocusMessage(absentSymbolInstance = absentSymbolInstance) )
            }
        //despawn all instances of a symbolObj and displaytype
        symbolDisplay.removeIf { it.symbolObj == symbol && it.displayType == symbolDisplayType }
    }

    //despawn an actual instance of this symbol
    private fun despawn(symbolInstance : SymbolInstance) {
        if (symbolInstance.displayType == SymbolDisplayType.ABSENT)
        //remove all plans related to ABSENT symbolInstance
            MessageChannel.INT_REMOVE_FOCUS_PLAN.send(entity, FocusMessage(absentSymbolInstance = symbolInstance) )
        //despawn a particular instance of a symbolObj
        symbolDisplay.removeIf { it == symbolInstance }
    }

    private fun despawnHandler(symbolMessage : SymbolMessage) {
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

    //spawn instance of symbol, defaulting to PRESENT display
    private fun spawn(symbol : IPerceivedSymbol) {
        val symbolInstance = symbol.spawn()
        symbolDisplay.add(symbolInstance)
        update(symbolInstance)
    }

    //spawn instance of symbol in display type
    private fun spawn(symbol : IPerceivedSymbol, symbolDisplayType: SymbolDisplayType) {
        val symbolInstance : SymbolInstance

        if (symbolDisplayType == SymbolDisplayType.ABSENT) {
            //only one symbol of this type can be spawned in ABSENT display
            if (symbolDisplay.none { it.symbolObj == symbol && it.displayType == symbolDisplayType }) {

                symbolInstance = symbol.spawn().apply { this.displayType = symbolDisplayType }
                symbolDisplay.add(symbolInstance)

                //add focus for ABSENT symbol
                symbol.focusSatisfiers.forEach {
                    MessageChannel.INT_ADD_FOCUS_PLAN.send(entity, FocusMessage(satisfierFocus = it, absentSymbolInstance = symbolInstance))
                }
                update(symbolInstance)
            }
        } else {
            symbolInstance = symbol.spawn()
            symbolDisplay.add(symbolInstance)
            update(symbolInstance)
        }
    }

    private fun spawn(symbolInstance : SymbolInstance) {
        if (symbolInstance.displayType == SymbolDisplayType.ABSENT) {
            if (symbolDisplay.none { it.symbolObj == symbolInstance.symbolObj && it.displayType == symbolInstance.displayType }) {

                symbolDisplay.add(symbolInstance)

                //add focus for ABSENT symbol
                symbolInstance.symbolObj.focusSatisfiers.forEach {
                    MessageChannel.INT_ADD_FOCUS_PLAN.send(
                        entity,
                        FocusMessage(satisfierFocus = it, absentSymbolInstance = symbolInstance)
                    )
                }
            }
        } else {
            symbolDisplay.add(symbolInstance)
        }
        update(symbolInstance)
    }

    private fun spawnHandler(symbolMessage : SymbolMessage) {
        when {
            (symbolMessage.symbol != null) ->
                if (symbolMessage.symbolDisplayType != null) {
                    spawn(symbolMessage.symbol!!, symbolMessage.symbolDisplayType!!)
                } else {
                    spawn(symbolMessage.symbol!!)
                }

            (symbolMessage.symbolInstance != null) -> {
                spawn(symbolMessage.symbolInstance!!)
            }
        }
    }

    private fun updateHandler(symbolMessage : SymbolMessage) {

        if (symbolMessage.symbolInstance != null) {
            if (symbolMessage.symbolInstance!!.displayType == SymbolDisplayType.PRESENT) {
                propagateUpdateHandler(symbolMessage)
            }
            update(symbolMessage.symbolInstance!!)
        }
    }

    private fun addOrnamentHandler(ornamentMessage : OrnamentMessage) {

        symbolDisplay.filter { it == ornamentMessage.symbolInstance }.firstOrNull { it.ornaments.add(ornamentMessage.ornament!!) }
        spawn(ornamentMessage.symbolInstance!!.symbolObj, SymbolDisplayType.ABSENT)
    }

    private fun removeOrnamentHandler(ornamentMessage : OrnamentMessage) {

        symbolDisplay.filter { it == ornamentMessage.symbolInstance }.firstOrNull { it.ornaments.remove(ornamentMessage.ornament!!) }
    }

    private fun update(symbolInstance: SymbolInstance) {

        if (symbolDisplay.any { it == symbolInstance } ) {

            symbolInstance.normalizePosition()
            symbolInstance.normalizeFacetState()

            symbolDisplay.filter { it == symbolInstance && it.displayType == symbolInstance.displayType }.forEach {
                it.cycles = symbolInstance.cycles
                it.position = symbolInstance.position
                it.consumeCapacity = symbolInstance.consumeCapacity
                it.handleCapacity = symbolInstance.handleCapacity
                it.possessCapacity = symbolInstance.possessCapacity
            }

            //spawn / despawn / etc. as needed
            symbolInstance.symbolObj.symbolActions.filter { it.symbolActionType != SymbolActionType.MODIFY}.forEach {
                it.execute(entity, SymbolMessage(symbolInstance = symbolInstance))
            }
            if (symbolInstance.cycles == 0f && symbolInstance.displayType == SymbolDisplayType.PRESENT) {
                despawn(symbolInstance)
            }
        }
    }

    private fun propagateUpdateHandler(symbolMessage : SymbolMessage) {
        if (symbolMessage.symbolInstance != null && symbolMessage.symbolInstance!!.displayType == SymbolDisplayType.PRESENT) {
            propagateUpdate(symbolMessage.symbolInstance!!)
        }
    }

    private fun propagateUpdate(symbolInstance : SymbolInstance) {
        if (symbolDisplay.any { it == symbolInstance }) {
            symbolDisplay.flatMap { it.symbolObj.symbolActions }.filter { it.symbolActionType == SymbolActionType.MODIFY && (it as SymbolModifyAction).sourceSymbol == symbolInstance.symbolObj }.forEach { symbolAction ->
                symbolDisplay.filter { it.symbolObj == (symbolAction as SymbolModifyAction).targetSymbol && it.displayType == symbolAction.targetDisplayType}.forEach { targetSymbolInstance ->
                    if (circularity.contains(Pair(symbolInstance, targetSymbolInstance)) ) throw Exception("circularity detected: ${symbolInstance}, $targetSymbolInstance")
                    else {
                        circularity.add(Pair(symbolInstance, targetSymbolInstance))
                        symbolAction.execute(entity, SymbolMessage(symbolInstance = symbolInstance, targetSymbolInstance = targetSymbolInstance, targetSymbolDisplayType = (symbolAction as SymbolModifyAction).targetDisplayType))
                    }
                }
            }
        }
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_SYMBOL_SPAWN.id()) {
                this.spawnHandler(MessageChannel.INT_SYMBOL_SPAWN.receiveMessage(msg.extraInfo))
            }
            if (msg.message == MessageChannel.INT_SYMBOL_DESPAWN.id()) {
                this.despawnHandler(MessageChannel.INT_SYMBOL_DESPAWN.receiveMessage(msg.extraInfo))
            }
            if (msg.message == MessageChannel.INT_SYMBOL_MODIFY.id()) {
                this.updateHandler(MessageChannel.INT_SYMBOL_MODIFY.receiveMessage(msg.extraInfo))
            }
            if (msg.message == MessageChannel.INT_SYMBOL_ADD_ORNAMENT.id()) {
                this.addOrnamentHandler(MessageChannel.INT_SYMBOL_ADD_ORNAMENT.receiveMessage(msg.extraInfo))
            }
            if (msg.message == MessageChannel.INT_SYMBOL_REMOVE_ORNAMENT.id()) {
                this.removeOrnamentHandler(MessageChannel.INT_SYMBOL_REMOVE_ORNAMENT.receiveMessage(msg.extraInfo))
            }
        }
        return true
    }
}