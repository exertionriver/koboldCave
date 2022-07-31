package org.river.exertion.s2d.ui

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import org.river.exertion.*
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolDisplayMessage
import space.earlygrey.shapedrawer.JoinType

class UISymbolDisplay(val initSkin : Skin) : Table(), Telegraph {

    val tableMax = 5

    val register = mutableMapOf<String, SymbolInstance>()

    init {
        MessageChannel.UI_SYMBOL_DISPLAY.enableReceive(this)
        x = KoboldCave.initViewportWidth * 8/16f
        y = KoboldCave.initViewportHeight * 12/16f
        name = "symbolDisplay"
//        this.debug = true
        this.add(Label(this.name, initSkin))
        this.row()
    }

    override fun handleMessage(msg: Telegram?): Boolean {

        if (msg != null) {
            val symbolDisplayMessage = msg.extraInfo as SymbolDisplayMessage

            register.clear()

            if ( (symbolDisplayMessage.symbolDisplay != null) && (symbolDisplayMessage.symbolDisplay!!.symbolDisplay.isNotEmpty() ) ) {
                symbolDisplayMessage.symbolDisplay!!.symbolDisplay.forEach { symbolInstance ->
                    register[symbolInstance.uuid.toString()] = symbolInstance
                }
            }

            this.clear()

            register.entries.forEach {
                this.add(Label("[${it.value.symbolObj}] ${it.value.cycles} @ ${"%.3f".format(it.value.position)}", initSkin)).apply { this.align(Align.right)}
                this.row().apply { this.align(Align.right) }
            }

            return true
        }

        return false
    }

    override fun draw(batch : Batch, parentAlpha : Float) {
        super.draw(batch, parentAlpha)
        render(batch, Point(x, y - 100f))
    }


    fun render(batch : Batch, currentPos : Point)  {

        val pcSdc = ShapeDrawerConfig(batch, RenderPalette.ForeColors[7])
        val pcDrawer = pcSdc.getDrawer()

        val halfWidth = 300f

        val bar : com.badlogic.gdx.utils.Array<Vector2> = com.badlogic.gdx.utils.Array()

        bar.clear()

//            Gdx.app.log("render","scaleX: $scaleX")

        val leftOffset = currentPos.minus(Point(halfWidth, 0f))
        val rightOffset = currentPos.plus(Point(halfWidth, 0f))

        bar.add(leftOffset, rightOffset)
        pcDrawer.path(bar, 1f, JoinType.SMOOTH, true)

        (0 .. 600 step 60).forEach { vertTick ->
            val tickOffset = vertTick - halfWidth

            val topOffset = currentPos.plus(Point(tickOffset, 10f))
            val bottomOffset = currentPos.plus(Point(tickOffset, -10f))

            bar.clear()
            bar.add(topOffset, bottomOffset)
            pcDrawer.path(bar, 1f, JoinType.SMOOTH, true)
        }

        register.entries.forEach {
            val tickOffset = it.value.position * halfWidth

            val topOffset = currentPos.plus(Point(tickOffset, 20f))
            val bottomOffset = currentPos.plus(Point(tickOffset, -20f))

            bar.clear()
            bar.add(topOffset, bottomOffset)
            pcDrawer.path(bar, 1f, JoinType.SMOOTH, true)
        }

    }
}