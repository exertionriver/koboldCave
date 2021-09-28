package org.river.exertion.koboldCave.screen.lattice

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.koboldCave.lattice.ILattice.Companion.getLineList
import org.river.exertion.koboldCave.lattice.RoundedLattice
import org.river.exertion.koboldCave.screen.RenderPalette.BackColors
import org.river.exertion.koboldCave.screen.RenderPalette.ForeColors
import space.earlygrey.shapedrawer.ShapeDrawer

class DemoRoundedLatticeHeightScreen(private val batch: Batch,
                                     private val font: BitmapFont,
                                     private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVertOffset = Point(0F, Game.initViewportHeight / 32)

    val roundedLatticeList = List(8) { roundedLatticeIdx ->
        RoundedLattice(topHeight = roundedLatticeIdx + 1, position = Point((8 - roundedLatticeIdx) * horizOffset + horizOffset, roundedLatticeIdx * vertOffset + vertOffset * 2) )
    }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        val texture = Texture(pixmap, true); //remember to dispose of later

        val region = TextureRegion(texture, 0, 0, 1, 1);
        val drawer = ShapeDrawer(batch, region)

        batch.use {

            roundedLatticeList.reversed().forEachIndexed { latticeIdx, lattice ->
                font.drawLabel(it, lattice.position + labelVertOffset, "RoundedLattice(height=${lattice.topHeight})", ForeColors[latticeIdx % ForeColors.size])

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