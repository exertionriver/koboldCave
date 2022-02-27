package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.MessageIds
import org.river.exertion.ecs.component.action.core.ActionNoneComponent
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.ActionType
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.entity.core.IEntity
import org.river.exertion.ecs.component.environment.core.IEnvironment
import org.river.exertion.s2d.IBaseActor

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