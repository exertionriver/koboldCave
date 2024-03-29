package org.river.exertion.demos.ecs.nodeRoomMesh

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxScreen
import ktx.ashley.get
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.assets.*
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.entity.character.CharacterPlayerCharacter
import org.river.exertion.ecs.entity.location.LocationCave
import org.river.exertion.ecs.system.SystemManager
import org.river.exertion.geom.node.nodeMesh.NodeRoom
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsAndPath
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.render
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.renderWallsAndPath
import org.river.exertion.Render
import org.river.exertion.RenderPalette

class DemoNodeRoomMeshECSNavigateScreen(private val batch: Batch,
                                        private val font: BitmapFont,
                                        private val assets: AssetManager,
                                        private val stage: Stage,
                                        private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = KoboldCave.initViewportWidth / 11
    val vertOffset = KoboldCave.initViewportHeight / 11
    val labelVert = Point(0F, KoboldCave.initViewportHeight * 2 / 32)

    var nodeRoomMesh = NodeRoomMesh(NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f)))

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val cave = LocationCave.instantiate(engine, stage, "spookyCave", nodeRoomMesh)
    val playerCharacter = CharacterPlayerCharacter.instantiate(engine, stage, location = cave, camera = null)

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    val controlAreaCamera = OrthographicCamera()

    override fun render(delta: Float) {

//        println ("delta:$delta, rps:${1f/delta}")
//        Gdx.gl.glViewport(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight())

        val prevExitNodes = cave[LocationCave.mapper]!!.nodeRoomMesh.activatedExitNodes.size
 //       val nodeRoomIdx = cave[EnvironmentCave.mapper]!!.nodeRoomMesh.getCurrentRoomIdx(playerCharacter[ActionMoveComponent.mapper]!!.currentNode)
        cave[LocationCave.mapper]!!.nodeRoomMesh.inactiveExitNodesInRange(playerCharacter[ActionMoveComponent.mapper]!!.currentNode).forEach {
            cave[LocationCave.mapper]!!.nodeRoomMesh.activateExitNode( playerCharacter[ActionMoveComponent.mapper]!!.currentNode, it ) }
        val currExitNodes = cave[LocationCave.mapper]!!.nodeRoomMesh.activatedExitNodes.size

        if (prevExitNodes != currExitNodes) {
            cave[LocationCave.mapper]!!.nodeRoomMesh.buildWallsAndPath()
            cave[LocationCave.mapper]!!.nodeRoomMesh.renderWallsAndPath()
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
            cave[LocationCave.mapper]!!.nodeRoomMesh.render(batch)

//            engine.entities.filter { checkEntity -> checkEntity.isEntity() && checkEntity.contains(EntityKobold.mapper) }.forEach { renderKobold ->
//                ActorKobold.render(batch, renderKobold[ActionMoveComponent.mapper]!!.currentPosition, renderKobold[ActionMoveComponent.mapper]!!.currentAngle)
 //           }
  //          ActorPlayerCharacter.render(batch, playerCharacter[ActionMoveComponent.mapper]!!.currentPosition, playerCharacter[ActionMoveComponent.mapper]!!.currentAngle)

/*            drawer.filledCircle(playerCharacter[ActionMoveComponent.mapper]!!.currentNode.position, 2F, RenderPalette.ForeColors[3])
            drawer.filledCircle(playerCharacter[ActionMoveComponent.mapper]!!.forwardNextNodeAngle.first.position, 2F, RenderPalette.ForeColors[4])
            drawer.filledCircle(playerCharacter[ActionMoveComponent.mapper]!!.backwardNextNodeAngle.first.position, 2F, RenderPalette.ForeColors[5])
  */      }

        stage.draw()
        stage.act()

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

        cave[LocationCave.mapper]!!.nodeRoomMesh.buildWallsAndPath()
        cave[LocationCave.mapper]!!.nodeRoomMesh.renderWallsAndPath()

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