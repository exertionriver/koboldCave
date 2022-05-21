package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.MessageIds
import org.river.exertion.ecs.component.action.core.IComponent

class ConditionComponent(var entity : Telegraph) : IComponent, Component, Telegraph {

    override val componentName = "Condition"

    var mLife = 1f
    var mLifeRegen = 0.05f

    var mIntAnxiety = .2f
    var mAwake = .6f

    var mTiredness = .2f
    var mExhaustion = .2f
    var mHunger = .2f
    var mThirst = .2f

    //out of 20
    var aIntelligence : Float = 10f
    var aWisdom : Float = 10f

    // posture enum
//    var isLyingDown : Boolean
//    var isSitting : Boolean
//    var isStanding : Boolean

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.receiver == entity) ) {
            if (msg.message == MessageIds.INT_CONDITION.id()) {
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