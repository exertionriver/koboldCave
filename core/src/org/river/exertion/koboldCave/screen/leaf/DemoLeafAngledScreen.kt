package org.river.exertion.koboldCave.screen.leaf

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.MathUtils.sin
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.koboldCave.leaf.Leaf
import org.river.exertion.koboldCave.screen.RenderPalette

class DemoLeafAngledScreen(private val batch: Batch,
                           private val font: BitmapFont,
                           private val camera: OrthographicCamera) : KtxScreen {

    val centerPoint = Point(Game.initViewportWidth / 2, Game.initViewportHeight / 2)
    val xOffset = Point(Game.initViewportWidth / 4, 0F)
    val yOffset = Point(0F, Game.initViewportWidth / 4) // to make it appear circular

    val labelVertOffset = Point(0F, Game.initViewportHeight / 32)

    val startingList = listOf(
        centerPoint - xOffset
        , centerPoint + Point(-xOffset.x * sin(45F.radians()), -yOffset.y * sin(45F.radians()) )
        , centerPoint - yOffset
        , centerPoint + Point(xOffset.x * sin(45F.radians()), -yOffset.y * sin(45F.radians()) )
        , centerPoint + xOffset
        , centerPoint + Point(xOffset.x * sin(45F.radians()), yOffset.y * sin(45F.radians()) )
        , centerPoint + yOffset
        , centerPoint + Point(-xOffset.x * sin(45F.radians()), yOffset.y * sin(45F.radians()) )
    )

    val leafList = List(8) { leafIdx ->
        Leaf(topHeight = 4, topAngle = leafIdx * 45F, position = startingList[leafIdx] )
    }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

     //   println("leaf.size: ${leaf.getList().size}, prunedLeaf.size: ${prunedLeaf.size}")

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        batch.use { batch ->

            leafList.forEachIndexed { idx, leaf ->

                when {
                    (idx in 0..3) ->
                        font.drawLabel(batch, leaf.position - labelVertOffset / 4
                            , "Leaf(height=${leaf.topHeight})\nangled ${leaf.topAngle} degrees", RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size])
                    else ->
                        font.drawLabel(batch, leaf.position + labelVertOffset * 2
                            , "Leaf(height=${leaf.topHeight})\nangled ${leaf.topAngle} degrees", RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size])
                }

                leaf.getLineList().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,
                            RenderPalette.BackColors[idx % RenderPalette.BackColors.size], 2F )
                    }
                }

                leaf.getList().forEachIndexed { index, listLeaf ->
                    drawer.filledCircle(listLeaf.position, 2F, RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size])
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