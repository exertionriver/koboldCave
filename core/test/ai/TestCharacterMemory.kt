package ai

import org.junit.jupiter.api.Test
import org.river.exertion.ai.Knowable
import org.river.exertion.ai.memories.KoboldMemory
import org.river.exertion.btree.v0_1.*


@ExperimentalUnsignedTypes
class TestCharacterMemory {

    var character = KoboldCharacter()

    @Test
    fun testKoboldMemory() {
        character.characterMemory.associativeMemoryList = KoboldMemory.memories()

        val opinions1 = "other"
        character.characterMemory.opinions(Knowable.KnowableGranularity.OTHER, opinions1).forEach {
            println("opinions on $opinions1: ${it.knowable}, ${it.internalPhenomenaInstance}, ${it.becauseOf}")
        }

        val opinions2 = "kobold"
        character.characterMemory.opinions(Knowable.KnowableGranularity.ENTITY_TYPE, opinions2).forEach {
            println("opinions on $opinions2: ${it.knowable}, ${it.internalPhenomenaInstance}, ${it.becauseOf}")
        }

        val opinions3 = "red hand"
        character.characterMemory.opinions(Knowable.KnowableGranularity.ENTITY_GROUP, opinions3).forEach {
            println("opinions on $opinions3: ${it.knowable}, ${it.internalPhenomenaInstance}, ${it.becauseOf}")
        }
    }
}