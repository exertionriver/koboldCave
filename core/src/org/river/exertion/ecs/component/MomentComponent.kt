package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.IComponent

class MomentComponent(initMoment : Float = 10f) : IComponent, Component {

    override val componentName = "Moment"

    //tenths of a second
    var moment = initMoment
    var momentCountdown = initMoment

    var systemMoment = 10f //for simulation

    fun ready() = momentCountdown <= 0
    fun reset() { momentCountdown = moment }

    companion object {
        val mapper = mapperFor<MomentComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is MomentComponent } != null
        fun getFor(entity : Entity) : MomentComponent? = if (has(entity)) entity.components.first { it is MomentComponent } as MomentComponent else null

    }

}