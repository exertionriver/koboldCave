package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.msg.MessageManager
import ktx.ashley.allOf
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ecs.component.action.LookActionComponent
import org.river.exertion.ecs.component.MomentComponent
import org.river.exertion.ecs.entity.IEntity

class LookActionSystem : IteratingSystem(allOf(LookActionComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        var lookDigest = ""

        if ( IEntity.has(entity) && MomentComponent.has(entity) ) {
         //   entity[MomentComponent.mapper]!!.reset(this.javaClass.name)

            engine.entities.filter { IEntity.has(it) }.forEach {

                //for now, look is external--entity cannot see themselves
                if (entity != it) {
                    lookDigest += IEntity.getFor(it)!!.entityName + ", "
                }
            }
            val lookReport = if (lookDigest.isNotEmpty()) "sees $lookDigest" else "sees nothing"

            MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity)!!, MessageChannel.PERCEPTION_BRIDGE.id(), lookReport)
        }
    }
}
