package ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.Vector3
import org.junit.jupiter.api.Test
import org.river.exertion.ai.internalSymbol.perceivedSymbols.FriendSymbol
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

    fun seedMemory(memCharacter : Entity, ofCharacter : Entity) {
        KoboldMemory.memoriesPA(IEntity.getFor(ofCharacter)!!.noumenonInstance).forEach {
            println("perceivedAttribute:${it.attributeInstance?.attributeObj}, ext phenom:${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.type?.name}, ${it.knowledgeSourceInstance}")

            val perceivedNoumenon = PerceivedNoumenon().apply { this.perceivedAttributes.add(it); this.noumenonType = NoumenonType.KOBOLD; this.isNamed = true}
            MemoryComponent.getFor(memCharacter)!!.internalMemory.activeMemory.noumenaRegister.add(MemoryInstance(perceivedNoumenon, FriendSymbol))
        }
        KoboldMemory.memoriesPN(IEntity.getFor(ofCharacter)!!.noumenonInstance).forEach {
            println("perceivedNoumenon:${it.noumenonType.tag()}")

            MemoryComponent.getFor(memCharacter)!!.internalMemory.activeMemory.noumenaRegister.add(MemoryInstance(it, FriendSymbol))
        }
    }

    @Test
    fun testKoboldMemory() {
        seedMemory(character, secondCharacter)

        val opinions1 = "other"
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions1).forEach {
            println("opinions on $opinions1: ${it.tag}")
        }
        println("opinion on $opinions1: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions1)}")

        val opinions2 = "kobold"
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions2).forEach {
            println("opinions on $opinions2: ${it.tag}")
        }
        println("opinion on $opinions2: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions2)}")

        val opinions3 = IEntity.getFor(secondCharacter)!!.noumenonInstance.instanceName
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions3).forEach {
            println("opinions on $opinions3: ${it.tag}")
        }
        println("opinion on $opinions3: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions3)}")
    }

    @Test
    fun testAddingKoboldMemoryFromManifest() {
        seedMemory(character, secondCharacter)

        MessageChannel.ADD_EXT_PHENOMENA.send(CharacterKobold.getFor(secondCharacter)!!, koboldBalter)

        engine.update(CharacterKobold.getFor(character)!!.moment)

        ManifestComponent.getFor(character)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.noumenaRegister.forEach { println("${it.perceivedNoumenon.noumenonType.tag()}, ${it.perceivedNoumenon.instanceName}, ${it.perceivedNoumenon.perceivedAttributes.first()}") }

        val opinions1 = "other"
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions1).forEach {
            println("opinions on $opinions1: ${it.tag}")
        }
        println("opinion on $opinions1: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions1)}")

        val opinions4 = "low-race"
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions4).forEach {
            println("opinions on $opinions4: ${it.tag}")
        }
        println("opinion on $opinions4: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions4)}")

        val opinions5 = "kobold"
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions5).forEach {
            println("opinions on $opinions5: ${it.tag}")
        }
        println("opinion on $opinions5: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions5)}")

        val opinions6 = CharacterKobold.getFor(secondCharacter)!!.noumenonInstance.instanceName
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinions(opinions6).forEach {
            println("opinions on $opinions6: ${it.tag}")
        }
        println("opinion on $opinions6: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions6)}")

    }

    @Test
    fun testPollKoboldFacts() {
        //only seed memories, not attributes
        KoboldMemory.memoriesPN(IEntity.getFor(secondCharacter)!!.noumenonInstance).forEach {
            println("perceivedNoumenon:${it.noumenonType.tag()}")

            MemoryComponent.getFor(character)!!.internalMemory.activeMemory.noumenaRegister.add(MemoryInstance(it, FriendSymbol))
        }
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.noumenaRegister.forEach { println("${it.perceivedNoumenon.noumenonType.tag()}, ${it.perceivedNoumenon.instanceName}, ${it.perceivedNoumenon.perceivedAttributes.first()} - ${it.symbol.tag}") }

        MessageChannel.ADD_EXT_PHENOMENA.send(CharacterKobold.getFor(secondCharacter)!!, koboldBalter)
        engine.update(CharacterKobold.getFor(character)!!.moment)

        ManifestComponent.getFor(character)!!.internalManifest.getManifest(ExternalPhenomenaType.AUDITORY).joinedList().forEach { println("$it : ${it.perceivedExternalPhenomena?.externalPhenomenaImpression?.countdown},${it.internalPhenomenaImpression?.countdown}") }

        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.noumenaRegister.forEach { println("${it.perceivedNoumenon.noumenonType.tag()}, ${it.perceivedNoumenon.instanceName}, ${it.perceivedNoumenon.perceivedAttributes.first()} - ${it.symbol.tag}") }

        val opinions5 = "kobold"
        println("facts on $opinions5")
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.facts(opinions5).forEach { fact -> println(fact) }
        println("opinion on $opinions5: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions5)}")

        val opinions6 = "low-race"
        println("facts on $opinions6")
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.facts(opinions6).forEach { fact -> println(fact) }
        println("opinion on $opinions6: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions6)}")

        val opinions7 = CharacterKobold.getFor(secondCharacter)!!.noumenonInstance.instanceName
        println("facts on $opinions7")
        MemoryComponent.getFor(character)!!.internalMemory.activeMemory.facts(opinions7).forEach { fact -> println(fact) }
        println("opinion on $opinions7: ${MemoryComponent.getFor(character)!!.internalMemory.activeMemory.opinion(opinions7)}")


    }
}