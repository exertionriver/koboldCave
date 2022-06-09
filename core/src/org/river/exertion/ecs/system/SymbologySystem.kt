package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.ai.msg.MessageManager
import ktx.ashley.allOf
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.internalSymbol.perceivedSymbols.MomentElapseSymbol
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolActionMessage
import org.river.exertion.ai.messaging.SymbolMessage
import org.river.exertion.ecs.component.SymbologyComponent
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.ecs.entity.character.ICharacter

class SymbologySystem : IntervalIteratingSystem(allOf(SymbologyComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        val entityMomentDelta = (-10f * this.interval) / ICharacter.getFor(entity)!!.moment //moment is in tenths of a second

        val entityMomentSymbolInstance = SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolDisplay.firstOrNull { it.symbolObj == MomentElapseSymbol }

        if (entityMomentSymbolInstance != null)
            SymbolModifyAction.executeImmediate(IEntity.getFor(entity)!!, SymbolMessage(symbolInstance = entityMomentSymbolInstance.apply { this.deltaPosition = entityMomentDelta }))
/*
        //add new plan for absent symbol, if needed
        SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolsAbsent.symbolDisplay.filter { it.symbolObj.mitigators.isNotEmpty() }.forEach { absentSymbolInstance ->
            val satisfier = absentSymbolInstance.symbolObj.mitigators.firstOrNull() //todo: order desc by success rate
            val activePlansForAbsentSymbol = SymbologyComponent.getFor(entity)!!.internalSymbology.internalFocusDisplay.focusPlansPresent.filter { it.absentSymbolInstance == absentSymbolInstance }

            //add absentSymbolInstance satisfier chain to focusesPresent if not already added
            if ( (satisfier != null) && (activePlansForAbsentSymbol.isEmpty()) )
                MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity), MessageChannel.INT_ADD_FOCUS_PLAN.id(), SymbolActionMessage(absentSymbolInstance))
        }

        //execute active plans, removing satisfied last chain links, adding new last chain links, as needed
        SymbologyComponent.getFor(entity)!!.internalSymbology.internalFocusDisplay.focusPlansPresent.sortedByDescending { it.absentSymbolInstance.impact }.forEach { focusPlan ->
            val satisfySymbol = SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolsPresent.sortedBy { it.position }.firstOrNull { it.symbolObj == focusPlan.absentSymbolInstance.symbolObj }

            if ( (satisfySymbol != null) && focusPlan.instancesChain.isNotEmpty() ) {
                var linkSatisfied = true
                var revIdx = focusPlan.instancesChain.size - 1
                var focusPlanSizeDelta = 0

                while (revIdx >= 0 && linkSatisfied) {
                    if (focusPlan.instancesChain[revIdx].internalFocusObj.satisfyingCondition(satisfySymbol)) {
                        focusPlan.instancesChain[revIdx].internalFocusObj.satisfyingResult(IEntity.getFor(entity)!!, satisfySymbol)

                        MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity), MessageChannel.INT_REMOVE_FOCUS_CHAIN_LINK.id(), SymbolActionMessage(focusPlan.absentSymbolInstance, deltaPosition = revIdx.toFloat()))
                        focusPlanSizeDelta--
                        revIdx--
                    } else linkSatisfied = false
                }

                if ((focusPlanSizeDelta == 0) || (revIdx < 0) )
                    MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity), MessageChannel.INT_ADD_FOCUS_CHAIN_LINK.id(), SymbolActionMessage(focusPlan.absentSymbolInstance, deltaPosition = 0f))
            }

            if (!SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolsAbsent.map { it.symbolObj }.contains(focusPlan.absentSymbolInstance.symbolObj) )
                MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity), MessageChannel.INT_REMOVE_FOCUS_PLAN.id(), SymbolActionMessage(focusPlan.absentSymbolInstance))
        }*/
    }
}
