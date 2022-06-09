package org.river.exertion.ai.internalSymbol.core

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.perceivedSymbols.NonePerceivedSymbol
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolActionMessage
import kotlin.math.sign

data class SymbolInstance (var symbolObj : IPerceivedSymbol = NonePerceivedSymbol, var displayType : SymbolDisplayType = SymbolDisplayType.PRESENT, var cycles : Float = 0f, var position : Float = 0f) {

    var impact = 0f //used for absent symbols

    var deltaCycles = 0f
    var deltaPosition = 0f

    var consumeCapacity = 0f
    var handleCapacity = 0f
    var possessCapacity = 0f

    var ornaments = mutableSetOf<SymbolInstance>()

    fun normalizePosition() {

        this.position += deltaPosition
        this.cycles += deltaCycles

        //first update position wrt symbol cycle style
        if (this.symbolObj.cycle == SymbolCycle.MULTIPLE) {
            while (this.position < 0) {
                this.position += 1
                this.cycles -= 1
            }
            while (this.position > 1) {
                this.position -= 1
                this.cycles += 1
            }
        } else { //single or none
            if (this.position < 0) {
                this.position = 0f
                this.cycles = 0f
            }
            if (this.position > 1) {
                this.position = 1f
                this.cycles = 0f
            }
        }

        deltaCycles = 0f
        deltaPosition = 0f
    }

    //indirectly update position via modification
    //e.g. this == Hunger, modifyingSymbol == Food
    fun updateModifiedPosition(modifyingSymbol : SymbolInstance, modifierType: SymbolModifierType, sourceToTargetRatio : Float) {

        deltaPosition = when (modifierType) {
            SymbolModifierType.POSITION_TO_POSITION ->
                this.symbolObj.targetPosition.targetPosition().sign *
                        modifyingSymbol.symbolObj.targetPosition.targetPosition().sign *
                        sourceToTargetRatio *
                        modifyingSymbol.deltaPosition
            SymbolModifierType.CYCLE_TO_POSITION ->
                this.symbolObj.targetPosition.targetPosition().sign *
                        modifyingSymbol.symbolObj.targetPosition.targetPosition().sign *
                        sourceToTargetRatio *
                        modifyingSymbol.deltaCycles
            else -> 0f
        }

        deltaCycles = when (modifierType) {
            SymbolModifierType.CYCLE_TO_CYCLE ->
                this.symbolObj.targetPosition.targetPosition().sign *
                        modifyingSymbol.symbolObj.targetPosition.targetPosition().sign *
                        sourceToTargetRatio *
                        modifyingSymbol.deltaCycles
            SymbolModifierType.POSITION_TO_CYCLE ->
                this.symbolObj.targetPosition.targetPosition().sign *
                        modifyingSymbol.symbolObj.targetPosition.targetPosition().sign *
                        sourceToTargetRatio *
                        modifyingSymbol.deltaPosition
            else -> 0f
        }
    }
}