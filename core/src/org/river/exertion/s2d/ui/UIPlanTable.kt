package org.river.exertion.s2d.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import org.river.exertion.MessageIds
import org.river.exertion.Point
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.action.MessageComponent
import org.river.exertion.ecs.component.action.core.ActionNoneComponent.label

class UIPlanTable(initSkin : Skin) : Table(), Telegraph {

    val tableMax = 5

    init {
        MessageManager.getInstance().addListener(this, MessageIds.PLAN_BRIDGE.id())

        skin = initSkin
        x = Gdx.graphics.width / 8f
        y = 4 * Gdx.graphics.height / 8f
        name = "planTable"
        this.debug = true
        this.add(Label("planTable", initSkin) )
        this.row()
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
            Gdx.app.log("message","planTable received telegram:${msg.message}, ${(msg.sender as MessageComponent).name}, ${msg.extraInfo}")

            if ( (this.children.size >= tableMax) || (this.getChild(0) as Label).textEquals("planTable") ) {
                if (this.children.size > 0) this.getChild(0).remove()
            }

            this.add("[${(msg.sender as MessageComponent).name}] ${msg.extraInfo}")
            this.row()

            return true
        }

        return false
    }


}