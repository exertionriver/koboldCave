package ai

import com.badlogic.ashley.core.PooledEngine
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
import org.river.exertion.ecs.component.ManifestComponent
import org.river.exertion.ecs.component.MemoryComponent
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.action.ActionSimpleDecideMoveComponent
import org.river.exertion.ecs.component.action.core.ActionType
import org.river.exertion.ecs.entity.character.CharacterKobold
import org.river.exertion.ecs.system.SystemManager


@ExperimentalUnsignedTypes
class TestCharacterMemory {

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val character = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }
    val secondCharacter = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }

    val koboldGrowl = ExternalPhenomenaInstance().apply {
        this.type = ExternalPhenomenaType.AUDITORY
        this.actionType = ActionType.BALTER
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
            MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.noumenaRegister.add(perceivedNoumenon)
        }

        MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.noumenaRegister.addAll(KoboldMemory.memoriesPN())

        val opinions1 = "other"
        MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinions(opinions1).forEach {
            println("opinions on $opinions1: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions1: ${MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinion(opinions1)}")

        val opinions2 = "kobold"
        MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinions(opinions2).forEach {
            println("opinions on $opinions2: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions2: ${MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinion(opinions2)}")

        val opinions3 = "intelligence"
        MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinions(opinions3).forEach {
            println("opinions on $opinions3: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions3: ${MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinion(opinions3)}")
    }

    @Test
    fun testAddingKoboldMemoryFromManifest() {
        KoboldMemory.memoriesPA().forEach {
            val isi = InternalFacetInstancesState().apply { this.internalState.add(FearFacet.fearFacet { magnitude = 0.6f }) }
            val perceivedNoumenon = PerceivedNoumenon(internalStateInstance = isi, knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE) ).apply { this.perceivedAttributes.add(it); this.noumenonType = NoumenonType.OTHER; this.isNamed = true}
            MemoryComponent.getFor(character)!!.internalMemory.longtermMemory.noumenaRegister.add(perceivedNoumenon)
        }

        MemoryComponent.getFor(character)!!.internalMemory.longtermMemory.noumenaRegister.addAll(KoboldMemory.memoriesPN())

        MemoryComponent.getFor(character)!!.internalMemory.internalState.internalState.add( angerFacet { magnitude = 0.7f } )
        MessageManager.getInstance().dispatchMessage(CharacterKobold.getFor(secondCharacter)!!, MessageIds.EXT_PHENOMENA.id(), koboldGrowl)
        engine.update(CharacterKobold.getFor(character)!!.moment * 2 + 0.01f)

        ManifestComponent.getFor(character)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.noumenaRegister.forEach { println("${it.noumenonType.tag()}, ${it.instanceName}, ${it.perceivedAttributes.first()}") }

        val opinions4 = "low race"
        MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinions(opinions4).forEach {
            println("opinions on $opinions4: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions4: ${MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinion(opinions4)}")

        val opinions5 = "kobold"
        MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinions(opinions5).forEach {
            println("opinions on $opinions5: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions5: ${MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinion(opinions5)}")

        val opinions6 = CharacterKobold.getFor(secondCharacter)!!.noumenonInstance.instanceName
        MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinions(opinions6).forEach {
            println("opinions on $opinions6: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions6: ${MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinion(opinions6)}")

    }

    @Test
    fun testPollKoboldFacts() {

        MemoryComponent.getFor(character)!!.internalMemory.internalState.internalState.add( angerFacet { magnitude = 0.7f } )
        MessageManager.getInstance().dispatchMessage(CharacterKobold.getFor(secondCharacter)!!, MessageIds.EXT_PHENOMENA.id(), koboldGrowl)
        engine.update(CharacterKobold.getFor(character)!!.moment * 2 + 0.01f)

        ManifestComponent.getFor(character)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.noumenaRegister.forEach { println("${it.noumenonType.tag()}, ${it.instanceName}, ${it.perceivedAttributes.first()}") }

        val opinions5 = "kobold"
        println("facts on $opinions5")
        MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.facts(opinions5).forEach { fact -> println(fact) }
        println("opinion on $opinions5: ${MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinion(opinions5)}")

        val opinions6 = "low race"
        println("facts on $opinions6")
        MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.facts(opinions6).forEach { fact -> println(fact) }
        println("opinion on $opinions6: ${MemoryComponent.getFor(character)!!.internalMemory.registerExecutive.opinion(opinions6)}")

    }
}