package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.ai.msg.MessageManager
import ktx.ashley.allOf
import org.river.exertion.ai.internalSymbol.perceivedSymbols.MomentElapseSymbol
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.PresentSymbolMessage
import org.river.exertion.ecs.component.SymbologyComponent
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.ecs.entity.character.ICharacter

class SymbologySystem : IntervalIteratingSystem(allOf(SymbologyComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        val entityMomentDelta = (10f * this.interval) / ICharacter.getFor(entity)!!.moment //moment is in tenths of a second

        val entityMomentSymbol = SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolsPresent.firstOrNull { it.symbolObj == MomentElapseSymbol }

        if (entityMomentSymbol != null)
            MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity), MessageChannel.INT_PRESENT_SYMBOL_MODIFY.id(), PresentSymbolMessage(entityMomentSymbol, -entityMomentDelta))

        /*
        //add new plan
        SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolsAbsent.filter { it.symbolObj.satisfiers.isNotEmpty() }.forEach { absentSymbolInstance ->
            val satisfier = absentSymbolInstance.symbolObj.satisfiers.firstOrNull()

            //add absentSymbolInstance satisfier chain to focusesPresent if not already added
            if ( (satisfier != null) && (!SymbologyComponent.getFor(entity)!!.internalSymbology.internalFocusDisplay.focusPlansPresent.map { it.absentSymbolInstance }.contains(absentSymbolInstance) ) )
                SymbologyComponent.getFor(entity)!!.internalSymbology.internalFocusDisplay.focusPlansPresent.add(InternalFocusPlan(absentSymbolInstance).apply { this.init(SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolsPresent) } )
        }

        //execute plans
        SymbologyComponent.getFor(entity)!!.internalSymbology.internalFocusDisplay.focusPlansPresent.sortedByDescending { it.absentSymbolInstance.impact }.forEach { focusPlan ->
            val satisfySymbol = SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolsPresent.sortedBy { it.position }.firstOrNull { it.symbolObj == focusPlan.absentSymbolInstance.symbolObj }

            if ( (satisfySymbol != null) && focusPlan.instancesChain.isNotEmpty() ) {
                focusPlan.instancesChain.asReversed().forEach { internalFocusInstance ->
                    if (internalFocusInstance.internalFocusObj.satisfyingCondition(satisfySymbol) ) {
                        //update symbol
                        SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.update(mutableSetOf(internalFocusInstance.internalFocusObj.satisfyingResult(satisfySymbol) ) )
                    }
                }
                if (!SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolsAbsent.map { it.symbolObj }.contains(focusPlan.absentSymbolInstance.symbolObj) )
                    SymbologyComponent.getFor(entity)!!.internalSymbology.internalFocusDisplay.focusPlansPresent.remove(focusPlan)
                else
                    focusPlan.update(SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolsPresent)
            }
        }*/
    }
}
