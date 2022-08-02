package org.river.exertion.s2d.ui

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.river.exertion.KoboldCave
import org.river.exertion.ai.messaging.AnxietyBarMessage
import org.river.exertion.ai.messaging.MessageChannel

class UIAnxietyBarTable(initSkin : Skin) : Table(initSkin), Telegraph {

    val tableMax = 1

    init {
        MessageChannel.UI_ANXIETY_BAR.enableReceive(this)
        x = KoboldCave.initViewportWidth * 1f
        y = KoboldCave.initViewportHeight * 14/16f
        name = "anxietyBarTable"
//        this.debug = true
        this.add(Label("anxietyBarTable", initSkin) )
        this.row()
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
            val anxietyBarMessage : AnxietyBarMessage = MessageChannel.UI_ANXIETY_BAR.receiveMessage(msg.extraInfo)

            if (anxietyBarMessage.value != null) {
                this.clear()
                this.add("[mIntAnxiety] ${"%.3f".format(anxietyBarMessage.value)}")
                this.row()
            }

            return true
        }

        return false
    }
}