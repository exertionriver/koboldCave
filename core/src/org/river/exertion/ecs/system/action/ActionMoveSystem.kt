package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.ashley.allOf
import ktx.ashley.contains
import ktx.ashley.get
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.entity.EntityKoboldComponent
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import kotlin.time.ExperimentalTime

class ActionMoveSystem : IteratingSystem(allOf(ActionMoveComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( ActionPlexSystem.readyToExecute(entity, ActionMoveComponent.mapper) && entity.contains(EntityKoboldComponent.mapper) )
            entity[EntityKoboldComponent.mapper]?.let {
                println ("entity ${it.name} moves to ${entity[ActionMoveComponent.mapper]?.currentPosition}.")
            }
    }
}
