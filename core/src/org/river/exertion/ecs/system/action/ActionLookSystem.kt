package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.ashley.allOf
import ktx.ashley.contains
import ktx.ashley.get
import org.river.exertion.ecs.component.action.ActionLookComponent
import org.river.exertion.ecs.component.action.core.ActionPlexComponent
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.entity.EntityKoboldComponent
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import kotlin.time.ExperimentalTime

class ActionLookSystem : IteratingSystem(allOf(ActionLookComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        var lookDigest = ""
        engine.entities.filter { checkEntity -> ActionPlexSystem.readyToExecute(checkEntity, ActionLookComponent.mapper) }.forEach { lookEntity ->

            //for now, look is external--entity cannot see themselves
            if (entity != lookEntity) {
                lookEntity[EntityKoboldComponent.mapper]?.let {
                    lookDigest += it.description + ", "
                }
            }
        }
        if (lookDigest.isNotEmpty()) println ("entity ${entity[EntityKoboldComponent.mapper]?.name} sees $lookDigest")
    }
}
