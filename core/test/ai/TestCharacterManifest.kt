package ai

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.math.Vector3
import org.junit.jupiter.api.Test
import org.river.exertion.MessageIds
import org.river.exertion.ai.manifest.InternalStateBiases
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.btree.v0_1.*


@ExperimentalUnsignedTypes
class TestCharacterManifest {

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
        this.origin = InternalStateBiases.none()
        this.arising = InternalStateBiases.desire()
    }

    @Test
    fun testRandomPhenomena() {

        MessageManager.getInstance().dispatchMessage(secondCharacter, MessageIds.EXT_PHENOMENA.id(), ordinarySound)

        character.update(character.actionMoment * 2 + 0.01f)

        character.characterManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        MessageManager.getInstance().dispatchMessage(secondCharacter, MessageIds.EXT_PHENOMENA.id(), weirdSound)

        character.update(character.actionMoment * 2 + 0.01f)

        character.characterManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        MessageManager.getInstance().dispatchMessage(secondCharacter, MessageIds.INT_PHENOMENA.id(), scared)

        character.update(character.actionMoment * 2 + 0.01f)

        println("Auditory Channel")
        character.characterManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }
        println("Wisdom Channel")
        character.characterManifest.getManifest(ExternalPhenomenaType.WISDOM).joinedList().forEach { println("$it : ${it.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

    }
}