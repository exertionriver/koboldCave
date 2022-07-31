package org.river.exertion.demos.fsm

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxScreen
import ktx.ashley.get
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.assets.MusicAssets
import org.river.exertion.assets.get
import org.river.exertion.assets.load
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.ecs.entity.character.CharacterPlayerCharacter
import org.river.exertion.ecs.entity.location.LocationCave
import org.river.exertion.ecs.system.SystemManager
import org.river.exertion.geom.node.nodeMesh.NodeRoom
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsAndPath
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.render
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.renderWallsAndPath

class DemoFSM(private val menuBatch: Batch,
              private val font: BitmapFont,
              private val assets: AssetManager,
              private val menuStage: Stage,
              private val menuCamera: OrthographicCamera) : KtxScreen {

    val horizOffset = KoboldCave.initViewportWidth / 11
    val vertOffset = KoboldCave.initViewportHeight / 11
    val labelVert = Point(0F, KoboldCave.initViewportHeight * 2 / 32)

    var nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f))
    var nodeRoomMesh = NodeRoomMesh(nodeRoom)

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val cave = LocationCave.instantiate(engine, menuStage, "spookyCave", nodeRoomMesh)
    val playerCharacter = CharacterPlayerCharacter.instantiate(engine, menuStage, location = cave, camera = null)

    val sdc = ShapeDrawerConfig(menuBatch)
    val drawer = sdc.getDrawer()

    @Suppress("NewApi")
    override fun render(delta: Float) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        InputHandler.handleInput(menuCamera)

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.UP) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.FORWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.BACKWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.LEFT }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.RIGHT }
            Gdx.input.isKeyJustPressed(Input.Keys.Z) -> { MessageChannel.ECS_FSM_BRIDGE.send(IEntity.getFor(playerCharacter)!!, "hello from ${IEntity.getFor(playerCharacter)!!.entityName}") }
        }

        menuCamera.update()
        menuBatch.projectionMatrix = menuCamera.combined

        menuBatch.use {
            cave[LocationCave.mapper]!!.nodeRoomMesh.render(menuBatch)
        }

        menuStage.draw()
        menuStage.act()

        menuBatch.use {
            font.drawLabel(menuBatch, Point(300f, 200f), "${playerCharacter[ActionMoveComponent.mapper]!!.currentNode}\n${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeLink}\n" +
                    "nodeRoom:${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.uuid}\nlength:${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeLink.getDistance(nodeRoomMesh.nodesMap.keys)}\n" +
                    "occupiedNodes:${cave[LocationCave.mapper]!!.nodeRoomMesh.numOccupiedNodes()}/${cave[LocationCave.mapper]!!.nodeRoomMesh.nodesMap.size}", RenderPalette.ForeColors[1])
        }

        engine.update(delta)

    }

    override fun hide() {
    }

    override fun show() {
        Render.initRender(menuCamera, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid, Render.cameraAngle)

        cave[LocationCave.mapper]!!.nodeRoomMesh.buildWallsAndPath()
        cave[LocationCave.mapper]!!.nodeRoomMesh.renderWallsAndPath()

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
        menuCamera.viewportWidth = width.toFloat()
        menuCamera.viewportHeight = height.toFloat()
    }

    override fun dispose() {
        assets.dispose()
    }
}