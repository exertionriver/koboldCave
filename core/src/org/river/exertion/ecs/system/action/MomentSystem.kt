package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.GdxAI
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.entity.IEntity
import org.river.exertion.ecs.component.entity.location.ILocation

class MomentSystem : IntervalIteratingSystem(allOf(MomentComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        if (MomentComponent.getFor(entity)!!.momentCountdown > 0) MomentComponent.getFor(entity)!!.momentCountdown--

        if (MomentComponent.getFor(entity)!!.ready()) {
            IEntity.getFor(entity)!!.stateMachine.update()
            MomentComponent.getFor(entity)!!.reset()
        }

        GdxAI.getTimepiece().update(1/10f)

//        Gdx.app.log(this.javaClass.name, "${IEntity.getFor(entity)!!.entityName}: ${entity[MomentComponent.mapper]!!.momentCountdown}")
    }
}
