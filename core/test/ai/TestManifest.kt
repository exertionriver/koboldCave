package ai

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.math.Vector3
import org.junit.jupiter.api.Test
import org.river.exertion.MessageIds
import org.river.exertion.ai.internalFacet.AngerFacet.angerFacet
import org.river.exertion.ai.internalFacet.ConfusionFacet.confusionFacet
import org.river.exertion.ai.internalFacet.DoubtFacet.doubtFacet
import org.river.exertion.ai.internalFacet.FearFacet.fearFacet
import org.river.exertion.ai.internalFacet.InternalFacetAttribute.Companion.internalFacetAttribute
import org.river.exertion.ai.internalState.InternalFacetAttributesState
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.ecs.component.ManifestComponent
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.action.ActionSimpleDecideMoveComponent
import org.river.exertion.ecs.entity.character.CharacterKobold
import org.river.exertion.ecs.system.SystemManager


@ExperimentalUnsignedTypes
class TestManifest {

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

    val weirdSound = ExternalPhenomenaInstance().apply {
        this.type = ExternalPhenomenaType.AUDITORY
        this.direction = 120f
        this.magnitude = 120f
        this.location = Vector3(30f, 30f, 30f)
        this.loss = .3f
    }

    val scared = InternalPhenomenaInstance().apply {
        this.arisenFacet = fearFacet { magnitude = 0.6f }
    }

    @Test
    fun testRandomPhenomena() {

        MessageManager.getInstance().dispatchMessage(CharacterKobold.getFor(secondCharacter), MessageIds.EXT_PHENOMENA.id(), ordinarySound)

        engine.update(CharacterKobold.getFor(character)!!.moment / 10)

        println("Auditory Channel after ordinary, character:")
        ManifestComponent.getFor(character)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        println("Auditory Channel after ordinary, second character:")
        ManifestComponent.getFor(secondCharacter)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        MessageManager.getInstance().dispatchMessage(CharacterKobold.getFor(secondCharacter), MessageIds.EXT_PHENOMENA.id(), weirdSound)

        engine.update(CharacterKobold.getFor(character)!!.moment / 10)

        println("Auditory Channel after weird sound, character:")
        ManifestComponent.getFor(character)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        println("Auditory Channel after weird sound, second character:")
        ManifestComponent.getFor(secondCharacter)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        MessageManager.getInstance().dispatchMessage(CharacterKobold.getFor(secondCharacter), MessageIds.INT_PHENOMENA.id(), scared)

        engine.update(CharacterKobold.getFor(character)!!.moment / 10)

        println("final Auditory Channel, character")
        ManifestComponent.getFor(character)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }
        println("final Auditory Channel, second character")
        ManifestComponent.getFor(secondCharacter)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

    //        ManifestComponent.getFor(character)!!.internalManifest.getManifest(ExternalPhenomenaType.WISDOM).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

    }

}