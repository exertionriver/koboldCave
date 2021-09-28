package org.river.exertion.koboldCave.screen.nodeRoom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.screen.RenderPalette.BackColors
import org.river.exertion.koboldCave.screen.RenderPalette.ForeColors

class DemoNodeRoomHeightScreen(private val batch: Batch,
                               private val font: BitmapFont,
                               private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    var circleNoise = 50
    var angleNoise = 50
    var heightNoise = 50

    var nodeRoomList = List(3) { nodeRoomIdx ->
        NodeRoom(height = nodeRoomIdx * 2 + 1, centerPoint = Point((3 - nodeRoomIdx) * horizOffset * 3f + horizOffset, nodeRoomIdx * vertOffset * 2 + vertOffset),
            circleNoise = circleNoise, angleNoise = angleNoise, heightNoise = heightNoise)
    }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.projectionMatrix = camera.combined
        camera.update()

        batch.use {

            nodeRoomList.reversed().forEachIndexed { nodeRoomIdx, nodeRoom ->
                font.drawLabel(it, Point(nodeRoom.centroid.position.x, labelVert.y), "NodeRoom (nodes=${nodeRoom.nodes.size}, exits=${nodeRoom.getExitNodes().size})\n" +
                        "(circleNoise:${nodeRoom.attributes.circleNoise})\n" +
                        "(angleNoise:${nodeRoom.attributes.angleNoise})\n" +
                        "(heightNoise:${nodeRoom.attributes.heightNoise})", ForeColors[nodeRoomIdx % ForeColors.size])

                nodeRoom.getLineList().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,BackColors[nodeRoomIdx % BackColors.size], 2F )
                    }
                }

                nodeRoom.nodes.forEachIndexed { index, node ->
                    drawer.filledCircle(node.position, 2F, BackColors[nodeRoomIdx % ForeColors.size])
                }

                nodeRoom.nodes.forEach { node ->
                    drawer.filledCircle(node.position, 2F, ForeColors[nodeRoomIdx % ForeColors.size])
                }

                drawer.filledCircle(nodeRoom.centroid.position, 6F, ForeColors[nodeRoomIdx % ForeColors.size])

                nodeRoom.getExitNodes().forEachIndexed { index, exitNode ->
                    drawer.filledCircle(exitNode.position, 4F, ForeColors[nodeRoomIdx % ForeColors.size])
                }
            }

            InputHandler.handleInput(camera)

            when {
                Gdx.input.isKeyJustPressed(Input.Keys.T) -> { if (circleNoise < 100) circleNoise += 10 }
                Gdx.input.isKeyJustPressed(Input.Keys.Y) -> { if (angleNoise < 100) angleNoise += 10 }
                Gdx.input.isKeyJustPressed(Input.Keys.U) -> { if (heightNoise < 100) heightNoise += 10 }
                Gdx.input.isKeyJustPressed(Input.Keys.G) -> { if (circleNoise > 0) circleNoise -= 10 }
                Gdx.input.isKeyJustPressed(Input.Keys.H) -> { if (angleNoise > 0) angleNoise -= 10 }
                Gdx.input.isKeyJustPressed(Input.Keys.J) -> { if (heightNoise > 0) heightNoise -= 10 }
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE) -> {
                    nodeRoomList = List(3) { nodeRoomIdx ->
                        NodeRoom(height = nodeRoomIdx * 2 + 1, centerPoint = Point((3 - nodeRoomIdx) * horizOffset * 3f + horizOffset, nodeRoomIdx * vertOffset * 2 + vertOffset),
                            circleNoise = circleNoise, angleNoise = angleNoise, heightNoise = heightNoise)
                    }
                }
            }
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