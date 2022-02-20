package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.*
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.entity.EntityKobold
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEntityComponent
import org.river.exertion.getEnvironmentComponent
import org.river.exertion.isEntity
import org.river.exertion.isEnvironment

class ActionInstantiateSystem : IteratingSystem(allOf(ActionInstantiateComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {

        if ( ActionPlexSystem.readyToExecute(entity, ActionInstantiateComponent.mapper) && entity.isEnvironment() ) {

            //max three entities spawning for now
            if ( engine.entities.filter { it.isEntity() }.count() < 3) {
                val newKobold = EntityKobold.instantiate(this.engine as PooledEngine, entity[ActionInstantiateComponent.mapper]!!.stage, cave = entity)

                entity[ActionInstantiateComponent.mapper]!!.executed = true
            }
        }
    }
}
