package org.river.exertion.s2d.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import org.river.exertion.*
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.messaging.AnxietyBarMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolDisplayMessage
import org.river.exertion.ai.messaging.TimingTableMessage
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.geom.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.s2d.actor.ActorKobold
import space.earlygrey.shapedrawer.JoinType

class UIAnxietyBar(initSkin : Skin) : ProgressBar(0f, 1f, 0.01f, true, initSkin), Telegraph {

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.UI_ANXIETY_BAR.id())
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

    companion object {
        fun send(sender : Telegraph? = null, anxietyBarMessage: AnxietyBarMessage) {
            MessageManager.getInstance().dispatchMessage(sender, MessageChannel.UI_ANXIETY_BAR.id(), anxietyBarMessage)
        }
    }

}