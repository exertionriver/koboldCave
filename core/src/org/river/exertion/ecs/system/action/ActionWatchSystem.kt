package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEntityComponent
import org.river.exertion.isEntity

class ActionWatchSystem : IteratingSystem(allOf(ActionWatchComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( ActionPlexSystem.readyToExecute(entity, ActionWatchComponent.mapper) && entity.isEntity() ) {
      //      println ("entity ${entity.getEntityComponent().name} watches..")

            entity[ActionWatchComponent.mapper]!!.executed = true
        }
    }
}
