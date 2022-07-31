package org.river.exertion.s2d.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ecs.entity.IEntity

class UIPerceptionTable(initSkin : Skin) : Table(), Telegraph {

    val tableMax = 5

    val register = mutableMapOf<String, String>()

    init {
        MessageChannel.PERCEPTION_BRIDGE.enableReceive(this)

        skin = initSkin
        x = Gdx.graphics.width / 8f
        y = 6 * Gdx.graphics.height / 8f
        name = "perceptionTable"
//        this.debug = true
        this.add(Label("perceptionTable", initSkin) )
        this.row()
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
//            Gdx.app.log("message","perceptionTable received telegram:${msg.message}, ${(msg.sender as MessageComponent).entityName}, ${msg.extraInfo}")

            if ( (this.children.size >= tableMax) || (this.getChild(0) as Label).textEquals("perceptionTable") ) {
                if (this.children.size > 0) this.getChild(0).remove()
            }

            register[(msg.sender as IEntity).entityName] = (msg.extraInfo) as String

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