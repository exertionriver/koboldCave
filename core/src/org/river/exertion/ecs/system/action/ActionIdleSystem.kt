package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.ashley.allOf
import ktx.ashley.contains
import ktx.ashley.get
import org.river.exertion.ecs.component.action.ActionIdleComponent
import org.river.exertion.ecs.component.action.ActionInstantiateComponent
import org.river.exertion.ecs.component.action.ActionLookComponent
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.entity.EntityKoboldComponent
import org.river.exertion.ecs.component.entity.core.IEntityComponent
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEntityComponent
import org.river.exertion.isEntity
import kotlin.time.ExperimentalTime

class ActionIdleSystem : IteratingSystem(allOf(ActionIdleComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( ActionPlexSystem.readyToExecute(entity, ActionIdleComponent.mapper) && entity.isEntity() ) {
            println ("entity ${entity.getEntityComponent().name} putters around for a bit..")

            entity[ActionIdleComponent.mapper]!!.executed = true
        }
    }
}
