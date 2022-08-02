package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ktx.ashley.allOf
import org.river.exertion.ai.internalSymbol.core.SymbolActionType
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.internalSymbol.perceivedSymbols.AnxietySymbol
import org.river.exertion.ai.internalSymbol.perceivedSymbols.MomentElapseSymbol
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage
import org.river.exertion.ai.messaging.TimingTableMessage
import org.river.exertion.ecs.component.ConditionComponent
import org.river.exertion.ecs.component.MomentComponent
import org.river.exertion.ecs.component.SymbologyComponent
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.ecs.entity.character.ICharacter

class SymbologySystem : IntervalIteratingSystem(allOf(SymbologyComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        //clear circularity check for moment
        SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.circularity.clear()

        val entityMomentDelta = (-MomentComponent.getFor(entity)!!.systemMoment * this.interval) / ICharacter.getFor(entity)!!.moment //moment is in tenths of a second

        val entityMomentSymbolInstance = SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolDisplay.firstOrNull { it.symbolObj == MomentElapseSymbol }

        if (entityMomentSymbolInstance != null)
            MessageChannel.INT_SYMBOL_MODIFY.send(IEntity.getFor(entity)!!, SymbolMessage(symbolInstance = entityMomentSymbolInstance.apply { this.deltaPosition = entityMomentDelta }))

        //clear circularity check for anxiety
        SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.circularity.clear()

        val entityAnxietySymbolInstance = SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolDisplay.firstOrNull { it.symbolObj == AnxietySymbol }

        if (entityAnxietySymbolInstance != null) {
            val entityAnxietyDelta = ConditionComponent.getFor(entity)!!.internalCondition.mIntAnxiety - entityAnxietySymbolInstance.position

            MessageChannel.INT_SYMBOL_MODIFY.send(IEntity.getFor(entity)!!, SymbolMessage(symbolInstance = entityAnxietySymbolInstance.apply { this.deltaPosition = entityAnxietyDelta }))
        }

        //spawn / despawn for any remaining symbols
        SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.symbolDisplay.filter { it.displayType == SymbolDisplayType.PRESENT }.forEach { symbolInstance ->
            symbolInstance.symbolObj.symbolActions.filter { it.symbolActionType == SymbolActionType.SPAWN || it.symbolActionType == SymbolActionType.DESPAWN }.forEach {
                it.execute(IEntity.getFor(entity)!!, SymbolMessage(symbolInstance = symbolInstance))
            }
        }

        //rebuild plans from state of internalSymbolDisplay
        SymbologyComponent.getFor(entity)!!.internalSymbology.internalFocusDisplay.rebuildPlans(SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay, entityMomentDelta)

        SymbologyComponent.getFor(entity)!!.internalSymbology.internalSymbolDisplay.mergeAndUpdateFacets()

        MessageChannel.UI_TIMING_DISPLAY.send(null, TimingTableMessage(label = "symbolSystem", value = interval))
        MessageChannel.UI_TIMING_DISPLAY.send(null, TimingTableMessage(label = "${ICharacter.getFor(entity)!!.entityName} moment delta", value = entityMomentDelta))

    }
}
