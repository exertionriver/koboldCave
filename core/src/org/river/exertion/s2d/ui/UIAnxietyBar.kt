package org.river.exertion.s2d.ui

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import org.river.exertion.KoboldCave
import org.river.exertion.ai.messaging.AnxietyBarMessage
import org.river.exertion.ai.messaging.MessageChannel

class UIAnxietyBar(initSkin : Skin) : ProgressBar(0f, 1f, 0.01f, true, initSkin), Telegraph {

    init {
        MessageChannel.UI_ANXIETY_BAR.enableReceive(this)
        this.value = 0f
        x = KoboldCave.initViewportWidth * 1f
        y = KoboldCave.initViewportHeight * 9/16f
        name = "anxiety Bar"
//        this.debug = true
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
            val anxietyBarMessage = msg.extraInfo as AnxietyBarMessage

            if (anxietyBarMessage.value != null) this.value = anxietyBarMessage.value!!
            return true
        }

        return false
    }
}