package org.river.exertion.screen.nodeRoom

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.MathUtils
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.NodeAttributes
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom.Companion.buildFloors
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom.Companion.buildWalls
import org.river.exertion.screen.RenderPalette.BackColors
import org.river.exertion.screen.RenderPalette.FadeForeColors
import org.river.exertion.screen.RenderPalette.ForeColors

class DemoNodeRoomSlopeScreen(private val batch: Batch,
                              private val font: BitmapFont,
                              private val camera: OrthographicCamera) : KtxScreen {

    val centerPoint = Point(Game.initViewportWidth / 2, Game.initViewportHeight / 2)
    val xOffset = Point(Game.initViewportWidth / 4, 0F)
    val yOffset = Point(0F, Game.initViewportWidth / 4) // to make it appear circular
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    val labelVertOffset = Point(0F, Game.initViewportHeight / 32)

    val startingList = listOf(
        centerPoint - xOffset
        , centerPoint + Point(-xOffset.x * MathUtils.sin(45F.radians()), -yOffset.y * MathUtils.sin(45F.radians()))
        , centerPoint - yOffset
        , centerPoint + Point(xOffset.x * MathUtils.sin(45F.radians()), -yOffset.y * MathUtils.sin(45F.radians()))
        , centerPoint + xOffset
        , centerPoint + Point(xOffset.x * MathUtils.sin(45F.radians()), yOffset.y * MathUtils.sin(45F.radians()))
        , centerPoint + yOffset
        , centerPoint + Point(-xOffset.x * MathUtils.sin(45F.radians()), yOffset.y * MathUtils.sin(45F.radians()))
    )

    val nodeRoomList = List(8) { nodeLineIdx ->
        val firstNode = Node(position = startingList[nodeLineIdx].round())
        firstNode.attributes.nodeElevation = NodeAttributes.getProbNodeElevation()
        firstNode.attributes.nodeObstacle = NodeAttributes.getProbNodeObstacle()

        val secondNode = Node(position = startingList[nodeLineIdx].getPositionByDistanceAndAngle((nodeLineIdx + 1) * xOffset.x / 8, nodeLineIdx * 45f ).round())
        secondNode.attributes.nodeElevation = NodeAttributes.getProbNodeElevation()
        secondNode.attributes.nodeObstacle = NodeAttributes.getProbNodeObstacle()

        val nodeLink = NodeLink(firstNode, secondNode)
        NodeRoom(nodes = mutableListOf(firstNode, secondNode), nodeLinks = mutableListOf(nodeLink) )
    }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.projectionMatrix = camera.combined
        camera.update()

        batch.use {

            val nodeRoomIdx = 1
            
    //        drawer.arc(198f, 206f, 12f, 20f.radians(), 120f.radians() )
    //        drawer.arc(203f, 203f, 12f, 80f.radians(), 120f.radians() )
    //        drawer.arc(200f, 200f, 12f, 40f.radians(), 120f.radians() )

            nodeRoomList.forEachIndexed { nodeRoomIdx, nodeRoom ->
                val yLabelOffset = if (nodeRoomIdx < 5) nodeRoom.nodes[0].position.y - labelVert.y / 2 else nodeRoom.nodes[0].position.y + labelVert.y * 2

                font.drawLabel(it, Point(nodeRoom.nodes[0].position.x, yLabelOffset), "NodeRoom (elevation:${nodeRoom.nodes[0].attributes.nodeElevation} to ${nodeRoom.nodes[1].attributes.nodeElevation})\n" +
                    "obstacle:${nodeRoom.nodes[0].attributes.nodeObstacle} to ${nodeRoom.nodes[1].attributes.nodeObstacle}"
                    , ForeColors[nodeRoomIdx % ForeColors.size])

                nodeRoom.currentWall.values.forEach { wallNode ->
                    drawer.filledCircle(wallNode, 0.5F, BackColors[nodeRoomIdx % BackColors.size])
                }
                nodeRoom.currentFloor.values.forEach { wallNode ->
                    drawer.filledCircle(wallNode, 0.5F, FadeForeColors[4 % BackColors.size])
                }
                nodeRoom.currentWallFade.values.forEach { wallNode ->
                    drawer.filledCircle(wallNode, 0.3F, BackColors[nodeRoomIdx % BackColors.size])
                }

            }

            InputHandler.handleInput(camera)

        }
    }

    override fun hide() {
    }

    override fun show() {
        nodeRoomList.forEach { it.buildWalls() }
        nodeRoomList.forEach { it.buildFloors() }
        nodeRoomList.forEach { println("${it.description} ${it.nodes}") }
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