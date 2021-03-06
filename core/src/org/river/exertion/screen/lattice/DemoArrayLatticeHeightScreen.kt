package org.river.exertion.screen.lattice

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.koboldCave.lattice.ArrayLattice
import org.river.exertion.koboldCave.lattice.ILattice.Companion.getLineList
import org.river.exertion.screen.RenderPalette.BackColors
import org.river.exertion.screen.RenderPalette.ForeColors

class DemoArrayLatticeHeightScreen(private val batch: Batch,
                                   private val font: BitmapFont,
                                   private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVertOffset = Point(0F, Game.initViewportHeight / 32)

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

                lattice.getLineList().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,BackColors[latticeIdx % BackColors.size], 2F )
                    }
                }

                lattice.getList().forEachIndexed { index, listLeaf ->
                    drawer.filledCircle(listLeaf.position, 2F, ForeColors[latticeIdx % ForeColors.size])
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