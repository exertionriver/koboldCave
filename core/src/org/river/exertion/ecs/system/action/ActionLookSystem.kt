package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.ashley.allOf
import ktx.ashley.contains
import ktx.ashley.get
import org.river.exertion.ecs.component.action.ActionLookComponent
import org.river.exertion.ecs.component.entity.EntityKoboldComponent
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
class ActionLookSystem : IteratingSystem(allOf(ActionLookComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        var lookDigest = ""
        engine.entities.filter { it.contains(EntityKoboldComponent.mapper) }.forEach { koboldEntity ->
            if (entity != koboldEntity) {
                koboldEntity[EntityKoboldComponent.mapper]?.let {
                    lookDigest += it.description + ", "
                }
            }
        }
        println ("entity ${entity[EntityKoboldComponent.mapper]?.name} sees $lookDigest")
    }
}
