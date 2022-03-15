package org.river.exertion.s2d.actor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.MessageIds
import org.river.exertion.Point
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.entity.IEntity

interface IBaseActor : Telegraph {

    var actorName : String
    var currentPosition : Point
    var currentAngle : Float

//    fun init() {
//        MessageManager.getInstance().addListener(this, MessageIds.ECS_S2D_BRIDGE.id())
//    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null && (msg.sender as IEntity).entityName == actorName) {
 //           Gdx.app.log("message","actor $actorName received telegram:${msg.message}, ${(msg.sender as MessageComponent).entityName}, ${msg.extraInfo}")

            if (msg.extraInfo != null && msg.extraInfo is ActionMoveComponent) {
 //               Gdx.app.log("message","update currentPosition to: ${(msg.extraInfo as ActionMoveComponent).currentPosition}, currentAngle to: ${(msg.extraInfo as ActionMoveComponent).currentAngle}")

                currentPosition = (msg.extraInfo as ActionMoveComponent).currentPosition
                currentAngle = (msg.extraInfo as ActionMoveComponent).currentAngle
            }

            MessageManager.getInstance().dispatchMessage(this, MessageIds.S2D_ECS_BRIDGE.id())

            return true
        }

        return false
    }
}