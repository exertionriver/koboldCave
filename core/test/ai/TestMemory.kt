package ai

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.math.Vector3
import org.junit.jupiter.api.Test
import org.river.exertion.ai.internalFacet.AngerFacet.angerFacet
import org.river.exertion.ai.internalFacet.FearFacet
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState
import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.memory.KnowledgeSourceType
import org.river.exertion.ai.memory.MemoryInstance
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.perception.PerceivedNoumenon
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.ecs.component.ManifestComponent
import org.river.exertion.ecs.component.MemoryComponent
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.action.ActionSimpleDecideMoveComponent
import org.river.exertion.ecs.component.action.core.ActionType
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.ecs.entity.character.CharacterKobold
import org.river.exertion.ecs.system.SystemManager


@ExperimentalUnsignedTypes
class TestMemory {

    val engine = PooledEngine().apply { SystemManager.init(this) }
    val character = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }
    val secondCharacter = CharacterKobold.ecsInstantiate(engine).apply { this.remove(ActionMoveComponent.getFor(this)!!.javaClass) ; this.remove(ActionSimpleDecideMoveComponent.getFor(this)!!.javaClass) }

    val koboldBalter = ExternalPhenomenaInstance().apply {
        this.type = ExternalPhenomenaType.AUDITORY
        this.actionType = ActionType.BALTER
        this.direction = 120f
        this.magnitude = 120f
        this.location = Vector3(30f, 30f, 30f)
        this.loss = .3f
    }

    @Test
    fun testKoboldMemory() {
        val isi = InternalFacetInstancesState(IEntity.getFor(character)!!).apply { this.internalState.add(FearFacet.fearFacet { magnitude = 0.6f }) }

        KoboldMemory.memoriesPA().forEach {
            val perceivedNoumenon = PerceivedNoumenon(knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE) ).apply { this.perceivedAttributes.add(it); this.noumenonType = NoumenonType.OTHER; this.isNamed = true}
            MemoryComponent.getFor(character)!!.internalMemory.activeMemory.noumenaRegister.add(MemoryInstance(perceivedNoumenon, isi))
        }
        KoboldMemory.memoriesPN().forEach {
            MemoryComponent.getFor(character)!!.internalMemory.activeMemory.noumenaRegister.add(MemoryInstance(it, isi))
        }

        val opinions1 = "other"
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions1).forEach {
            println("opinions on $opinions1: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions1: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions1)}")

        val opinions2 = "kobold"
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions2).forEach {
            println("opinions on $opinions2: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions2: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions2)}")

        val opinions3 = "intelligence"
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions3).forEach {
            println("opinions on $opinions3: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions3: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions3)}")
    }

    @Test
    fun testAddingKoboldMemoryFromManifest() {
        val isi = InternalFacetInstancesState(IEntity.getFor(character)!!).apply { this.internalState.add(FearFacet.fearFacet { magnitude = 0.6f }) }

        MemoryComponent.getFor(character)!!.internalFacetInstancesState = isi

        MessageManager.getInstance().dispatchMessage(CharacterKobold.getFor(secondCharacter)!!, MessageChannel.ADD_EXT_PHENOMENA.id(), koboldBalter)
        engine.update(CharacterKobold.getFor(character)!!.moment)

        ManifestComponent.getFor(character)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.noumenaRegister.forEach { println("${it.perceivedNoumenon.noumenonType.tag()}, ${it.perceivedNoumenon.instanceName}, ${it.perceivedNoumenon.perceivedAttributes.first()}") }

        val opinions1 = "other"
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions1).forEach {
            println("opinions on $opinions1: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions1: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions1)}")

        val opinions4 = "low-race"
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions4).forEach {
            println("opinions on $opinions4: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions4: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions4)}")

        val opinions5 = "kobold"
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions5).forEach {
            println("opinions on $opinions5: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions5: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions5)}")

        val opinions6 = CharacterKobold.getFor(secondCharacter)!!.noumenonInstance.instanceName
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions6).forEach {
            println("opinions on $opinions6: ${it.internalState}: ${it.magnitudeOpinion()}")
        }
        println("opinion on $opinions6: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions6)}")

    }

    @Test
    fun testPollKoboldFacts() {
        val isi = InternalFacetInstancesState(IEntity.getFor(character)!!).apply { this.internalState.add(angerFacet { magnitude = 0.7f }) }

        MemoryComponent.getFor(character)!!.internalFacetInstancesState = isi

        MessageManager.getInstance().dispatchMessage(CharacterKobold.getFor(secondCharacter)!!, MessageChannel.ADD_EXT_PHENOMENA.id(), koboldBalter)
        engine.update(CharacterKobold.getFor(character)!!.moment)

        ManifestComponent.getFor(character)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.noumenaRegister.forEach { println("${it.perceivedNoumenon.noumenonType.tag()}, ${it.perceivedNoumenon.instanceName}, ${it.perceivedNoumenon.perceivedAttributes.first()}") }

        val opinions5 = "kobold"
        println("facts on $opinions5")
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.facts(opinions5).forEach { fact -> println(fact) }
        println("opinion on $opinions5: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions5)}")

        val opinions6 = "low-race"
        println("facts on $opinions6")
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.facts(opinions6).forEach { fact -> println(fact) }
        println("opinion on $opinions6: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions6)}")

    }
}