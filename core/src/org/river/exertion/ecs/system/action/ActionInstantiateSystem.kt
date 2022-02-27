package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.*
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.entity.EntityKobold
import org.river.exertion.ecs.component.entity.core.IEntity
import org.river.exertion.ecs.component.environment.core.IEnvironment

class ActionInstantiateSystem : IteratingSystem(allOf(ActionInstantiateComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {

        if ( IEnvironment.has(entity) && MomentComponent.has(entity) && entity[MomentComponent.mapper]!!.ready()) {
            entity[MomentComponent.mapper]!!.reset(this.javaClass.name)

            //max three entities spawning for now
            if ( engine.entities.filter { IEntity.has(it) }.count() < 3) {
                EntityKobold.instantiate(this.engine as PooledEngine, entity[ActionInstantiateComponent.mapper]!!.stage, cave = entity)
            }
        }
    }
}
