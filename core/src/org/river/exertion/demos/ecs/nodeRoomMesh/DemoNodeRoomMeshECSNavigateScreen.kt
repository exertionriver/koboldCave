package org.river.exertion.demos.ecs.nodeRoomMesh

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
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
import org.river.exertion.ecs.component.entity.EntityPlayerCharacter
import org.river.exertion.ecs.component.environment.EnvironmentCave
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.geom.node.nodeMesh.NodeRoom
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsAndPath
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.render
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.renderWallsAndPath
import org.river.exertion.Render
import org.river.exertion.RenderPalette
import org.river.exertion.s2d.ActorKobold
import org.river.exertion.s2d.ActorPlayerCharacter

class DemoNodeRoomMeshECSNavigateScreen(private val batch: Batch,
                                        private val font: BitmapFont,
                                        private val assets: AssetManager,
                                        private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    var nodeRoomMesh = NodeRoomMesh(NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f)))

    val engine = PooledEngine().apply { ActionPlexSystem(this) }
    val cave = EnvironmentCave.instantiate(engine, "spookyCave", nodeRoomMesh)
    val playerCharacter = EntityPlayerCharacter.instantiate(engine, cave = cave, camera = null)

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    val controlAreaCamera = OrthographicCamera()

    override fun render(delta: Float) {

//        println ("delta:$delta, rps:${1f/delta}")
//        Gdx.gl.glViewport(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight())

        val prevExitNodes = cave[EnvironmentCave.mapper]!!.nodeRoomMesh.activatedExitNodes.size
 //       val nodeRoomIdx = cave[EnvironmentCave.mapper]!!.nodeRoomMesh.getCurrentRoomIdx(playerCharacter[ActionMoveComponent.mapper]!!.currentNode)
        cave[EnvironmentCave.mapper]!!.nodeRoomMesh.inactiveExitNodesInRange(playerCharacter[ActionMoveComponent.mapper]!!.currentNode).forEach {
            cave[EnvironmentCave.mapper]!!.nodeRoomMesh.activateExitNode( playerCharacter[ActionMoveComponent.mapper]!!.currentNode, it ) }
        val currExitNodes = cave[EnvironmentCave.mapper]!!.nodeRoomMesh.activatedExitNodes.size

        if (prevExitNodes != currExitNodes) {
            cave[EnvironmentCave.mapper]!!.nodeRoomMesh.buildWallsAndPath()
            cave[EnvironmentCave.mapper]!!.nodeRoomMesh.renderWallsAndPath()
        }

        InputHandler.handleInput(camera)

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.UP) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.FORWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.BACKWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.LEFT }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.RIGHT }
        }

        batch.projectionMatrix = camera.combined
        camera.update()

        batch.use {
            cave[EnvironmentCave.mapper]!!.nodeRoomMesh.render(batch)

            engine.entities.filter { checkEntity -> checkEntity.isEntity() && checkEntity.contains(EntityKobold.mapper) }.forEach { renderKobold ->
                ActorKobold.render(batch, renderKobold[ActionMoveComponent.mapper]!!.currentPosition, renderKobold[ActionMoveComponent.mapper]!!.currentAngle)
            }
            ActorPlayerCharacter.render(batch, playerCharacter[ActionMoveComponent.mapper]!!.currentPosition, playerCharacter[ActionMoveComponent.mapper]!!.currentAngle)

/*            drawer.filledCircle(playerCharacter[ActionMoveComponent.mapper]!!.currentNode.position, 2F, RenderPalette.ForeColors[3])
            drawer.filledCircle(playerCharacter[ActionMoveComponent.mapper]!!.forwardNextNodeAngle.first.position, 2F, RenderPalette.ForeColors[4])
            drawer.filledCircle(playerCharacter[ActionMoveComponent.mapper]!!.backwardNextNodeAngle.first.position, 2F, RenderPalette.ForeColors[5])
  */      }

        controlAreaCamera.update()
        batch.projectionMatrix = controlAreaCamera.combined

        batch.use {
            font.drawLabel(batch, Point(300f, 150f), "${playerCharacter[ActionMoveComponent.mapper]!!.currentNode}\n${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeLink}\n" +
                    "nodeRoom:${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.uuid}\nlength:${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeLink.getDistance(nodeRoomMesh.nodesMap.keys)}\n" +
                    "nodeRenderState:${playerCharacter[ActionMoveComponent.mapper]!!.currentNode.attributes.renderState}", RenderPalette.ForeColors[1])
        }

        engine.update(delta)
    }

    override fun hide() {
    }

    override fun show() {
     //   val nodeRoomIdx = cave[EnvironmentCave.mapper]!!.nodeRoomMesh.getCurrentRoomIdx(playerCharacter[ActionMoveComponent.mapper]!!.currentNode)
        playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.inactiveExitNodesInRange(playerCharacter[ActionMoveComponent.mapper]!!.currentNode).forEach { nodeRoomMesh.activateExitNode( playerCharacter[ActionMoveComponent.mapper]!!.currentNode, it ) }

        Render.initRender(camera, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid, Render.cameraAngle)
        controlAreaCamera.setToOrtho(false, Gdx.graphics.getWidth().toFloat(), Gdx.graphics.getHeight().toFloat())

        cave[EnvironmentCave.mapper]!!.nodeRoomMesh.buildWallsAndPath()
        cave[EnvironmentCave.mapper]!!.nodeRoomMesh.renderWallsAndPath()

        // start the playback of the background music when the screen is shown
        MusicAssets.values().forEach { assets.load(it) }
        assets.finishLoading()
        //println("done!")
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