package ai

import org.junit.jupiter.api.Test
import org.river.exertion.ai.attribute.Characteristic.Companion.getRandomCharacteristicAttributeInstances
import org.river.exertion.ai.noumena.KoboldNoumenon
import org.river.exertion.ai.noumena.KoboldNoumenon.kobold
import org.river.exertion.ai.noumena.LowRaceNoumenon
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

@ExperimentalUnsignedTypes
class TestAttribute {

    val kn = KoboldNoumenon
    val lrn = LowRaceNoumenon

    @Test
    fun testAttributableLists() {
        kn.characteristics().forEach { char ->
            char.attribute().getDescriptions().forEach { println(it) }
        }
    }

    @Test
    fun testAttributablesDescription() {
        kn.characteristics().forEach { char ->
            println ( char.attribute().getAttributeValueByOrder(0)!!.description )
            println ( char.attribute().getAttributeValueByOrder(0)!!.value )
            println ( char.attribute().getAttributeValueByOrder(1)!!.value?.let { char.attribute().getDescriptionByValue(it) })
        }
    }

    @Test
    fun testAttributablesGetRandomValue() {
        kn.characteristics().forEach { char ->
            println (char.getRandomCharacteristicAttributeValue().value?.let { char.attribute().getDescriptionByValue(it) })
            println (char.getRandomCharacteristicAttributeValue().value?.let { char.attribute().getDescriptionByValue(it) })
            println (char.getRandomCharacteristicAttributeValue().value?.let { char.attribute().getDescriptionByValue(it) })
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueK() {
        (0..10).forEach {
            kn.characteristics().getRandomCharacteristicAttributeInstances().forEach { attr ->
                println ( "${attr.attribute().type().tag()}, ${attr.attributeValue.value}" )
            }
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueLR() {
        (0..10).forEach {
            lrn.characteristics().getRandomCharacteristicAttributeInstances().forEach { attr ->
                println ( "${attr.attribute().type().tag()}, ${attr.attributeValue.value}" )
            }
        }
    }

    @Test
    fun testPollRandomAttribute() {
        val testIndividual = kobold { instanceName = "test123" }

        println("${testIndividual.instanceName} attributes:")
        testIndividual.attributeInstances.forEach { println ( "${it.attribute().type().tag()}, ${it.attributeValue.value}" ) }

        println("${testIndividual.instanceName} attribute random selection:")
        (0..10).forEach {
            testIndividual.pollRandomAttributeInstance().run { if (this != null) println ("${this.attribute().type().tag()}: ${this.attributeValue.value}") }
        }

        println("${testIndividual.instanceName} attribute random selection(ExternalPhenomenaType.AUDITORY):")
        (0..10).forEach {
            testIndividual.pollRandomAttributeInstance(ExternalPhenomenaType.AUDITORY).run { if (this != null) println ("${this.attribute().type().tag()}: ${this.attributeValue.value}") }
        }

        println("${testIndividual.instanceName} attribute random selection(ExternalPhenomenaType.WISDOM):")
        (0..10).forEach {
            testIndividual.pollRandomAttributeInstance(ExternalPhenomenaType.WISDOM).run { if (this != null) println ("${this.attribute().type().tag()}: ${this.attributeValue.value}") }
        }
    }
}