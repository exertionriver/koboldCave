package ai

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.math.Vector3
import org.junit.jupiter.api.Test
import org.river.exertion.MessageIds
import org.river.exertion.ai.internalFacet.AngerFacet.angerFacet
import org.river.exertion.ai.internalFacet.FearFacet
import org.river.exertion.ai.internalState.InternalFacetInstancesState
import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.memory.KnowledgeSourceType
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.perception.PerceivedNoumenon
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.btree.v0_1.*


@ExperimentalUnsignedTypes
class TestCharacterMemory {

    var character = KoboldCharacter()
    var secondCharacter = KoboldCharacter()

    val koboldGrowl = ExternalPhenomenaInstance().apply {
        this.type = ExternalPhenomenaType.AUDITORY
        this.taskType = Behavior.BALTER
        this.direction = 120f
        this.magnitude = 120f
        this.location = Vector3(30f, 30f, 30f)
        this.loss = .3f
    }

    @Test
    fun testKoboldMemory() {
        KoboldMemory.memoriesPA().forEach {
            val isi = InternalFacetInstancesState().apply { this.internalState.add(FearFacet.fearFacet { magnitude = 0.6f }) }
            val perceivedNoumenon = PerceivedNoumenon(internalStateInstance = isi, knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE) ).apply { this.perceivedAttributes.add(it); this.noumenonType = NoumenonType.OTHER; this.isNamed = true}
            character.characterMemory.registerExecutive.noumenaRegister.add(perceivedNoumenon)
        }

        character.characterMemory.registerExecutive.noumenaRegister.addAll(KoboldMemory.memoriesPN())

        val opinions1 = "other"
        character.characterMemory.registerExecutive.opinions(opinions1).forEach {
            println("opinions on $opinions1: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions1: ${character.characterMemory.registerExecutive.opinion(opinions1)}")

        val opinions2 = "kobold"
        character.characterMemory.registerExecutive.opinions(opinions2).forEach {
            println("opinions on $opinions2: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions2: ${character.characterMemory.registerExecutive.opinion(opinions2)}")

        val opinions3 = "intelligence"
        character.characterMemory.registerExecutive.opinions(opinions3).forEach {
            println("opinions on $opinions3: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions3: ${character.characterMemory.registerExecutive.opinion(opinions3)}")
    }

    @Test
    fun testAddingKoboldMemoryFromManifest() {
        KoboldMemory.memoriesPA().forEach {
            val isi = InternalFacetInstancesState().apply { this.internalState.add(FearFacet.fearFacet { magnitude = 0.6f }) }
            val perceivedNoumenon = PerceivedNoumenon(internalStateInstance = isi, knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE) ).apply { this.perceivedAttributes.add(it); this.noumenonType = NoumenonType.OTHER; this.isNamed = true}
            character.characterMemory.longtermMemory.noumenaRegister.add(perceivedNoumenon)
        }

        character.characterMemory.longtermMemory.noumenaRegister.addAll(KoboldMemory.memoriesPN())

        character.characterMemory.internalState.internalState.add( angerFacet { magnitude = 0.7f } )
        MessageManager.getInstance().dispatchMessage(secondCharacter, MessageIds.EXT_PHENOMENA.id(), koboldGrowl)
        character.update(character.actionMoment * 2 + 0.01f)
        character.characterManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        character.characterMemory.registerExecutive.noumenaRegister.forEach { println("${it.noumenonType.tag()}, ${it.instanceName}, ${it.perceivedAttributes.first()}") }

        val opinions4 = "low race"
        character.characterMemory.registerExecutive.opinions(opinions4).forEach {
            println("opinions on $opinions4: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions4: ${character.characterMemory.registerExecutive.opinion(opinions4)}")

        val opinions5 = "kobold"
        character.characterMemory.registerExecutive.opinions(opinions5).forEach {
            println("opinions on $opinions5: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions5: ${character.characterMemory.registerExecutive.opinion(opinions5)}")

        val opinions6 = secondCharacter.noumenonInstance.instanceName
        character.characterMemory.registerExecutive.opinions(opinions6).forEach {
            println("opinions on $opinions6: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions6: ${character.characterMemory.registerExecutive.opinion(opinions6)}")

    }

    @Test
    fun testPollKoboldFacts() {

        character.characterMemory.internalState.internalState.add( angerFacet { magnitude = 0.7f } )
        MessageManager.getInstance().dispatchMessage(secondCharacter, MessageIds.EXT_PHENOMENA.id(), koboldGrowl)
        character.update(character.actionMoment * 2 + 0.01f)
        character.characterManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        character.characterMemory.registerExecutive.noumenaRegister.forEach { println("${it.noumenonType.tag()}, ${it.instanceName}, ${it.perceivedAttributes.first()}") }

        val opinions5 = "kobold"
        println("facts on $opinions5")
        character.characterMemory.registerExecutive.facts(opinions5).forEach { fact -> println(fact) }
        println("opinion on $opinions5: ${character.characterMemory.registerExecutive.opinion(opinions5)}")

        val opinions6 = "low race"
        println("facts on $opinions6")
        character.characterMemory.registerExecutive.facts(opinions6).forEach { fact -> println(fact) }
        println("opinion on $opinions6: ${character.characterMemory.registerExecutive.opinion(opinions6)}")

    }
}