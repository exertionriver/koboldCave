package org.river.exertion.demos.geom.leaf

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.geom.leaf.Leaf
import org.river.exertion.RenderPalette.BackColors
import org.river.exertion.RenderPalette.ForeColors

class DemoLeafHeightScreen(private val batch: Batch,
                    private val font: BitmapFont,
                    private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = KoboldCave.initViewportWidth / 11
    val vertOffset = KoboldCave.initViewportHeight / 11
    val labelVertOffset = Point(0F, KoboldCave.initViewportHeight / 32)

    val leafList = List(8) { leafIdx ->
        Leaf(topHeight = leafIdx + 1, position = Point((8 - leafIdx) * horizOffset + horizOffset, leafIdx * vertOffset + vertOffset * 2) )
    }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        batch.use {

            leafList.reversed().forEachIndexed { leafIdx, leaf ->
                font.drawLabel(it, leaf.position + labelVertOffset, "Leaf (height=${leaf.topHeight})", ForeColors[leafIdx % ForeColors.size])

                leaf.getLineSet().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,BackColors[leafIdx % BackColors.size], 2F )
                    }
                }

                leaf.getSet().forEachIndexed { index, listLeaf ->
                    drawer.filledCircle(listLeaf.position, 2F, ForeColors[leafIdx % ForeColors.size])
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