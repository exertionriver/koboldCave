package org.river.exertion.demos.ai

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.app.KtxScreen
import ktx.ashley.get
import ktx.graphics.use
import ktx.scene2d.*
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
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.perceivedSymbols.FoodSymbol
import org.river.exertion.ai.internalSymbol.perceivedSymbols.HungerSymbol
import org.river.exertion.ai.internalSymbol.perceivedSymbols.MomentElapseSymbol
import org.river.exertion.ai.messaging.SymbolDisplayMessage
import org.river.exertion.ai.messaging.TimingTableMessage
import org.river.exertion.ecs.component.SymbologyComponent
import org.river.exertion.ecs.component.action.ActionSimpleDecideMoveComponent
import org.river.exertion.ecs.entity.character.CharacterKobold
import org.river.exertion.ecs.entity.character.ICharacter
import org.river.exertion.s2d.ui.*

class DemoBasicAI(private val menuBatch: Batch,
                  private val font: BitmapFont,
                  private val assets: AssetManager,
                  private val menuStage: Stage,
                  private val menuCamera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val character = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }

    @Suppress("NewApi")
    override fun render(delta: Float) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        InputHandler.handleInput(menuCamera)

/*        when {
            Gdx.input.isKeyJustPressed(Input.Keys.Z) -> { menuStage.root.findActor<Table>("planTable").add("test ${LocalDateTime.now()}").row() }
            Gdx.input.isKeyJustPressed(Input.Keys.X) -> { val staticTable = menuStage.root.findActor<Table>("planTable"); if (staticTable.children.size > 0) staticTable.getChild(0).remove() }
            Gdx.input.isKeyJustPressed(Input.Keys.C) -> { menuStage.root.findActor<Table>("feelingTable").add("test ${LocalDateTime.now()}").row() }
            Gdx.input.isKeyJustPressed(Input.Keys.B) -> { val scrollTable = menuStage.root.findActor<Table>("feelingTable") ; if (scrollTable.children.size > 0) scrollTable.getChild(0).remove() }
            Gdx.input.isKeyJustPressed(Input.Keys.N) -> { menuStage.root.findActor<Table>("perceptionTable").add("test ${LocalDateTime.now()}").row() }
            Gdx.input.isKeyJustPressed(Input.Keys.M) -> { val scrollTable = menuStage.root.findActor<Table>("perceptionTable") ; if (scrollTable.children.size > 0) scrollTable.getChild(0).remove() }
       }
    */
        menuCamera.update()
        menuBatch.projectionMatrix = menuCamera.combined

        menuStage.draw()
        menuStage.act()

        UITimingTable.send(timingTableMessage = TimingTableMessage(timingType = TimingTableMessage.TimingEntryType.RENDER, label = "render", value = delta))
        UISymbolDisplay.send(symbolDisplayMessage = SymbolDisplayMessage(symbolDisplay = SymbologyComponent.getFor(character)!!.internalSymbology.internalSymbolDisplay))

        engine.update(delta)
    }

    override fun hide() {
    }

    override fun show() {
        Render.initRender(menuCamera)

        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("skin/clean-crispy-ui.json"))

        menuStage.addActor(UITimingTable(Scene2DSkin.defaultSkin))
        menuStage.addActor(UISymbolDisplay(Scene2DSkin.defaultSkin))

        SymbologyComponent.getFor(character)!!.internalSymbology.internalSymbolDisplay.symbolDisplay = mutableSetOf(
                SymbolInstance(HungerSymbol, cycles = 1f, position = .55f),
                SymbolInstance(FoodSymbol, cycles = 12f, position = .6f).apply { this.consumeCapacity = 1f; this.handleCapacity = 3f},
                SymbolInstance(MomentElapseSymbol, cycles = -1f, position = .4f)
        )
//        menuStage.addActor(UIFeelingTable(Scene2DSkin.defaultSkin))
//        menuStage.addActor(UIPerceptionTable(Scene2DSkin.defaultSkin))

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