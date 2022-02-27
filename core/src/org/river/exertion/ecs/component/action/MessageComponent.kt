package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
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
import org.river.exertion.ecs.component.entity.core.EntityNone
import org.river.exertion.s2d.IBaseActor

class MessageComponent(val initName : String) : IActionComponent, Component, Telegraph {

    override val componentName = "MessageComponent"
    var entityName = initName

    init {
        MessageManager.getInstance().addListener(this, MessageIds.S2D_ECS_BRIDGE.id())
    }

    companion object {
        val mapper = mapperFor<MessageComponent>()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( msg != null && (msg.sender as IBaseActor).actorName == entityName) {
            Gdx.app.log("message","entity $entityName received telegram:${msg.message}, ${(msg.sender as IBaseActor).actorName}, ${msg.extraInfo}")

            return true
        }

        return false
    }

}