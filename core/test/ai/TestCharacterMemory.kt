package ai

import org.junit.jupiter.api.Test
import org.river.exertion.btree.v0_1.*


@ExperimentalUnsignedTypes
class TestCharacterMemory {

    var character = KoboldCharacter()

    @Test
    fun testKoboldMemory() {
        character.characterMemory.associativePerceptionList = KoboldMemory.memoriesPA()
        character.characterMemory.associativeNoumenaList = KoboldMemory.memoriesPN()

        val opinions1 = "other"
        character.characterMemory.opinions(opinions1).forEach {
            println("opinions on $opinions1: $it")
        }

        val opinions2 = "kobold"
        character.characterMemory.opinions(opinions2).forEach {
            println("opinions on $opinions2: $it")
        }

        val opinions3 = "intelligence"
        character.characterMemory.opinions(opinions3).forEach {
            println("opinions on $opinions3: $it")
        }
    }
}