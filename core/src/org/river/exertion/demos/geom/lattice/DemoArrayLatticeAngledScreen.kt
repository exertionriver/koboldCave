package org.river.exertion.demos.geom.lattice

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.MathUtils.sin
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.geom.lattice.ArrayLattice
import org.river.exertion.geom.lattice.ILattice.Companion.getLineSet
import org.river.exertion.RenderPalette

class DemoArrayLatticeAngledScreen(private val batch: Batch,
                                   private val font: BitmapFont,
                                   private val camera: OrthographicCamera) : KtxScreen {

    val centerPoint = Point(KoboldCave.initViewportWidth / 2, KoboldCave.initViewportHeight / 2)
    val xOffset = Point(KoboldCave.initViewportWidth / 4, 0F)
    val yOffset = Point(0F, KoboldCave.initViewportWidth / 4) // to make it appear circular

    val labelVertOffset = Point(0F, KoboldCave.initViewportHeight / 32)

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

    val laceList = List(8) { latticeIdx ->
        ArrayLattice(topHeight = 4, topAngle = latticeIdx * 45F, position = startingList[latticeIdx] )
    }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

     //   println("leaf.size: ${leaf.getList().size}, prunedLeaf.size: ${prunedLeaf.size}")

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        batch.use { batch ->

            laceList.forEachIndexed { idx, lattice ->

                when {
                    (idx in 0..3) ->
                        font.drawLabel(batch, lattice.position - labelVertOffset / 4
                            , "Lattice(height=${lattice.topHeight})\nangled ${lattice.topAngle} degrees", RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size])
                    else ->
                        font.drawLabel(batch, lattice.position + labelVertOffset * 4
                            , "Lattice(height=${lattice.topHeight})\nangled ${lattice.topAngle} degrees", RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size])
                }

                lattice.getLineSet().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,
                            RenderPalette.BackColors[idx % RenderPalette.BackColors.size], 2F )
                    }
                }

                lattice.getSet().forEachIndexed { index, listLeaf ->
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