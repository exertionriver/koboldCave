package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.ashley.*
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.entity.EntityKoboldComponent
import org.river.exertion.ecs.component.environment.EnvironmentCaveComponent
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.with

class ActionInstantiateSystem : IteratingSystem(allOf(ActionInstantiateComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( ActionPlexSystem.readyToExecute(entity, ActionInstantiateComponent.mapper) && entity.contains(EnvironmentCaveComponent.mapper) ) {
            val entityName = "krazza" + Random()

            engine.entity {
                with<EntityKoboldComponent>()
            }.apply { this[EntityKoboldComponent.mapper]?.instantiate(entityName, this) }

            println ("entity $entityName instantiated..!")
        }
    }
}
