package org.river.exertion.demos.ai

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import org.river.exertion.InputHandler
import org.river.exertion.Render
import org.river.exertion.ai.internalFacet.AngerFacet
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.ornaments.SocialOrnament
import org.river.exertion.ai.internalSymbol.perceivedSymbols.*
import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.memory.KnowledgeSourceType
import org.river.exertion.ai.memory.MemoryInstance
import org.river.exertion.ai.messaging.*
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.perception.PerceivedAttribute
import org.river.exertion.ai.perception.PerceivedNoumenon
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ecs.component.*
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.action.ActionSimpleDecideMoveComponent
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.ecs.entity.character.CharacterKobold
import org.river.exertion.ecs.system.SystemManager
import org.river.exertion.s2d.ui.*

class DemoBasicAI(private val menuBatch: Batch,
                  private val assets: AssetManager,
                  private val menuStage: Stage,
                  private val menuCamera: OrthographicCamera) : KtxScreen {

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val character = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }
    val secondCharacter = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }

    val ordinarySound = ExternalPhenomenaInstance().apply {
        this.type = ExternalPhenomenaType.AUDITORY
        this.direction = 120f
        this.magnitude = 50f
        this.location = Vector3(10f, 10f, 10f)
        this.loss = .2f
    }

    val secondCharacterPN = PerceivedNoumenon().apply {
        this.perceivedAttributes.add(PerceivedAttribute(attributeInstance = IEntity.getFor(secondCharacter)!!.noumenonInstance.pollRandomAttributeInstance(ExternalPhenomenaType.AUDITORY), knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE))); this.instanceName = IEntity.getFor(secondCharacter)!!.entityName; this.noumenonType = NoumenonType.INDIVIDUAL; isNamed = true
    }
    val secondCharacterMemory = MemoryInstance(secondCharacterPN, FriendSymbol)

    val hungerDisplay = mutableSetOf(
        SymbolInstance(AnxietySymbol, cycles = 1f, position = .1f),
        SymbolInstance(HungerSymbol, cycles = 1f, position = .55f),
        SymbolInstance(FoodSymbol, cycles = 12f, position = .6f).apply { this.consumeCapacity = 1f; this.handleCapacity = 3f},
        SymbolInstance(MomentElapseSymbol, cycles = -1f, position = .4f)
    )

    @Suppress("NewApi")
    override fun render(delta: Float) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        InputHandler.handleInput(menuCamera)

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE) -> MomentComponent.getFor(character)!!.systemMoment = if (MomentComponent.getFor(character)!!.systemMoment == 0f) 10f else 0f
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> MomentComponent.getFor(character)!!.systemMoment += 5f
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> MomentComponent.getFor(character)!!.systemMoment -= 5f
            Gdx.input.isKeyJustPressed(Input.Keys.UP) -> ConditionComponent.getFor(character)!!.internalCondition.mIntAnxiety += .05f
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> ConditionComponent.getFor(character)!!.internalCondition.mIntAnxiety -= .05f
            //TODO: update facet at a time via INT_FACET_MODIFY
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) -> FacetComponent.getFor(character)!!.internalFacetState.internalState.first { it.facetObj == AngerFacet }.magnitude += .05f
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) -> FacetComponent.getFor(character)!!.internalFacetState.internalState.first { it.facetObj == AngerFacet }.magnitude -= .05f

            //TODO: simplify symbol-ornament messaging
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_5) -> {
                MessageChannel.INT_SYMBOL_ADD_ORNAMENT.send(IEntity.getFor(character)!!,
                OrnamentMessage(symbolInstance = SymbologyComponent.getFor(character)!!.internalSymbology.internalSymbolDisplay.symbolDisplay.filter { it.symbolObj == FriendSymbol }.first(), ornament = SocialOrnament)
                )
                SymbologyComponent.getFor(character)!!.internalSymbology.internalSymbolDisplay.symbolDisplay.filter { it.symbolObj == FriendSymbol }.first().recalcTargetPosition()
            }
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_0) -> MessageChannel.ADD_EXT_PHENOMENA.send(IEntity.getFor(secondCharacter)!!, ordinarySound)
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) -> SymbologyComponent.getFor(character)!!.internalSymbology.internalSymbolDisplay.symbolDisplay.addAll(hungerDisplay)
       }

        menuCamera.update()
        menuBatch.projectionMatrix = menuCamera.combined

        menuStage.draw()
        menuStage.act()

        MessageChannel.UI_TIMING_DISPLAY.send(null, TimingTableMessage(timingType = TimingTableMessage.TimingEntryType.RENDER, label = "render", value = delta))
        MessageChannel.UI_TIMING_DISPLAY.send(null, TimingTableMessage(timingType = TimingTableMessage.TimingEntryType.CHARACTER, label = "systemMoment", value = MomentComponent.getFor(character)!!.systemMoment))

        MessageChannel.UI_SYMBOL_DISPLAY.send(null, SymbolDisplayMessage(symbolDisplay = SymbologyComponent.getFor(character)!!.internalSymbology.internalSymbolDisplay))
        MessageChannel.UI_FOCUS_DISPLAY.send(null, FocusDisplayMessage(focusDisplay = SymbologyComponent.getFor(character)!!.internalSymbology.internalFocusDisplay))

        MessageChannel.UI_ANXIETY_BAR.send(null, AnxietyBarMessage(value = ConditionComponent.getFor(character)!!.internalCondition.mIntAnxiety))
        MessageChannel.UI_FACET_DISPLAY.send(null, FacetTableMessage(internalFacetInstancesState = FacetComponent.getFor(character)!!.internalFacetState))

        MessageChannel.UI_MANIFEST_DISPLAY.send(null, ManifestDisplayMessage(internalManifest = ManifestComponent.getFor(character)!!.internalManifest))
        MessageChannel.UI_MEMORY_DISPLAY.send(null, MemoryDisplayMessage(internalMemory = MemoryComponent.getFor(character)!!.internalMemory))

        engine.update(delta)
    }

    override fun hide() {
    }

    override fun show() {
        Render.initRender(menuCamera)

        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("skin/clean-crispy-ui.json"))

        menuStage.addActor(UITimingTable(Scene2DSkin.defaultSkin))
        menuStage.addActor(UISymbolDisplay(Scene2DSkin.defaultSkin))
        menuStage.addActor(UIFocusDisplay(Scene2DSkin.defaultSkin))
        menuStage.addActor(UIAnxietyBar(Scene2DSkin.defaultSkin))
        menuStage.addActor(UIAnxietyBarTable(Scene2DSkin.defaultSkin))
        menuStage.addActor(UIFacetTable(Scene2DSkin.defaultSkin))
        menuStage.addActor(UIInternalManifestDisplay(Scene2DSkin.defaultSkin))
        menuStage.addActor(UIExternalManifestDisplay(Scene2DSkin.defaultSkin))
        menuStage.addActor(UIMemoryTable(Scene2DSkin.defaultSkin))

       MemoryComponent.getFor(character)!!.internalMemory.longtermMemory.noumenaRegister.add(secondCharacterMemory)
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