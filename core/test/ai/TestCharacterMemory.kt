package ai

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.math.Vector3
import org.junit.jupiter.api.Test
import org.river.exertion.MessageIds
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.btree.v0_1.*


@ExperimentalUnsignedTypes
class TestCharacterMemory {

    var character = KoboldCharacter()
    var secondCharacter = KoboldCharacter()

    val koboldGrowl = ExternalPhenomenaInstance().apply {
        this.type = ExternalPhenomenaType.AUDITORY
        this.direction = 120f
        this.magnitude = 120f
        this.location = Vector3(30f, 30f, 30f)
        this.loss = .3f
    }

    @Test
    fun testKoboldMemory() {
        character.characterMemory.perceptionRegister = KoboldMemory.memoriesPA()
        character.characterMemory.noumenaRegister = KoboldMemory.memoriesPN()

        val opinions1 = "other"
        character.characterMemory.opinions(opinions1).forEach {
            println("opinions on $opinions1: ${it.change()}")
        }
        println("opinion on $opinions1: ${character.characterMemory.opinion(opinions1)}")

        val opinions2 = "kobold"
        character.characterMemory.opinions(opinions2).forEach {
            println("opinions on $opinions2: ${it.change()}")
        }
        println("opinion on $opinions2: ${character.characterMemory.opinion(opinions2)}")

        val opinions3 = "intelligence"
        character.characterMemory.opinions(opinions3).forEach {
            println("opinions on $opinions3: ${it.change()}")
        }
        println("opinion on $opinions3: ${character.characterMemory.opinion(opinions3)}")
    }

    @Test
    fun testAddingKoboldMemoryFromManifest() {

        MessageManager.getInstance().dispatchMessage(secondCharacter, MessageIds.EXT_PHENOMENA.id(), koboldGrowl)
        character.update(character.actionMoment * 2 + 0.01f)
        character.characterManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        character.characterMemory.perceptionRegister.forEach { println("${it.perceivedNoumenaTags.first()}, ${it.attributableTag}, ${it.attributeValue}") }
        character.characterMemory.noumenaRegister.forEach { println("${it.noumenonTag}, ${it.perceivedAttributableTags.first()}") }

        val opinions4 = "growl"
        character.characterMemory.opinions(opinions4).forEach {
            println("opinions on $opinions4: ${it.change()}")
        }
        println("opinion on $opinions4: ${character.characterMemory.opinion(opinions4)}")

        val opinions5 = "kobold"
        character.characterMemory.opinions(opinions5).forEach {
            println("opinions on $opinions5: ${it.change()}")
        }
        println("opinion on $opinions5: ${character.characterMemory.opinion(opinions5)}")

        val opinions6 = secondCharacter.noumenon.name
        character.characterMemory.opinions(opinions6).forEach {
            println("opinions on $opinions6: ${it.change()}")
        }
        println("opinion on $opinions6: ${character.characterMemory.opinion(opinions6)}")

    }
}