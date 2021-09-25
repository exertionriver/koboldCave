package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.ashley.allOf
import ktx.ashley.contains
import ktx.ashley.get
import org.river.exertion.ecs.component.action.ActionIdleComponent
import org.river.exertion.ecs.component.action.ActionLookComponent
import org.river.exertion.ecs.component.action.ActionReflectComponent
import org.river.exertion.ecs.component.entity.EntityKoboldComponent
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
class ActionReflectSystem : IteratingSystem(allOf(ActionReflectComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        engine.entities.filter { it.contains(EntityKoboldComponent.mapper) }.forEach { koboldEntity ->
            if (entity != koboldEntity) {
                koboldEntity[EntityKoboldComponent.mapper]?.let {
                    println ("entity ${it.name} thinks things over for a moment..")
                }
            }
        }
    }
}
