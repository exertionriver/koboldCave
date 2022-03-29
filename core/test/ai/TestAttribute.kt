package ai

import org.junit.jupiter.api.Test
import org.river.exertion.ai.noumena.INoumenon.Companion.getRandomAttributes
import org.river.exertion.ai.noumena.IndividualNoumenon
import org.river.exertion.ai.noumena.KoboldNoumenon
import org.river.exertion.ai.noumena.LowRaceNoumenon
import org.river.exertion.ai.phenomena.ExternalPhenomenaType
import org.river.exertion.btree.v0_1.KoboldCharacter

@ExperimentalUnsignedTypes
class TestAttribute {

    val kn = KoboldNoumenon
    val lrn = LowRaceNoumenon

    @Test
    fun testAttributableLists() {
        kn.attributables().forEach { attr ->
            attr.attributable.getDescriptions().forEach { println(it) }
        }
    }

    @Test
    fun testAttributablesDescription() {
        kn.attributables().forEach { attr ->
            println ( attr.attributable.getDescriptionByOrder(0) )
            println ( attr.attributable.getValueByOrder(0) )
            println ( attr.attributable.getDescriptionByValue( attr.attributable.getValueByOrder(0)!! ) )
        }
    }

    @Test
    fun testAttributablesGetRandomValue() {
        kn.attributables().forEach { attr ->
            println ( attr.attributable.getDescriptionByValue( attr.attributable.getRandomValue() ) )
            println ( attr.attributable.getDescriptionByValue( attr.attributable.getRandomValue() ) )
            println ( attr.attributable.getDescriptionByValue( attr.attributable.getRandomValue() ) )
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueK() {
        (0..10).forEach {
            kn.attributables().getRandomAttributes().forEach { attr ->
                println ( "${attr.attributableTag}, ${attr.attributeValue.value}" )
            }
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueLR() {
        (0..10).forEach {
            lrn.attributables().getRandomAttributes().forEach { attr ->
                println ( "${attr.attributableTag}, ${attr.attributeValue.value}" )
            }
        }
    }

    @Test
    fun testPollRandomAttribute() {
        val testIndividual = IndividualNoumenon("test123", KoboldNoumenon.tags(), KoboldNoumenon.attributables())

        println("${testIndividual.name} attributes:")
        testIndividual.attributes.forEach { println ( "${it.attributableTag}, ${it.attributeValue.value}" ) }

        println("${testIndividual.name} attribute random selection:")
        (0..10).forEach {
            testIndividual.pollRandomAttribute().run { if (this != null) println ("${this.attributableTag}: ${this.attributeValue.value}") }
        }

        println("${testIndividual.name} attribute random selection(ExternalPhenomenaType.AUDITORY):")
        (0..10).forEach {
            testIndividual.pollRandomAttribute(ExternalPhenomenaType.AUDITORY).run { if (this != null) println ("${this.attributableTag}: ${this.attributeValue.value}") }
        }

        println("${testIndividual.name} attribute random selection(ExternalPhenomenaType.WISDOM):")
        (0..10).forEach {
            testIndividual.pollRandomAttribute(ExternalPhenomenaType.WISDOM).run { if (this != null) println ("${this.attributableTag}: ${this.attributeValue.value}") }
        }
    }
}