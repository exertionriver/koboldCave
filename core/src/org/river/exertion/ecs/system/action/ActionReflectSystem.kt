package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.contains
import ktx.ashley.get
import org.river.exertion.ecs.component.action.ActionReflectComponent
import org.river.exertion.ecs.component.entity.EntityKoboldComponent
import org.river.exertion.ecs.system.action.core.ActionPlexSystem

class ActionReflectSystem : IteratingSystem(allOf(ActionReflectComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( ActionPlexSystem.readyToExecute(entity, ActionReflectComponent.mapper) && entity.contains(EntityKoboldComponent.mapper) )
            entity[EntityKoboldComponent.mapper]?.let {
                println ("entity ${it.name} thinks things over for a moment..")
            }
    }
}
