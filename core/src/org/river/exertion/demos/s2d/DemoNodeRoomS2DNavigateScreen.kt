package org.river.exertion.demos.s2d

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction
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
import org.river.exertion.Render
import org.river.exertion.RenderPalette

class DemoNodeRoomS2DNavigateScreen(private val batch: Batch,
                                    private val font: BitmapFont,
                                    private val assets: AssetManager,
                                    private val stage: Stage,
                                    private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    var nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f))
    var nodeRoomMesh = NodeRoomMesh(nodeRoom)

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val cave = LocationCave.instantiate(engine, stage, "spookyCave", nodeRoomMesh)
    val playerCharacter = CharacterPlayerCharacter.instantiate(engine, stage, location = cave, camera = camera)

    val controlAreaCamera = OrthographicCamera()
//    val controlAreaViewport = ExtendViewport(Gdx.graphics.getWidth().toFloat(), Gdx.graphics.getHeight().toFloat(), controlAreaCamera)
    val s2aBig = ScaleToAction().apply { this.setScale(2f); this.duration = 5f }
    val s2aSmall = ScaleToAction().apply { this.setScale(0.75f); this.duration = 3f }

//    val sdc = ShapeDrawerConfig(batch)
//    val drawer = sdc.getDrawer()

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

//        batch.use {
//            ActorCave.render(batch, cave[LocationCave.mapper]!!.nodeRoomMesh)

//            cave[EnvironmentCave.mapper]!!.nodeRoomMesh.nodesMap.keys.filter { it.attributes.occupied }.forEach {
//                drawer.filledCircle(it.position, 2F, RenderPalette.ForeColors[3])
//            }
//        }

        stage.draw()
        stage.act()

        controlAreaCamera.update()
        batch.projectionMatrix = controlAreaCamera.combined

        batch.use {
            font.drawLabel(batch, Point(300f, 200f), "${playerCharacter[ActionMoveComponent.mapper]!!.currentNode}\n${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeLink}\n" +
                    "nodeRoom:${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.uuid}\nlength:${playerCharacter[ActionMoveComponent.mapper]!!.currentNodeLink.getDistance(nodeRoomMesh.nodesMap.keys)}\n" +
                    "occupiedNodes:${cave[LocationCave.mapper]!!.nodeRoomMesh.numOccupiedNodes()}/${cave[LocationCave.mapper]!!.nodeRoomMesh.nodesMap.size}", RenderPalette.ForeColors[1])
        }

        engine.update(delta)

    }

    override fun hide() {
    }

    override fun show() {
        //overhead, following character
        Render.initRender(playerCharacter[ActionMoveComponent.mapper]!!.camera!!, playerCharacter[ActionMoveComponent.mapper]!!.currentNode, playerCharacter[ActionMoveComponent.mapper]!!.currentAngle)
        //overhead
//        Render.initRender(camera, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid, Render.cameraAngle)
        controlAreaCamera.setToOrtho(false, Gdx.graphics.getWidth().toFloat(), Gdx.graphics.getHeight().toFloat())
/*
        val actor = ActorPlayerCharacter("PlayerCharacter", playerCharacter[ActionMoveComponent.mapper]!!.currentPosition, playerCharacter[ActionMoveComponent.mapper]!!.currentAngle )
        stage.addActor(actor.apply { this.addAction(s2aBig) } )
*/
 //       cave[LocationCave.mapper]!!.nodeRoomMesh.buildWallsAndPath()
 //       cave[LocationCave.mapper]!!.nodeRoomMesh.renderWallsAndPath()

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
        assets.dispose()
    }
}