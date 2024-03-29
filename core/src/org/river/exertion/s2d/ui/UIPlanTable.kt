package org.river.exertion.s2d.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ecs.entity.IEntity

class UIPlanTable(initSkin : Skin) : Table(), Telegraph {

    val tableMax = 5

    val register = mutableMapOf<String, String>()

    init {
        MessageChannel.PLAN_BRIDGE.enableReceive(this)

        skin = initSkin
        x = Gdx.graphics.width / 8f
        y = 4 * Gdx.graphics.height / 8f
        name = "planTable"
//        this.debug = true
        this.add(Label("planTable", initSkin) )
        this.row()
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
//            Gdx.app.log("message","planTable received telegram:${msg.message}, ${(msg.sender as MessageComponent).entityName}, ${msg.extraInfo}")

            val entityName : String = MessageChannel.PLAN_BRIDGE.receiveMessage(msg.extraInfo)

            if ( (this.children.size >= tableMax) || (this.getChild(0) as Label).textEquals("planTable") ) {
                if (this.children.size > 0) this.getChild(0).remove()
            }

            register[(msg.sender as IEntity).entityName] = entityName

            this.clear()
            register.entries.forEach {
                this.add("[${it.key}] ${it.value}")
                this.row()
            }

            return true
        }

        return false
    }


}