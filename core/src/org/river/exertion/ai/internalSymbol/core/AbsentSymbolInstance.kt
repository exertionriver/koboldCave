package org.river.exertion.ai.internalSymbol.core

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.perceivedSymbols.NonePerceivedSymbol

data class AbsentSymbolInstance (var symbolObj : IPerceivedSymbol = NonePerceivedSymbol, var cycles : Float = 0f, var position : Float = 0f, var impact : Float = 0f) {

    var ornaments = mutableSetOf<AbsentSymbolInstance>()

    fun normalizePosition(deltaPosition : Float) {

        this.position += deltaPosition

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

    //indirectly update position via impactor
    fun updatePosition(entity : Telegraph, deltaPosition : Float) {
        normalizePosition(deltaPosition)

        var position = 0f
        var impact = 0f
/*
        //if these modified and update symbols are magnetism-aligned, spawn 'absent' symbolinstance if not already spawned
        this.symbolObj.absentModifiers.forEach { absentImpactor ->
            val modifierRatio = absentImpactor.modifierRatio
            val impactorRatio = absentImpactor.impactorRatio

            val presentSymbolInstance = symbolsPresent.firstOrNull { it.symbolObj == absentImpactor.modifyingSymbol }

            if (presentSymbolInstance != null) {
                position += if (modifierRatio == 0f) 0f else
                    if (this.symbolObj.targetMagnetism == SymbolMagnetism.ATTRACT_CONSUME)
                        (presentSymbolInstance.symbolObj.targetMagnetism.targetPosition() - presentSymbolInstance.position) / modifierRatio
                    else
                        -(presentSymbolInstance.symbolObj.targetMagnetism.targetPosition() - presentSymbolInstance.position) / modifierRatio
                impact += if (impactorRatio == 0f) 0f else
                    if (this.symbolObj.targetMagnetism == SymbolMagnetism.ATTRACT_CONSUME)
                        (presentSymbolInstance.symbolObj.targetMagnetism.targetPosition() - presentSymbolInstance.position) * impactorRatio
                    else
                        -(presentSymbolInstance.symbolObj.targetMagnetism.targetPosition() - presentSymbolInstance.position) * impactorRatio

            }
        }
*/
        this.position = position
        this.impact = impact
    }
}