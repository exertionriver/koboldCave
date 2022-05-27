package org.river.exertion.ecs.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.noumena.core.NoumenonInstance
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.IComponent
import org.river.exertion.s2d.actor.IBaseActor

interface IEntity : Telegraph {

    var entityName : String
    var noumenonInstance : NoumenonInstance

    val stateMachine : DefaultStateMachine<IEntity, ActionState>

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( msg != null ) {
            if (msg.message == MessageChannel.S2D_ECS_BRIDGE.id() && (msg.sender as IBaseActor).actorName == entityName) {
//            Gdx.app.log("message","entity $entityName received telegram:${msg.message}, ${(msg.sender as IBaseActor).actorName}, ${msg.extraInfo}")
                return true
            } else if (msg.message == MessageChannel.ECS_FSM_BRIDGE.id()) {
                stateMachine.handleMessage(msg)
            }
        }
        return false
    }

    fun initialize(initName : String, entity: Entity)

    var actions : MutableList<IComponent>

    //tenths of a second
    var moment : Float

    companion object {
        fun has(entity : Entity) : Boolean { return entity.components.firstOrNull{ it is IEntity } != null }
        fun getFor(entity : Entity) : IEntity? = if ( has(entity) ) entity.components.first { it is IEntity } as IEntity else null
    }
}