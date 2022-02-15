package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.ecs.component.action.ActionIdleComponent
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEntityComponent
import org.river.exertion.isEntity

class ActionIdleSystem : IteratingSystem(allOf(ActionIdleComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( ActionPlexSystem.readyToExecute(entity, ActionIdleComponent.mapper) && entity.isEntity() ) {
          //  println ("entity ${entity.getEntityComponent().name} putters around for a bit..")

            entity[ActionIdleComponent.mapper]!!.executed = true
        }
    }
}
