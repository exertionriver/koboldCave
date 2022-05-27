package org.river.exertion.ecs.component.action.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ecs.entity.IEntity

enum class ActionState : State<IEntity> {

    NONE {
        override fun update(entity : IEntity) {
//            Gdx.app.log(this.javaClass.name, "${entity.entityName}: status none")
            entity.stateMachine.changeState(SOME)
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.FEELING_BRIDGE.id(), "feeling very ${entity.stateMachine.currentState}")

        }
    },
    SOME {
        override fun update(entity : IEntity) {
//            Gdx.app.log(this.javaClass.name, "${entity.entityName}: status some")
            entity.stateMachine.changeState(NONE)
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.FEELING_BRIDGE.id(), "feeling very ${entity.stateMachine.currentState}")
        }
    }
    ;

    abstract override fun update(entity : IEntity)

    override fun enter(entity: IEntity?) {
    }

    override fun exit(entity: IEntity?) {
    }

    override fun onMessage(entity: IEntity?, telegram: Telegram?): Boolean {

        if (telegram != null && telegram.message == MessageChannel.ECS_FSM_BRIDGE.id() ) {

            Gdx.app.log(this.javaClass.name, "received telegram from ${entity!!.entityName}: ${telegram.sender}, ${telegram.message}, ${telegram.extraInfo}")

            return true
        }
        return false
    }

}
