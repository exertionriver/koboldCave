package org.river.exertion.s2d.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.river.exertion.KoboldCave
import org.river.exertion.ai.messaging.AnxietyBarMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ecs.entity.IEntity

class UIAnxietyBarTable(initSkin : Skin) : Table(initSkin), Telegraph {

    val tableMax = 1

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.UI_ANXIETY_BAR.id())
        x = KoboldCave.initViewportWidth * 1f
        y = KoboldCave.initViewportHeight * 14/16f
        name = "anxietyBarTable"
//        this.debug = true
        this.add(Label("anxietyBarTable", initSkin) )
        this.row()
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
            val anxietyBarMessage = msg.extraInfo as AnxietyBarMessage

            if (anxietyBarMessage.value != null) {
                this.clear()
                this.add("[mIntAnxiety] ${"%.3f".format(anxietyBarMessage.value)}")
                this.row()
            }

            return true
        }

        return false
    }

    companion object {
        fun send(sender : Telegraph? = null, anxietyBarMessage: AnxietyBarMessage) {
            MessageManager.getInstance().dispatchMessage(sender, MessageChannel.UI_ANXIETY_BAR.id(), anxietyBarMessage)
        }
    }

}