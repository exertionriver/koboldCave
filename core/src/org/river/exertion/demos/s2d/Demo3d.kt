package org.river.exertion.demos.s2d

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute.createDiffuse
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.StretchViewport
import ktx.app.KtxScreen
import ktx.ashley.get
import ktx.graphics.use
import ktx.scene2d.*
import org.river.exertion.*
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.entity.character.CharacterPlayerCharacter
import org.river.exertion.ecs.component.entity.location.LocationCave
import org.river.exertion.ecs.system.action.SystemManager
import org.river.exertion.geom.node.nodeMesh.NodeRoom
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsAndPath
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.render
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.renderWallsAndPath
import org.river.exertion.Render
import org.river.exertion.RenderPalette
import java.time.LocalDateTime

class Demo3d(private val menuBatch: Batch,
             private val gameBatch: ModelBatch,
             private val font: BitmapFont,
             private val assets: AssetManager,
             private val menuStage: Stage,
             private val menuCamera: OrthographicCamera,
             private val gameCamera: PerspectiveCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    var nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f))
    var nodeRoomMesh = NodeRoomMesh(nodeRoom)

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val cave = LocationCave.instantiate(engine, menuStage, "spookyCave", nodeRoomMesh)
    val playerCharacter = CharacterPlayerCharacter.instantiate(engine, menuStage, cave = cave, camera = null)

//    val controlAreaCamera = OrthographicCamera()
    val gameAreaViewport = StretchViewport(Game.initViewportWidth, Game.initViewportHeight, gameCamera)

    val sdc = ShapeDrawerConfig(menuBatch)
    val drawer = sdc.getDrawer()

    val modelBuilder = ModelBuilder()
    var modelPoint = Model()
    lateinit var modelInstance : ModelInstance
    val environment = Environment()

    @Suppress("NewApi")
    override fun render(delta: Float) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        InputHandler.handleInput(gameCamera, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position)

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.UP) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.FORWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.BACKWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.LEFT }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.RIGHT }
            Gdx.input.isKeyJustPressed(Input.Keys.Z) -> { (menuStage.actors.find { it.name == "staticTable"} as Table).add("test ${LocalDateTime.now()}").row() }
            Gdx.input.isKeyJustPressed(Input.Keys.X) -> { (menuStage.actors.find { it.name == "staticTable"} as Table).getChild(0).remove() }
            Gdx.input.isKeyJustPressed(Input.Keys.C) -> { menuStage.root.findActor<Table>("scrollTableInner").add("test ${LocalDateTime.now()}").row() }
            Gdx.input.isKeyJustPressed(Input.Keys.B) -> { menuStage.root.findActor<Table>("scrollTableInner").getChild(0).remove() }
        }

        gameCamera.update()
        gameBatch.begin(gameCamera)
        gameBatch.render(modelInstance, environment)
        gameBatch.end()

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
        Render.initRender(gameCamera, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid, Render.cameraAngle)
        Render.initRender(menuCamera, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid, Render.cameraAngle)

        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("skin/clean-crispy-ui.json"))

        val myTable = scene2d.table {
            x = 600f
            y = 600f
            height = 100f
            name = "staticTable"
            label ("test 123")
        }
        myTable.row()

        val mySecondTable = scene2d.table {
            x = 400f
            y = 600f
            height = 100f
            name = "scrollTableOuter"
            label ("test 456")
            scrollPane {
                name = "scrollPane"
                listOf("test 789", "test 890")
                table {
                    name = "scrollTableInner"
                    label ("test 789")
                    setBounds(400f, 600f, 100f, 200f)
                }
            }
        }
//        mySecondTable.setFillParent(true)
//        (mySecondTable.getChild(0) as ScrollPane)

//        val test = ScrollPane(mySecondTable)
//            mySecondTable.add(test).expand().fill()
        menuStage.addActor(myTable)
        menuStage.addActor(mySecondTable)

        modelPoint = modelBuilder.createSphere(10f, 10f, 10f, 20, 20, Material(createDiffuse(Color.BLUE)), (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())
        modelInstance = ModelInstance(modelPoint, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position.x, playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.centroid.position.y, 0f)
//        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

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
        modelPoint.dispose()
    }
}