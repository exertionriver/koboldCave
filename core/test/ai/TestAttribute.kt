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

    val kn = KoboldNoumenon()
    val lrn = LowRaceNoumenon()

    @Test
    fun testAttributableLists() {
        kn.attributables.forEach { attr ->
            attr.key.getDescriptions().forEach { println(it) }
        }
    }

    @Test
    fun testAttributablesDescription() {
        kn.attributables.forEach { attr ->
            println ( attr.key.getDescriptionByOrder(0) )
            println ( attr.key.getValueByOrder(0) )
            println ( attr.key.getDescriptionByValue( attr.key.getValueByOrder(0)!! ) )
        }
    }

    @Test
    fun testAttributablesGetRandomValue() {
        kn.attributables.forEach { attr ->
            println ( attr.key.getDescriptionByValue( attr.key.getRandomValue() ) )
            println ( attr.key.getDescriptionByValue( attr.key.getRandomValue() ) )
            println ( attr.key.getDescriptionByValue( attr.key.getRandomValue() ) )
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueK() {
        (0..10).forEach {
            kn.attributables.getRandomAttributes().forEach { attr ->
                println ( "${attr.key}, ${attr.value.second.value}" )
            }
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueLR() {
        (0..10).forEach {
            lrn.attributables.getRandomAttributes().forEach { attr ->
                println ( "${attr.key}, ${attr.value.second.value}" )
            }
        }
    }

    @Test
    fun testPollRandomAttribute() {
        val testIndividual = IndividualNoumenon("test123", KoboldNoumenon.tags(), KoboldNoumenon.attributables())

        println("${testIndividual.name} attributes:")
        testIndividual.attributes.forEach { println ("${it.key}: ${it.value.second.value}")}

        println("${testIndividual.name} attribute random selection:")
        (0..10).forEach {
            testIndividual.pollRandomAttribute().run { if (this != null) println ("${this.first}: ${this.second.second.value}") }
        }

        println("${testIndividual.name} attribute random selection(ExternalPhenomenaType.AUDITORY):")
        (0..10).forEach {
            testIndividual.pollRandomAttribute(ExternalPhenomenaType.AUDITORY).run { if (this != null) println ("${this.first}: ${this.second.second.value}") }
        }

        println("${testIndividual.name} attribute random selection(ExternalPhenomenaType.WISDOM):")
        (0..10).forEach {
            testIndividual.pollRandomAttribute(ExternalPhenomenaType.WISDOM).run { if (this != null) println ("${this.first}: ${this.second.second.value}") }
        }
    }
}