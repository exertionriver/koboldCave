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
import org.river.exertion.ai.messaging.MemoryDisplayMessage
import org.river.exertion.ai.messaging.MessageChannel

class UIMemoryTable(val initSkin : Skin) : Table(), Telegraph {

    val tableMax = 5

    val register = mutableMapOf<String, String>()

    init {
        MessageChannel.UI_MEMORY_DISPLAY.enableReceive(this)
        x = KoboldCave.initViewportWidth * 0f
        y = KoboldCave.initViewportHeight * 1f
        name = "memoryDisplay"
//        this.debug = true
        this.add(Label(this.name, initSkin))
        this.row()
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
            val memoryDisplayMessage : MemoryDisplayMessage = MessageChannel.UI_MEMORY_DISPLAY.receiveMessage(msg.extraInfo)

            register.clear()

            if ( (memoryDisplayMessage.internalMemory != null) && (memoryDisplayMessage.internalMemory!!.activeMemory.noumenaRegister.isNotEmpty()) ) {
                memoryDisplayMessage.internalMemory!!.activeMemory.noumenaRegister.filter {it.perceivedNoumenon.instanceName != null}.forEach {
                            register[it.perceivedNoumenon.instanceName!!] = "${it.symbol.tag} : ${it.perceivedNoumenon.perceivedAttributes.first().attributeInstance!!.attributeObj.type().tag()}"
                                   // ", ${it.arisenFacetInstance(facetTableMessage.mIntAnxiety!!).magnitude}"

            //                facetTableMessage.internalFacetAttributesState!!.projections(facetTableMessage.mIntAnxiety!! / 10f).forEachIndexed {
//                    idx, it -> register["slot($idx) : ${it?.arisenFacet?.facet()?.type?.tag()}"] = it?.arisenFacet?.magnitude!!
                    }
                }

            this.clear()

            register.entries.forEach {
                this.add(Label("[${it.key}] ${(it.value)}", initSkin)).apply { this.align(Align.right)}
                this.row().apply { this.align(Align.right) }
            }

            return true
        }

        return false
    }
}