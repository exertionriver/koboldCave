package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.IActionComponent

class MomentComponent(val initMoment : Float = 10f) : IActionComponent, Component {

    override val componentName = "MomentComponent"

    //tenths of a second
    var moment = initMoment
    var momentCountdown = initMoment

    fun ready() = momentCountdown <= 0
    fun reset(systemName : String) {
        momentCountdown = moment

        Gdx.app.log(systemName, "resets the countdown..!")
    }

    companion object {
        val mapper = mapperFor<MomentComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is MomentComponent } != null
    }

}