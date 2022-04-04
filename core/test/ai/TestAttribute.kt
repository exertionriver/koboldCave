package ai

import org.junit.jupiter.api.Test
import org.river.exertion.ai.attributes.AttributeRange.Companion.getRandomAttributes
import org.river.exertion.ai.noumena.IndividualNoumenon
import org.river.exertion.ai.noumena.KoboldNoumenon
import org.river.exertion.ai.noumena.LowRaceNoumenon
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

@ExperimentalUnsignedTypes
class TestAttribute {

    val kn = KoboldNoumenon
    val lrn = LowRaceNoumenon

    @Test
    fun testAttributableLists() {
        kn.attributeRange().forEach { attr ->
            attr.attribute.getDescriptions().forEach { println(it) }
        }
    }

    @Test
    fun testAttributablesDescription() {
        kn.attributeRange().forEach { attr ->
            println ( attr.attribute.getAttributeValueByOrder(0)!!.description )
            println ( attr.attribute.getAttributeValueByOrder(0)!!.value )
            println ( attr.attribute.getDescriptionByValue( attr.attribute.getAttributeValueByOrder(0)!! ) )
        }
    }

    @Test
    fun testAttributablesGetRandomValue() {
        kn.attributeRange().forEach { attr ->
            println ( attr.attribute.getDescriptionByValue( attr.getRandomRangeAttributeValue() ) )
            println ( attr.attribute.getDescriptionByValue( attr.getRandomRangeAttributeValue() ) )
            println ( attr.attribute.getDescriptionByValue( attr.getRandomRangeAttributeValue() ) )
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueK() {
        (0..10).forEach {
            kn.attributeRange().getRandomAttributes().forEach { attr ->
                println ( "${attr.attribute.tag()}, ${attr.attributeValue.value}" )
            }
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueLR() {
        (0..10).forEach {
            lrn.attributeRange().getRandomAttributes().forEach { attr ->
                println ( "${attr.attribute.tag()}, ${attr.attributeValue.value}" )
            }
        }
    }

    @Test
    fun testPollRandomAttribute() {
        val testIndividual = IndividualNoumenon("test123", KoboldNoumenon.javaClass)

        println("${testIndividual.name} attributes:")
        testIndividual.attributeInstances.forEach { println ( "${it.attribute.tag()}, ${it.attributeValue.value}" ) }

        println("${testIndividual.name} attribute random selection:")
        (0..10).forEach {
            testIndividual.pollRandomAttribute().run { if (this != null) println ("${this.attribute.tag()}: ${this.attributeValue.value}") }
        }

        println("${testIndividual.name} attribute random selection(ExternalPhenomenaType.AUDITORY):")
        (0..10).forEach {
            testIndividual.pollRandomAttribute(ExternalPhenomenaType.AUDITORY).run { if (this != null) println ("${this.attribute.tag()}: ${this.attributeValue.value}") }
        }

        println("${testIndividual.name} attribute random selection(ExternalPhenomenaType.WISDOM):")
        (0..10).forEach {
            testIndividual.pollRandomAttribute(ExternalPhenomenaType.WISDOM).run { if (this != null) println ("${this.attribute.tag()}: ${this.attributeValue.value}") }
        }
    }
}