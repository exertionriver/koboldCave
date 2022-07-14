package org.river.exertion.demos.geom.lattice

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.geom.lattice.ArrayLattice
import org.river.exertion.geom.lattice.ILattice.Companion.getLineSet
import org.river.exertion.RenderPalette.BackColors
import org.river.exertion.RenderPalette.ForeColors

class DemoArrayLatticeHeightScreen(private val batch: Batch,
                                   private val font: BitmapFont,
                                   private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = KoboldCave.initViewportWidth / 11
    val vertOffset = KoboldCave.initViewportHeight / 11
    val labelVertOffset = Point(0F, KoboldCave.initViewportHeight / 32)

    val arrayLatticeList = List(8) { arrayLatticeIdx ->
        ArrayLattice(topHeight = arrayLatticeIdx + 1, position = Point((8 - arrayLatticeIdx) * horizOffset + horizOffset, arrayLatticeIdx * vertOffset + vertOffset * 2) )
    }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        batch.use {

            arrayLatticeList.reversed().forEachIndexed { latticeIdx, lattice ->
                font.drawLabel(it, lattice.position + labelVertOffset, "ArrayLattice(height=${lattice.topHeight})", ForeColors[latticeIdx % ForeColors.size])

                lattice.getLineSet().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,BackColors[latticeIdx % BackColors.size], 2F )
                    }
                }

                lattice.getSet().forEachIndexed { index, listLattice ->
                    drawer.filledCircle(listLattice.position, 2F, ForeColors[latticeIdx % ForeColors.size])
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