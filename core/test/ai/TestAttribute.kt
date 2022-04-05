package ai

import org.junit.jupiter.api.Test
import org.river.exertion.ai.attributes.AttributeRange.Companion.getRandomAttributes
import org.river.exertion.ai.noumena.NoumenonInstance
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
        kn.attributeRange().forEach { attr ->
            attr.attribute().getDescriptions().forEach { println(it) }
        }
    }

    @Test
    fun testAttributablesDescription() {
        kn.attributeRange().forEach { attr ->
            println ( attr.attribute().getAttributeValueByOrder(0)!!.description )
            println ( attr.attribute().getAttributeValueByOrder(0)!!.value )
            println ( attr.attribute().getAttributeValueByOrder(1)!!.value?.let { attr.attribute().getDescriptionByValue(it) })
        }
    }

    @Test
    fun testAttributablesGetRandomValue() {
        kn.attributeRange().forEach { attr ->
            println (attr.getRandomRangeAttributeValue().value?.let { attr.attribute().getDescriptionByValue(it) })
            println (attr.getRandomRangeAttributeValue().value?.let { attr.attribute().getDescriptionByValue(it) })
            println (attr.getRandomRangeAttributeValue().value?.let { attr.attribute().getDescriptionByValue(it) })
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueK() {
        (0..10).forEach {
            kn.attributeRange().getRandomAttributes().forEach { attr ->
                println ( "${attr.attribute().type().tag()}, ${attr.attributeValue.value}" )
            }
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueLR() {
        (0..10).forEach {
            lrn.attributeRange().getRandomAttributes().forEach { attr ->
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
            testIndividual.pollRandomAttribute().run { if (this != null) println ("${this.attribute().type().tag()}: ${this.attributeValue.value}") }
        }

        println("${testIndividual.instanceName} attribute random selection(ExternalPhenomenaType.AUDITORY):")
        (0..10).forEach {
            testIndividual.pollRandomAttribute(ExternalPhenomenaType.AUDITORY).run { if (this != null) println ("${this.attribute().type().tag()}: ${this.attributeValue.value}") }
        }

        println("${testIndividual.instanceName} attribute random selection(ExternalPhenomenaType.WISDOM):")
        (0..10).forEach {
            testIndividual.pollRandomAttribute(ExternalPhenomenaType.WISDOM).run { if (this != null) println ("${this.attribute().type().tag()}: ${this.attributeValue.value}") }
        }
    }
}