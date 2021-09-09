package org.river.exertion.screen.nodeRoom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom.Companion.buildFloors
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom.Companion.buildWalls
import org.river.exertion.screen.RenderPalette.BackColors
import org.river.exertion.screen.RenderPalette.FadeBackColors
import org.river.exertion.screen.RenderPalette.FadeForeColors
import org.river.exertion.screen.RenderPalette.ForeColors

class DemoNodeRoomWallFloorScreen(private val batch: Batch,
                                  private val font: BitmapFont,
                                  private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    var nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f), circleNoise = 0, heightNoise = 0)

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.projectionMatrix = camera.combined
        camera.update()

        batch.use {

            val nodeRoomIdx = 1

            font.drawLabel(it, Point(nodeRoom.centroid.position.x, labelVert.y), "NodeRoom (nodes=${nodeRoom.nodes.size}, exits=${nodeRoom.getExitNodes().size})\n" +
                    "(circleNoise:${nodeRoom.attributes.circleNoise})\n" +
                    "(angleNoise:${nodeRoom.attributes.angleNoise})\n" +
                    "(heightNoise:${nodeRoom.attributes.heightNoise})", ForeColors[nodeRoomIdx % ForeColors.size])

            nodeRoom.currentWall.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.5F, BackColors[nodeRoomIdx % BackColors.size])
            }
            nodeRoom.currentFloor.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.5F, FadeForeColors[4 % BackColors.size])
            }
            nodeRoom.currentWallFade.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.3F, BackColors[nodeRoomIdx % BackColors.size])
            }

            nodeRoom.getExitNodes().forEachIndexed { index, exitNode ->
                drawer.filledCircle(exitNode.position, 4F, ForeColors[nodeRoomIdx % ForeColors.size])
//                println(exitNode.attributes)
            }

            InputHandler.handleInput(camera)

            when {
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE) -> {
                    nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f))
                    nodeRoom.buildWalls()
                    nodeRoom.buildFloors()

//                    println("nodeRoomWallsSize : ${nodeRoom.currentWall.size}")
                }
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE) -> {
                    nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f))
                    nodeRoom.buildWalls()
                    nodeRoom.buildFloors()

//                    println("nodeRoomWallsSize : ${nodeRoom.currentWall.size}")
                }
            }
        }
    }

    override fun hide() {
    }

    override fun show() {
        nodeRoom.buildWalls()
        nodeRoom.buildFloors()
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