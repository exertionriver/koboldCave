package koboldCave

import org.junit.jupiter.api.Test
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect

@ExperimentalUnsignedTypes
class TestProb {

    @Test
    fun testPSAccumulating() {

        val psAccumulatingTwo = ProbabilitySelect.psAccumulating(listOf("A", "B"))
        println(psAccumulatingTwo)
        println(psAccumulatingTwo.getSelectedProbability())

        val psAccumulatingThree = ProbabilitySelect.psAccumulating(listOf("A", "B", "C"))
        println(psAccumulatingThree)
        println(psAccumulatingThree.getSelectedProbability())

        val psAccumulatingFour = ProbabilitySelect.psAccumulating(listOf("A", "B", "C", "D"))
        println(psAccumulatingFour)
        println(psAccumulatingFour.getSelectedProbability())

        val psAccumulatingFive = ProbabilitySelect.psAccumulating(listOf("A", "B", "C", "D", "E"))
        println(psAccumulatingFive)
        println(psAccumulatingFive.getSelectedProbability())
    }

    @Test
    fun testRandForwardAngle() {

        val probs = (1..10).toList().map {(Probability(0, 60).getValue()) }.toList()

        for (prob in probs) {
            println(prob)
        }
    }
}