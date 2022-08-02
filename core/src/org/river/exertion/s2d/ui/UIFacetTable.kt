package org.river.exertion.s2d.ui

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import org.river.exertion.KoboldCave
import org.river.exertion.ai.messaging.FacetTableMessage
import org.river.exertion.ai.messaging.ManifestDisplayMessage
import org.river.exertion.ai.messaging.MessageChannel

class UIFacetTable(val initSkin : Skin) : Table(), Telegraph {

    val tableMax = 5

    val register = mutableMapOf<String, Float>()

    init {
        MessageChannel.UI_FACET_DISPLAY.enableReceive(this)
        x = KoboldCave.initViewportWidth * 1f
        y = KoboldCave.initViewportHeight * 7/16f
        name = "facetDisplay"
//        this.debug = true
        this.add(Label(this.name, initSkin))
        this.row()
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
            val facetTableMessage : FacetTableMessage = MessageChannel.UI_FACET_DISPLAY.receiveMessage(msg.extraInfo)

            register.clear()

            if ( (facetTableMessage.internalFacetInstancesState != null) && (facetTableMessage.internalFacetInstancesState!!.currentState().isNotEmpty() ) ) {
                        facetTableMessage.internalFacetInstancesState!!.currentState().forEach {
                            register[it.facetObj.type.tag()] = it.magnitude
                                   // ", ${it.arisenFacetInstance(facetTableMessage.mIntAnxiety!!).magnitude}"

            //                facetTableMessage.internalFacetAttributesState!!.projections(facetTableMessage.mIntAnxiety!! / 10f).forEachIndexed {
//                    idx, it -> register["slot($idx) : ${it?.arisenFacet?.facet()?.type?.tag()}"] = it?.arisenFacet?.magnitude!!
                    }
                }

            this.clear()

            register.entries.forEach {
                this.add(Label("[${it.key}] ${"%.3f".format(it.value)}", initSkin)).apply { this.align(Align.right)}
                this.row().apply { this.align(Align.right) }
            }

            return true
        }

        return false
    }
}