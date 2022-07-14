package org.river.exertion.demos.geom.leaf

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.geom.leaf.Lace
import org.river.exertion.RenderPalette.BackColors
import org.river.exertion.RenderPalette.ForeColors

class DemoLaceHeightScreen(private val batch: Batch,
                           private val font: BitmapFont,
                           private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = KoboldCave.initViewportWidth / 11
    val vertOffset = KoboldCave.initViewportHeight / 11
    val labelVertOffset = Point(0F, KoboldCave.initViewportHeight / 32)

    val laceList = List(8) { laceIdx ->
        Lace(topHeight = laceIdx + 1, position = Point((8 - laceIdx) * horizOffset + horizOffset, laceIdx * vertOffset + vertOffset * 2) )
    }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        batch.use {

            laceList.reversed().forEachIndexed { laceIdx, lace ->
                font.drawLabel(it, lace.position + labelVertOffset, "Lace (height=${lace.topHeight})", ForeColors[laceIdx % ForeColors.size])

                lace.getLineSet().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,BackColors[laceIdx % BackColors.size], 2F )
                    }
                }

                lace.getSet().forEachIndexed { index, listLeaf ->
                    drawer.filledCircle(listLeaf.position, 2F, ForeColors[laceIdx % ForeColors.size])
                }
            }

            InputHandler.handleInput(camera)
        }

    }

    override fun hide() {
    }

    override fun show() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
    }

    override fun dispose() {
        sdc.disposeShapeDrawerConfig()
    }
}