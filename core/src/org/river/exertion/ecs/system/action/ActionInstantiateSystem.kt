package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.ashley.*
import org.river.exertion.assets.Kobold
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.entity.EntityKoboldComponent
import org.river.exertion.ecs.component.entity.core.IEntityComponent
import org.river.exertion.ecs.component.environment.EnvironmentCaveComponent
import org.river.exertion.ecs.component.environment.core.IEnvironmentComponent
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEntityComponent
import org.river.exertion.getEnvironmentComponent
import org.river.exertion.isEntity
import org.river.exertion.isEnvironment
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.with

class ActionInstantiateSystem : IteratingSystem(allOf(ActionInstantiateComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {

        if ( ActionPlexSystem.readyToExecute(entity, ActionInstantiateComponent.mapper) && entity.isEnvironment() ) {

            //max three entities spawning for now
            if ( engine.entities.filter { it.isEntity() }.count() < 3) {
                val newKobold = EntityKoboldComponent.instantiate(this.engine as PooledEngine)

                if (entity.getEnvironmentComponent().nodeRoom.nodes.isNotEmpty()) {
                    newKobold.getEntityComponent().currentNodeRoom = entity.getEnvironmentComponent().nodeRoom
                    newKobold.getEntityComponent().currentNode = entity.getEnvironmentComponent().nodeRoom.getRandomNode()
                    newKobold.getEntityComponent().currentAngle = entity.getEnvironmentComponent().nodeRoom.getRandomNextNodeAngle(newKobold.getEntityComponent().currentNode)
                }

                println ("entity ${newKobold.getEntityComponent().name} instantiated at ${newKobold.getEntityComponent().currentNode}, pointing ${newKobold.getEntityComponent().currentAngle}..!")

                entity[ActionInstantiateComponent.mapper]!!.executed = true
            }
        }
    }
}
