package ai

import org.junit.jupiter.api.Test
import org.river.exertion.ai.attribute.Trait.Companion.getRandomCharacteristics
import org.river.exertion.ai.noumena.other.being.humanoid.low_race.KoboldNoumenon
import org.river.exertion.ai.noumena.other.being.humanoid.low_race.KoboldNoumenon.kobold
import org.river.exertion.ai.noumena.other.being.humanoid.LowRaceNoumenon
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

@ExperimentalUnsignedTypes
class TestAttribute {

    val kn = KoboldNoumenon
    val lrn = LowRaceNoumenon

    @Test
    fun testAttributableLists() {
        kn.traits().forEach { char ->
            char.attribute().getDescriptions().forEach { println(it) }
        }
    }

    @Test
    fun testAttributablesDescription() {
        kn.traits().forEach { char ->
            println ( char.attribute().getAttributeValueByOrder(0)!!.description )
            println ( char.attribute().getAttributeValueByOrder(0)!!.value )
            println ( char.attribute().getAttributeValueByOrder(1)!!.value?.let { char.attribute().getDescriptionByValue(it) })
        }
    }

    @Test
    fun testAttributablesGetRandomValue() {
        kn.traits().forEach { char ->
            println (char.getRandomAttributeValue().value?.let { char.attribute().getDescriptionByValue(it) })
            println (char.getRandomAttributeValue().value?.let { char.attribute().getDescriptionByValue(it) })
            println (char.getRandomAttributeValue().value?.let { char.attribute().getDescriptionByValue(it) })
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueK() {
        (0..10).forEach {
            kn.traits().getRandomCharacteristics().forEach { attr ->
                println ( "${attr.attribute().type().tag()}, ${attr.characteristicValue.value}" )
            }
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueLR() {
        (0..10).forEach {
            lrn.traits().getRandomCharacteristics().forEach { attr ->
                println ( "${attr.attribute().type().tag()}, ${attr.characteristicValue.value}" )
            }
        }
    }

    @Test
    fun testPollRandomAttribute() {
        val testIndividual = kobold { instanceName = "test123" }

        println("${testIndividual.instanceName} attributes:")
        testIndividual.characteristics?.forEach { println ( "${it.attribute().type().tag()}, ${it.characteristicValue}" ) }

        println("${testIndividual.instanceName} attribute random selection:")
        (0..10).forEach {
            testIndividual.pollRandomAttributeInstance().run { if (this != null) println ("${this.attribute().type().tag()}: ${this.characteristicValue.value}") }
        }

        println("${testIndividual.instanceName} attribute random selection(ExternalPhenomenaType.AUDITORY):")
        (0..10).forEach {
            testIndividual.pollRandomAttributeInstance(ExternalPhenomenaType.AUDITORY).run { if (this != null) println ("${this.attribute().type().tag()}: ${this.characteristicValue.value}") }
        }

        println("${testIndividual.instanceName} attribute random selection(ExternalPhenomenaType.WISDOM):")
        (0..10).forEach {
            testIndividual.pollRandomAttributeInstance(ExternalPhenomenaType.WISDOM).run { if (this != null) println ("${this.attribute().type().tag()}: ${this.characteristicValue.value}") }
        }
    }
}