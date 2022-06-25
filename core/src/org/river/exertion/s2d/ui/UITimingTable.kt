package org.river.exertion.s2d.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.TimingTableMessage

class UITimingTable(val initSkin : Skin) : Table(), Telegraph {

    val tableMax = 5

    val register = mutableMapOf<String, Float>()

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.UI_TIMING_DISPLAY.id())
        register["elapsed"] = 0f
//        skin = initSkin
        x = Gdx.graphics.width / 8f
        y = 5 * Gdx.graphics.height / 8f
        name = "timingTable"
//        this.debug = true
        this.add(Label(this.name, initSkin))
        this.row()
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
            val timingTableMessage = msg.extraInfo as TimingTableMessage

            if ( (timingTableMessage.label != null) && (timingTableMessage.value != null) ) {
                register[timingTableMessage.label!!] = timingTableMessage.value!!

                if (timingTableMessage.timingType == TimingTableMessage.TimingEntryType.RENDER)
                    register["elapsed"] = register["elapsed"]!! + timingTableMessage.value!!
            }

            this.clear()
//            this.add(Label(name, initSkin)).apply { this.align(Align.right)}
//            this.row().apply { this.align(Align.right) }

            register.entries.forEach {
                this.add(Label("[${it.key}] ${"%.3f".format(it.value)}", initSkin)).apply { this.align(Align.right) }
                this.row().apply { this.align(Align.right) }
            }

            return true
        }

        return false
    }

    companion object {

        fun send(sender : Telegraph? = null, timingTableMessage: TimingTableMessage) {
            MessageManager.getInstance().dispatchMessage(sender, MessageChannel.UI_TIMING_DISPLAY.id(), timingTableMessage)
        }
    }

}