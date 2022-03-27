package ai

import org.junit.jupiter.api.Test
import org.river.exertion.ai.noumena.KoboldNoumenon
import org.river.exertion.ai.noumena.LowRaceNoumenon

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
            kn.getRandomAttributes().forEach { attr ->
                println ( "${attr.key}, ${attr.value.value}" )
            }
        }
    }

    @Test
    fun testAttributablesGetRandomAttribValueLR() {
        (0..10).forEach {
            lrn.getRandomAttributes().forEach { attr ->
                println ( "${attr.key}, ${attr.value.value}" )
            }
        }
    }
}