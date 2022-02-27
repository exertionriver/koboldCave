package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.Gdx
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.entity.core.IEntity
import org.river.exertion.ecs.component.environment.core.IEnvironment

class MomentSystem : IntervalIteratingSystem(allOf(MomentComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        if (entity[MomentComponent.mapper]!!.momentCountdown > 0) entity[MomentComponent.mapper]!!.momentCountdown--

        if (IEntity.has(entity))
            Gdx.app.log(this.javaClass.name, "${IEntity.getFor(entity)!!.entityName}: ${entity[MomentComponent.mapper]!!.momentCountdown}")
        else if (IEnvironment.has(entity))
            Gdx.app.log(this.javaClass.name, "${IEnvironment.getFor(entity)!!.environmentName}: ${entity[MomentComponent.mapper]!!.momentCountdown}")
    }
}
