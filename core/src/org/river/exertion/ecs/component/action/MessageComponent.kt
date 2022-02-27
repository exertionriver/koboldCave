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
import org.river.exertion.s2d.IBaseActor

class MessageComponent(base : Boolean = false)  : IActionComponent, Component, Telegraph {

    override val label = "Watch"
    override val description = { "Watch" }
    override var type = if (base) ActionType.Continual else ActionNoneComponent.type
    override var priority = ActionNoneComponent.priority
    override var state = if (base) ActionState.ActionQueue else ActionState.ActionStateNone

    override var plexSlotsFilled = ActionNoneComponent.plexSlotsFilled
    override var plexSlotsRequired = ActionNoneComponent.plexSlotsRequired
    override var maxParallel = ActionNoneComponent.maxParallel

    override val momentsToPrepare = ActionNoneComponent.momentsToPrepare
    override val momentsToExecute = ActionNoneComponent.momentsToExecute
    override val momentsToRecover = ActionNoneComponent.momentsToRecover

    //in moments
    override var stateCountdown = 0
    override var executed = false

    lateinit var name : String

    init {
        MessageManager.getInstance().addListener(this, MessageIds.S2D_ECS_BRIDGE.id())
    }

    companion object {
        val mapper = mapperFor<MessageComponent>()
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( msg != null && (msg.sender as IBaseActor).actorName == name) {
            Gdx.app.log("message","entity $name received telegram:${msg.message}, ${(msg.sender as IBaseActor).actorName}, ${msg.extraInfo}")

            return true
        }

        return false
    }

}