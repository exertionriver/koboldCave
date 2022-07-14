package org.river.exertion.demos.geom.nodeLine

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.nodeMesh.NodeLine
import org.river.exertion.RenderPalette.BackColors
import org.river.exertion.RenderPalette.ForeColors

class DemoNodeLineHeightScreen(private val batch: Batch,
                               private val font: BitmapFont,
                               private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = KoboldCave.initViewportWidth / 11
    val vertOffset = KoboldCave.initViewportHeight / 11
    val labelVertOffset = Point(0F, KoboldCave.initViewportHeight / 32)

    val firstNodeList = List(8) { idx -> Node(description = "nodeLine${idx}", position = Point((8 - idx) * 100 + 50f, (8 - idx) * vertOffset + 50f) ) }
    val secondNodeList = List(8) { idx -> Node(description = "nodeLine${idx}", position = Point((8 - idx) * 100 + 50f, KoboldCave.initViewportHeight - 50f ) ) }

    val nodeLineList = List(8) { nodeIdx -> NodeLine(firstNode = firstNodeList[7 - nodeIdx], lastNode = secondNodeList[7 - nodeIdx], lineNoise = (7 - nodeIdx) * 15) }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        batch.use {

            nodeLineList.reversed().forEachIndexed { nodeLineIdx, nodeLine ->
                font.drawLabel(it, firstNodeList[nodeLineIdx].position - labelVertOffset, "NodeLine (length=${nodeLine.getLineLength().toInt()})", ForeColors[nodeLineIdx % ForeColors.size])
                font.drawLabel(it, firstNodeList[nodeLineIdx].position - labelVertOffset * 2, "(lineNoise=${nodeLine.lineNoise})", ForeColors[nodeLineIdx % ForeColors.size])
                if (nodeLine.lineNoise > 100) {
                    font.drawLabel(it, firstNodeList[nodeLineIdx].position - labelVertOffset * 3, "(capped at 100)", ForeColors[nodeLineIdx % ForeColors.size])
                }


                nodeLine.getLineSet().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,BackColors[nodeLineIdx % BackColors.size], 2F )
                    }
                }

                nodeLine.nodes.forEachIndexed { index, listLeaf ->
                    drawer.filledCircle(listLeaf.position, 2F, ForeColors[nodeLineIdx % ForeColors.size])
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