package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.ai.msg.MessageManager
import ktx.ashley.allOf
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ecs.component.ConditionComponent
import org.river.exertion.ecs.entity.IEntity
import kotlin.random.Random

class ConditionSystem : IntervalIteratingSystem(allOf(ConditionComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        val delta = 1/10f

        var mIntAnxiety = ConditionComponent.getFor(entity)!!.mIntAnxiety

        //random jostling around
        val changeMIntAnxietyBy = (Random.nextInt(5) - 2) / 1000f

        mIntAnxiety += changeMIntAnxietyBy

        if (mIntAnxiety < 0) mIntAnxiety = 0f
        if (mIntAnxiety > 1) mIntAnxiety = 1f

        //updates ConditionComponent and Arising Facet
        MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity), MessageChannel.INT_CONDITION.id(), mIntAnxiety)

    }
}
