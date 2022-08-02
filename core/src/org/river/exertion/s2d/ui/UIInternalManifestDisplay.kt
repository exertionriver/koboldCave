package org.river.exertion.s2d.ui

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import org.river.exertion.KoboldCave
import org.river.exertion.ai.messaging.ManifestDisplayMessage
import org.river.exertion.ai.messaging.MessageChannel

class UIInternalManifestDisplay(val initSkin : Skin) : Table(), Telegraph {

    val tableMax = 10

    val register = mutableMapOf<Int, String>()

    init {
        MessageChannel.UI_MANIFEST_DISPLAY.enableReceive(this)
        x = KoboldCave.initViewportWidth * 1f
        y = KoboldCave.initViewportHeight * 2/16f
        name = "internalManifestDisplay"
//        this.debug = true
        this.add(Label(this.name, initSkin))
        this.row()
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
            val internalManifestDisplayMessage : ManifestDisplayMessage = MessageChannel.UI_MANIFEST_DISPLAY.receiveMessage(msg.extraInfo)

            register.clear()

            if (internalManifestDisplayMessage.internalManifest != null) {
                internalManifestDisplayMessage.internalManifest!!.manifests[0].projectionList.forEachIndexed { idx, projection ->
                    if (projection == null) register[idx] = "null" else register[idx] = "${projection.arisenFacet.facetObj.type.tag()} : ${projection.countdown}"
                }
            }

            this.clear()

            register.entries.forEach {
                this.add(Label("[${it.key}] ${it.value}", initSkin)).apply { this.align(Align.right)}
                this.row().apply { this.align(Align.right) }
            }

            return true
        }

        return false
    }

    override fun draw(batch : Batch, parentAlpha : Float) {
        super.draw(batch, parentAlpha)
    }
}