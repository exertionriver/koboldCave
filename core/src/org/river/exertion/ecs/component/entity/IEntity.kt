package org.river.exertion.ecs.component.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.IActionComponent

interface IEntity : Telegraph {

    var entityName : String
    var description : String

    val stateMachine : DefaultStateMachine<IEntity, ActionState>

    override fun handleMessage(msg: Telegram?): Boolean = stateMachine.handleMessage(msg)

    fun initialize(initName : String, entity: Entity)

    var actions : MutableList<IActionComponent>

    //tenths of a second
    var moment : Float

    companion object {
        fun has(entity : Entity) : Boolean { return entity.components.firstOrNull{ it is IEntity } != null }
        fun getFor(entity : Entity) : IEntity? = if ( has(entity) ) entity.components.first { it is IEntity } as IEntity else null
    }
}