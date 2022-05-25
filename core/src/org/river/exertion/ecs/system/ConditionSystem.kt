package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.ai.msg.MessageManager
import ktx.ashley.allOf
import org.river.exertion.MessageIds
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.ecs.component.ConditionComponent
import org.river.exertion.ecs.component.ManifestComponent
import org.river.exertion.ecs.entity.IEntity
import kotlin.random.Random

class ConditionSystem : IntervalIteratingSystem(allOf(ConditionComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        val delta = 1/10f

        //random jostling around
        val changeMIntAnxietyBy = (Random.nextInt(5) - 2) / 1000f

        ConditionComponent.getFor(entity)!!.mIntAnxiety += changeMIntAnxietyBy

        if (ConditionComponent.getFor(entity)!!.mIntAnxiety < 0) ConditionComponent.getFor(entity)!!.mIntAnxiety = 0f

        if (ConditionComponent.getFor(entity)!!.mIntAnxiety > 1) ConditionComponent.getFor(entity)!!.mIntAnxiety = 1f

        val mIntAnxiety = ConditionComponent.getFor(entity)!!.mIntAnxiety

        MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity), MessageIds.INT_FACET.id(), mIntAnxiety)

    }
}
