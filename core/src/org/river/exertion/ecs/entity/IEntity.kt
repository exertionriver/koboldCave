package org.river.exertion.ecs.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.MessageIds
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.btree.v0_1.IBTCharacter
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.IComponent
import org.river.exertion.s2d.actor.IBaseActor

interface IEntity : Telegraph {

    var entityName : String
    var description : String

    val stateMachine : DefaultStateMachine<IEntity, ActionState>

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( msg != null ) {
            if (msg.message == MessageIds.S2D_ECS_BRIDGE.id() && (msg.sender as IBaseActor).actorName == entityName) {
//            Gdx.app.log("message","entity $entityName received telegram:${msg.message}, ${(msg.sender as IBaseActor).actorName}, ${msg.extraInfo}")
                return true
            } else if (msg.message == MessageIds.ECS_FSM_BRIDGE.id()) {
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