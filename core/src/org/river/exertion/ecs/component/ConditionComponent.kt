package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ecs.component.action.core.IComponent

class ConditionComponent(var entity : Telegraph) : IComponent, Component, Telegraph {

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.INT_CONDITION.id())
    }

    override val componentName = "Condition"

    var mLife = 1f
    var mLifeRegen = 0.05f

    var mIntAnxiety = .2f
    var mAwake = .6f

    var mTiredness = .2f
    var mExhaustion = .2f
    var mHunger = .2f
    var mThirst = .2f

    // posture enum
//    var isLyingDown : Boolean
//    var isSitting : Boolean
//    var isStanding : Boolean

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_CONDITION.id()) {
                this.mIntAnxiety = msg.extraInfo as Float
            }
        }
        return true
    }

    companion object {
        val mapper = mapperFor<ConditionComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is ConditionComponent } != null
        fun getFor(entity : Entity) : ConditionComponent? = if (has(entity)) entity.components.first { it is ConditionComponent } as ConditionComponent else null

    }

}