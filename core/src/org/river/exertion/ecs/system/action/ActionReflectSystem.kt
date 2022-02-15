package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.ecs.component.action.ActionReflectComponent
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEntityComponent
import org.river.exertion.isEntity

class ActionReflectSystem : IteratingSystem(allOf(ActionReflectComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( ActionPlexSystem.readyToExecute(entity, ActionReflectComponent.mapper) && entity.isEntity() ) {
        //    println ("entity ${entity.getEntityComponent().name} thinks things over for a moment..")

            entity[ActionReflectComponent.mapper]!!.executed = true
        }
    }
}
