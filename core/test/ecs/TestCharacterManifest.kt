package ecs

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


@ExperimentalUnsignedTypes
class TestCharacterManifest {
/*
    var character = KoboldCharacter()
    var secondCharacter = KoboldCharacter()

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
        this.arisenFacet = fearFacet { 0.6f }
    }

    @Test
    fun testRandomPhenomena() {

        MessageManager.getInstance().dispatchMessage(secondCharacter, MessageIds.EXT_PHENOMENA.id(), ordinarySound)

        character.update(character.actionMoment * 2 + 0.01f)

        character.characterManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        MessageManager.getInstance().dispatchMessage(secondCharacter, MessageIds.EXT_PHENOMENA.id(), weirdSound)

        character.update(character.actionMoment * 2 + 0.01f)

        character.characterManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        MessageManager.getInstance().dispatchMessage(secondCharacter, MessageIds.INT_PHENOMENA.id(), scared)

        character.update(character.actionMoment * 2 + 0.01f)

        println("Auditory Channel")
        character.characterManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }
        println("Wisdom Channel")
        character.characterManifest.getManifest(ExternalPhenomenaType.WISDOM).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

    }

    @Test
    fun testOriginArisingProjections() {
        val testState = InternalFacetAttributesState().apply { this.internalFacetAttributes = mutableSetOf(
            internalFacetAttribute { internalFacetInstance = confusionFacet {}; origin = 0.2f; arising = 0.5f },
            internalFacetAttribute { internalFacetInstance = angerFacet {}; origin = 0.3f; arising = 0.7f },
            internalFacetAttribute { internalFacetInstance = fearFacet {}; origin = 0.4f; arising = 0.8f },
            internalFacetAttribute { internalFacetInstance = doubtFacet {}; origin = 0f; arising = 0.1f }
        ) }

        (0..10).forEach { mAnxiety ->
            println("mAnxiety : ${mAnxiety / 10f}")
            testState.projections(mAnxiety / 10f).forEachIndexed { idx, it -> println("slot($idx) : ${it?.arisenFacet?.facet()?.type?.tag()}, ${it?.arisenFacet?.magnitude}") }
        }
    }
*/
}