package org.river.exertion.koboldCave.screen.nodeRoomMesh

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
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
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsAndPath
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh.Companion.renderWallsAndPathLos
import org.river.exertion.koboldCave.screen.Render
import org.river.exertion.koboldCave.screen.RenderPalette
import org.river.exertion.koboldCave.screen.render

class DemoNodeRoomMeshECSRotateNavigateScreen(private val batch: Batch,
                                              private val font: BitmapFont,
                                              private val assets: AssetManager,
                                              private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    var nodeRoomMesh = NodeRoomMesh(NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f)))

    val visualRadius = NextDistancePx * 1.5f

    val engine = PooledEngine().apply { ActionPlexSystem(this) }
    val cave = EnvironmentCave.instantiate(engine, "spookyCave", nodeRoomMesh)
    val playerCharacter = EntityPlayerCharacter.instantiate(engine, cave = cave, camera = camera)

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    val graphicsCamera = OrthographicCamera()
//    val graphicsViewport = ExtendViewport(Gdx.graphics.getWidth().toFloat(), Gdx.graphics.getHeight().toFloat(), graphicsCamera)

    val controlAreaCamera = OrthographicCamera()
//    val controlAreaViewport = ExtendViewport(Gdx.graphics.getWidth().toFloat(), Gdx.graphics.getHeight().toFloat(), controlAreaCamera)

    var imgAlpha = 0f
    var imgAlphaAsc = false
    lateinit var imgSprite : Texture

    val spriteBatch = SpriteBatch()

    override fun render(delta: Float) {

//        println ("delta:$delta, rps:${1f/delta}")
//        Gdx.gl.glViewport(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight())

        val prevExitNodes = cave[EnvironmentCave.mapper]!!.nodeRoomMesh.activatedExitNodes.size
//        val nodeRoomIdx = cave[EnvironmentCave.mapper]!!.nodeRoomMesh.getCurrentRoomIdx(playerCharacter[ActionMoveComponent.mapper]!!.currentNode)
        cave[EnvironmentCave.mapper]!!.nodeRoomMesh.inactiveExitNodesInRange(playerCharacter[ActionMoveComponent.mapper]!!.currentNode).forEach {
            cave[EnvironmentCave.mapper]!!.nodeRoomMesh.activateExitNode( playerCharacter[ActionMoveComponent.mapper]!!.currentNode, it ) }
        val currExitNodes = cave[EnvironmentCave.mapper]!!.nodeRoomMesh.activatedExitNodes.size

        if (prevExitNodes != currExitNodes) {
            cave[EnvironmentCave.mapper]!!.nodeRoomMesh.buildWallsAndPath()
        }
        val losMap = cave[EnvironmentCave.mapper]!!.nodeRoomMesh.renderWallsAndPathLos(playerCharacter[ActionMoveComponent.mapper]!!.currentPosition, playerCharacter[ActionMoveComponent.mapper]!!.currentAngle, visualRadius)

        InputHandler.handleInput(camera)

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.UP) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.FORWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.BACKWARD }
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.LEFT }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> { playerCharacter[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.RIGHT }
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE) -> { imgAlphaAsc = !imgAlphaAsc }
        }

        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        spriteBatch.use {
            controlAreaCamera.update()
            spriteBatch.projectionMatrix = controlAreaCamera.combined

            spriteBatch.draw(imgSprite, 50f, 400f, 500f, 300f )
            val curColor = spriteBatch.color
//            println("imgAlphaAsc: $imgAlphaAsc, imgAlpha: $imgAlpha")
            imgAlpha = if (imgAlpha > 1f) 1f else if (imgAlpha < 0f) 0f else imgAlpha
            imgAlpha = if (imgAlphaAsc) imgAlpha + 0.02f else imgAlpha - 0.02f
            spriteBatch.setColor(curColor.r, curColor.g, curColor.b, imgAlpha)

        }

        batch.projectionMatrix = camera.combined
        camera.update()

        batch.use {

            cave[EnvironmentCave.mapper]!!.nodeRoomMesh.render(batch)

            engine.entities.filter { checkEntity -> checkEntity.isEntity() && checkEntity.contains(EntityKobold.mapper) }.forEach { renderKobold ->
                Kobold.renderLos(batch, losMap, playerCharacter[ActionMoveComponent.mapper]!!.currentPosition, renderKobold[ActionMoveComponent.mapper]!!.currentPosition, renderKobold[ActionMoveComponent.mapper]!!.currentAngle)
            }
            PlayerCharacter.render(batch, playerCharacter[ActionMoveComponent.mapper]!!.currentPosition, playerCharacter[ActionMoveComponent.mapper]!!.currentAngle)
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
//        val nodeRoomIdx = cave[EnvironmentCave.mapper]!!.nodeRoomMesh.getCurrentRoomIdx(playerCharacter[ActionMoveComponent.mapper]!!.currentNode)
        playerCharacter[ActionMoveComponent.mapper]!!.currentNodeRoom.inactiveExitNodesInRange(playerCharacter[ActionMoveComponent.mapper]!!.currentNode).forEach { nodeRoomMesh.activateExitNode( playerCharacter[ActionMoveComponent.mapper]!!.currentNode, it ) }

        Render.initRender(playerCharacter[ActionMoveComponent.mapper]!!.camera!!, playerCharacter[ActionMoveComponent.mapper]!!.currentNode, playerCharacter[ActionMoveComponent.mapper]!!.currentAngle)
        controlAreaCamera.setToOrtho(false, Gdx.graphics.getWidth().toFloat(), Gdx.graphics.getHeight().toFloat())

        nodeRoomMesh.buildWallsAndPath()

        TextureAssets.values().forEach { assets.load(it) }

        // start the playback of the background music when the screen is shown
        MusicAssets.values().forEach { assets.load(it) }
        assets.finishLoading()
//        println("done!")

        assets[MusicAssets.NavajoNight].apply { isLooping = true }.play()
        imgSprite = assets[TextureAssets.Suenos]
//        imgSprite.setAlpha(imgAlpha)
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