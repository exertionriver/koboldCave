package org.river.exertion.demos.geom.nodeLine

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.MathUtils.sin
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.nodeMesh.NodeLine
import org.river.exertion.RenderPalette

class DemoNodeLineAngledScreen(private val batch: Batch,
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

    val nodeLineList = List(8) { nodeLineIdx ->
        NodeLine(firstNode = Node(position = startingList[nodeLineIdx]), lastNode = Node(position = centerPoint), lineNoise = 10 * (nodeLineIdx + 1) )
    }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

     //   println("leaf.size: ${leaf.getList().size}, prunedLeaf.size: ${prunedLeaf.size}")

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        batch.use { batch ->

            nodeLineList.forEachIndexed { idx, nodeLine ->

                var labelText = "NodeLine(length=${nodeLine.getLineLength().toInt()})\nangled ${idx * 45} degrees\n(lineNoise=${nodeLine.lineNoise})"
                if (nodeLine.lineNoise > 100) labelText += "\n(capped at 100)"

                when {
                    (idx in 0..3) -> {
                        font.drawLabel(batch, nodeLine.nodes.first().position - labelVertOffset / 4, labelText, RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size])
                    }
                    else -> {
                        font.drawLabel(batch, nodeLine.nodes.first().position + labelVertOffset * 5, labelText, RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size])
                    }
                }

                nodeLine.getLineSet().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,
                            RenderPalette.BackColors[idx % RenderPalette.BackColors.size], 2F )
                    }
                }

                nodeLine.nodes.forEachIndexed { index, listLeaf ->
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