package org.river.exertion.koboldCave.screen.nodeRoom

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.app.KtxScreen
import ktx.ashley.contains
import ktx.ashley.get
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.assets.*
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.entity.EntityKobold
import org.river.exertion.ecs.component.entity.EntityPlayerCharacter
import org.river.exertion.ecs.component.environment.EnvironmentCave
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine.Companion.buildNodeLine
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsAndPath
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh.Companion.renderWallsAndPath
import org.river.exertion.koboldCave.screen.Render
import org.river.exertion.koboldCave.screen.RenderPalette
import org.river.exertion.koboldCave.screen.render

class DemoNodeRoomECSNavigateScreen(private val batch: Batch,
                                    private val font: BitmapFont,
                                    private val assets: AssetManager,
                                    private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    var nodeRoom = NodeRoom(height = 4, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f))
    var nodeRoomMesh = NodeRoomMesh(nodeRoom)

    val engine = PooledEngine().apply { ActionPlexSystem(this) }
    val cave = EnvironmentCave.instantiate(engine, "spookyCave", nodeRoomMesh)
    val playerCharacter = EntityPlayerCharacter.instantiate(engine, cave = cave, camera = null)

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    val controlAreaCamera = OrthographicCamera()
//    val controlAreaViewport = ExtendViewport(Gdx.graphics.getWidth().toFloat(), Gdx.graphics.getHeight().toFloat(), controlAreaCamera)

    override fun render(delta: Float) {

        InputHandler.handleInput(camera)

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.UP) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.FORWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.BACKWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.LEFT }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.RIGHT }
        }

        camera.update()
        batch.projectionMatrix = camera.combined

        batch.use {

            cave[EnvironmentCave.mapper]!!.nodeRoomMesh.render(batch)

            PlayerCharacter.render(batch, playerCharacter[ActionMoveComponent.mapper]!!.currentPosition, playerCharacter[ActionMoveComponent.mapper]!!.currentAngle)

            engine.entities.filter { checkEntity -> checkEntity.isEntity() && checkEntity.contains(EntityKobold.mapper) }.forEach { renderKobold ->
                Kobold.render(batch, renderKobold[ActionMoveComponent.mapper]!!.currentPosition, renderKobold[ActionMoveComponent.mapper]!!.currentAngle)
            }
        }

        controlAreaCamera.update()
        batch.projectionMatrix = controlAreaCamera.combined

        batch.use {
            font.drawLabel(batch, Point(300f, 100f), "${playerCharacter[ActionMoveComponent.mapper]!!.currentNode}\n${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeLink}\n" +
                    "nodeRoom:${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.uuid}\nlength:${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeLink.getDistance(nodeRoomMesh.nodesMap.keys)}", RenderPalette.ForeColors[1])
        }

        engine.update(delta)

    }

    override fun hide() {
    }

    override fun show() {
        Render.initRender(camera, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid, Render.cameraAngle)
        controlAreaCamera.setToOrtho(false, Gdx.graphics.getWidth().toFloat(), Gdx.graphics.getHeight().toFloat())

        cave[EnvironmentCave.mapper]!!.nodeRoomMesh.buildWallsAndPath()
        cave[EnvironmentCave.mapper]!!.nodeRoomMesh.renderWallsAndPath()

        // start the playback of the background music when the screen is shown
        MusicAssets.values().forEach { assets.load(it) }
        assets.finishLoading()
//        println("done!")
        assets[MusicAssets.NavajoNight].apply { isLooping = true }.play()
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
        assets.dispose()
    }
}