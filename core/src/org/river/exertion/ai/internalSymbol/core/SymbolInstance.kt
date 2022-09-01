package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalFacet.InternalFacetInstance
import org.river.exertion.ai.internalFacet.InternalFacetInstance.Companion.merge
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState
import org.river.exertion.ai.internalSymbol.perceivedSymbols.NonePerceivedSymbol
import java.util.*
import kotlin.math.abs
import kotlin.math.sign

data class SymbolInstance (var symbolObj : IPerceivedSymbol = NonePerceivedSymbol
                           , var displayType : SymbolDisplayType = SymbolDisplayType.PRESENT
                           , var cycles : Float = 0f
                           , var position : Float = 0f
                           , val initTargetPosition: SymbolTargetPosition = SymbolTargetPosition.NONE
                        ) {

    val uuid : UUID = UUID.randomUUID()
    var impact = 0f //used for absent symbols
    var targetPosition = initTargetPosition.targetPosition()

    var deltaCycles = 0f
    var deltaPosition = 0f

    var consumeCapacity = 0f
    var handleCapacity = 0f
    var possessCapacity = 0f

    var ornaments = mutableSetOf<IPerceivedSymbolOrnament>()
    var currentFacetState = mutableSetOf<InternalFacetInstance>()

    fun recalcTargetPosition() {
        //recalc present symbol position to avg of ornament positions
        targetPosition = ornaments.map { it.baseTargetPosition.targetPosition() }.reduce { sum, iPerceivedSymbolOrnament -> sum + iPerceivedSymbolOrnament }.div(ornaments.size)
    }

    fun targetPositionDistance() = abs(targetPosition - position)

    fun invTargetPositionDistance() = 1 - targetPositionDistance()

    fun recalcFacetState() {
        currentFacetState.clear()
        this.ornaments.map { it.facetModifiers }.forEach { facetModifiers ->
            facetModifiers.forEach { facetModifier ->
                currentFacetState.merge(InternalFacetInstance(facetModifier.facetObj, facetModifier.facetMagnitude * invTargetPositionDistance()))
            }
        }
//      TODO: merge ornaments with base facetMod
//        this.symbolObj.facetModifiers.forEach { facetModifier ->
//            currentFacetState.add(facetModifier.facetObj.spawn().apply { this.magnitude = facetModifier.facetMagnitude })
//        }
    }

    fun normalizePosition() {

        this.position += deltaPosition
        this.cycles += deltaCycles

        if (this.displayType == SymbolDisplayType.PRESENT) {
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
        }

        deltaCycles = 0f
        deltaPosition = 0f
    }

    //indirectly update position via modification
    //e.g. this == Hunger, modifyingSymbol == Food
    fun setDeltas(modifyingSymbol : SymbolInstance, modifierType: SymbolModifierType, sourceToTargetRatio : Float) {

        deltaPosition = when (modifierType) {
            SymbolModifierType.POSITION_TO_POSITION ->
                this.symbolObj.baseTargetPosition.targetPosition().sign *
                        modifyingSymbol.symbolObj.baseTargetPosition.targetPosition().sign *
                        sourceToTargetRatio *
                        modifyingSymbol.deltaPosition
            SymbolModifierType.CYCLE_TO_POSITION ->
                this.symbolObj.baseTargetPosition.targetPosition().sign *
                        modifyingSymbol.symbolObj.baseTargetPosition.targetPosition().sign *
                        sourceToTargetRatio *
                        modifyingSymbol.deltaCycles
            else -> 0f
        }

        deltaCycles = when (modifierType) {
            SymbolModifierType.CYCLE_TO_CYCLE ->
                this.symbolObj.baseTargetPosition.targetPosition().sign *
                        modifyingSymbol.symbolObj.baseTargetPosition.targetPosition().sign *
                        sourceToTargetRatio *
                        modifyingSymbol.deltaCycles
            SymbolModifierType.POSITION_TO_CYCLE ->
                this.symbolObj.baseTargetPosition.targetPosition().sign *
                        modifyingSymbol.symbolObj.baseTargetPosition.targetPosition().sign *
                        sourceToTargetRatio *
                        modifyingSymbol.deltaPosition
            else -> 0f
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SymbolInstance

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

}