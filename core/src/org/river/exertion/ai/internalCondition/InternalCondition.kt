package org.river.exertion.ai.internalCondition

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.messaging.MessageChannel

class InternalCondition(val entity : Telegraph) : Telegraph {

    init {
        MessageChannel.INT_CONDITION.enableReceive(this)
    }

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
}