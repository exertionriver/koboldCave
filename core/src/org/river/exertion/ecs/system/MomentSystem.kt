package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.ai.GdxAI
import ktx.ashley.allOf
import org.river.exertion.ecs.component.MomentComponent
import org.river.exertion.ecs.entity.IEntity

class MomentSystem : IntervalIteratingSystem(allOf(MomentComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        if (MomentComponent.getFor(entity)!!.momentCountdown > 0) MomentComponent.getFor(entity)!!.momentCountdown--

        if (MomentComponent.getFor(entity)!!.ready()) {
            IEntity.getFor(entity)!!.stateMachine.update()
            MomentComponent.getFor(entity)!!.reset()
        }

        GdxAI.getTimepiece().update(this.interval)

//        Gdx.app.log(this.javaClass.name, "${IEntity.getFor(entity)!!.entityName}: ${entity[MomentComponent.mapper]!!.momentCountdown}")
    }
}
