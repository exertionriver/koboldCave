package org.river.exertion.koboldQueue.screen.cave

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.ashley.contains
import ktx.ashley.get
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.assets.*
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.entity.EntityKobold
import org.river.exertion.ecs.component.environment.EnvironmentCave
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsAndPath
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh.Companion.renderWallsAndPath
import org.river.exertion.koboldCave.screen.RenderPalette.BackColors
import org.river.exertion.koboldCave.screen.RenderPalette.FadeForeColors
import org.river.exertion.koboldCave.screen.RenderPalette.ForeColors

class DemoNodeRoomCaveScreen(private val batch: Batch,
                             private val font: BitmapFont,
                             private val assets: AssetManager,
                             private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    var nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f), circleNoise = 0, heightNoise = 0)

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    var currentNode = nodeRoom.getRandomNode()
    var currentAngle = nodeRoom.getRandomNextNodeLinkAngle(currentNode).second

    val engine = PooledEngine().apply { ActionPlexSystem(this) }
    val cave = EnvironmentCave.instantiate(engine, "spookyCave")

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

            engine.entities.filter { checkEntity -> checkEntity.isEntity() && checkEntity.contains(EntityKobold.mapper)}.forEach { renderKobold ->
                Kobold.render(batch, renderKobold[ActionMoveComponent.mapper]!!.currentNode.position, renderKobold[ActionMoveComponent.mapper]!!.currentAngle)
            }

            InputHandler.handleInput(camera)

      //      println("delta:$delta")
            engine.update(delta)

        }
    }

    override fun hide() {
    }

    override fun show() {
        NodeRoomMesh(nodeRoom).buildWallsAndPath()
        NodeRoomMesh(nodeRoom).renderWallsAndPath()
        cave[EnvironmentCave.mapper]!!.nodeRoom = nodeRoom

        // start the playback of the background music when the screen is shown
        MusicAssets.values().forEach { assets.load(it) }
        assets.finishLoading()
//        println("done!")
        assets[MusicAssets.DarkMystery].apply { isLooping = true }.play()
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