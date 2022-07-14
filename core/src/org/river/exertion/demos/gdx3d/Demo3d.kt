package org.river.exertion.demos.gdx3d

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.StretchViewport
import ktx.app.KtxScreen
import ktx.ashley.get
import ktx.graphics.use
import ktx.scene2d.*
import org.river.exertion.*
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.entity.character.CharacterPlayerCharacter
import org.river.exertion.ecs.entity.location.LocationCave
import org.river.exertion.ecs.system.SystemManager
import org.river.exertion.geom.node.nodeMesh.NodeRoom
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsAndPath
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.renderWallsAndPath
import org.river.exertion.Render
import org.river.exertion.RenderPalette
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.render3d

class Demo3d(private val menuBatch: Batch,
             private val gameBatch: ModelBatch,
             private val font: BitmapFont,
             private val assets: AssetManager,
             private val menuStage: Stage,
             private val menuCamera: OrthographicCamera,
             private val gameCamera: PerspectiveCamera) : KtxScreen {

    val horizOffset = KoboldCave.initViewportWidth / 11
    val vertOffset = KoboldCave.initViewportHeight / 11
    val labelVert = Point(0F, KoboldCave.initViewportHeight * 2 / 32)

    var nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f))
    var nodeRoomMesh = NodeRoomMesh(nodeRoom)

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val cave = LocationCave.instantiate(engine, menuStage, "spookyCave", nodeRoomMesh)
    val playerCharacter = CharacterPlayerCharacter.instantiate(engine, menuStage, location = cave, camera = null)

//    val controlAreaCamera = OrthographicCamera()
    val gameAreaViewport = StretchViewport(KoboldCave.initViewportWidth, KoboldCave.initViewportHeight, gameCamera)

    val sdc = ShapeDrawerConfig(menuBatch)
    val drawer = sdc.getDrawer()

    val originPosition = Vector3(playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position.x, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position.y, 0f)
    val camPosition = Vector3(playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position.x, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position.y, 0f)

    val environment = Environment()

    @Suppress("NewApi")
    override fun render(delta: Float) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        InputHandler.handleInput(gameCamera, originPosition)

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.UP) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.FORWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.BACKWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.LEFT }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.RIGHT }
        }

        gameCamera.update()
        gameBatch.begin(gameCamera)
        cave[LocationCave.mapper]!!.nodeRoomMesh.render3d(gameBatch, environment)
        //gameBatch.render(modelInstance, environment)
        gameBatch.end()

//        menuCamera.update()
//        menuBatch.projectionMatrix = menuCamera.combined

//        menuBatch.use {
//            cave[LocationCave.mapper]!!.nodeRoomMesh.render(menuBatch)
//        }

//        menuStage.draw()
//        menuStage.act()

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
        Render.initRender(gameCamera, camPosition, originPosition)
        Render.initRender(menuCamera, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid, Render.cameraAngle)

        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("skin/clean-crispy-ui.json"))

//        modelPoint = modelBuilder.createSphere(10f, 10f, 10f, 20, 20, Material(createDiffuse(Color.BLUE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
//        modelInstance = ModelInstance(modelPoint, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position.x, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position.y, 0f)
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 0.4f))
        environment.add(DirectionalLight().set(1.0f, 1.0f, 1.0f, -1f, -0.8f, -0.2f))

        cave[LocationCave.mapper]!!.nodeRoomMesh.buildWallsAndPath()
        cave[LocationCave.mapper]!!.nodeRoomMesh.renderWallsAndPath()

        // start the playback of the background music when the screen is shown
//        MusicAssets.values().forEach { assets.load(it) }
//        assets.finishLoading()
//        println("done!")
//        assets[MusicAssets.NavajoNight].apply { isLooping = true }.play()
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        gameCamera.viewportWidth = width.toFloat()
        gameCamera.viewportHeight = height.toFloat()
        menuCamera.viewportWidth = width.toFloat()
        menuCamera.viewportHeight = height.toFloat()
    }

    override fun dispose() {
        assets.dispose()
    }
}