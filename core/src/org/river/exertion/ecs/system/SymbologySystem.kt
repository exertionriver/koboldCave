package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ktx.ashley.allOf
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.internalSymbol.perceivedSymbols.AnxietySymbol
import org.river.exertion.ai.internalSymbol.perceivedSymbols.MomentElapseSymbol
import org.river.exertion.ai.messaging.SymbolMessage
import org.river.exertion.ai.messaging.TimingTableMessage
import org.river.exertion.ecs.component.ConditionComponent
import org.river.exertion.ecs.component.MomentComponent
import org.river.exertion.ecs.component.SymbologyComponent
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.ecs.entity.character.ICharacter
import org.river.exertion.s2d.ui.UITimingTable

class SymbologySystem : IntervalIteratingSystem(allOf(SymbologyComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        //clear circularity check for moment
        SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.circularity.clear()

        val entityMomentDelta = (-MomentComponent.getFor(entity)!!.systemMoment * this.interval) / ICharacter.getFor(entity)!!.moment //moment is in tenths of a second

        val entityMomentSymbolInstance = SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolDisplay.firstOrNull { it.symbolObj == MomentElapseSymbol }

        if (entityMomentSymbolInstance != null)
            SymbolModifyAction.executeImmediate(IEntity.getFor(entity)!!, SymbolMessage(symbolInstance = entityMomentSymbolInstance.apply { this.deltaPosition = entityMomentDelta }))

        //clear circularity check for anxiety
        SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.circularity.clear()

        val entityAnxietySymbolInstance = SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolDisplay.firstOrNull { it.symbolObj == AnxietySymbol }

        if (entityAnxietySymbolInstance != null) {
            val entityAnxietyDelta = ConditionComponent.getFor(entity)!!.mIntAnxiety - entityAnxietySymbolInstance.position

            SymbolModifyAction.executeImmediate(IEntity.getFor(entity)!!, SymbolMessage(symbolInstance = entityAnxietySymbolInstance.apply { this.deltaPosition = entityAnxietyDelta }))
        }

        //rebuild plans from state of internalSymbolDisplay
        SymbologyComponent.getFor(entity)!!.internalSymbology.internalFocusDisplay.rebuildPlans(SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay, entityMomentDelta)

        UITimingTable.send(timingTableMessage = TimingTableMessage(label = "symbolSystem", value = interval))
        UITimingTable.send(timingTableMessage = TimingTableMessage(label = "${ICharacter.getFor(entity)!!.entityName} moment delta", value = entityMomentDelta))

    }
}
