
import com.soywiz.korma.geom.*
import kotlin.math.atan
import kotlin.test.Test

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

    @Test
    fun lineNodes() {

        val quad1Start = Point(1, 1)
        val quad1End = Point(10, 20)
        var currentPoint = quad1Start

        var angle = Angle.fromRadians(atan((quad1End.y - currentPoint.y) / (quad1End.x - currentPoint.x)))

        println("1) node at ${currentPoint}, angle ${angle.degrees}")
        currentPoint = Point(currentPoint.x + 2 * cos(angle), currentPoint.y + 2 * sin(angle))

        while ((quad1End.x >= currentPoint.x) && (quad1End.y >= currentPoint.y)) {
            println("1) node at ${currentPoint}, angle ${angle.degrees}")
            currentPoint = Point(currentPoint.x + 2 * cos(angle), currentPoint.y + 2 * sin(angle))
        }

        val quad2Start = Point(10, 1)
        val quad2End = Point(1, 20)
        currentPoint = quad2Start

        angle = Angle.fromRadians(atan((currentPoint.y - quad2End.y) / (quad2End.x - currentPoint.x)))

        println("2) node at ${currentPoint}, angle ${angle.degrees}")
        currentPoint = Point(currentPoint.x - 2 * cos(angle), currentPoint.y + 2 * sin(angle))


        while ( (quad2End.x < currentPoint.x) && (quad2End.y >= currentPoint.y) ) {
            println("2) node at ${currentPoint}, angle ${angle.degrees}")
            currentPoint = Point(currentPoint.x - 2 * cos(angle), currentPoint.y + 2 * sin(angle))
        }

        val quad3Start = Point(1, 10)
        val quad3End = Point(20, 1)
        currentPoint = quad3Start

        angle = Angle.fromRadians(atan((quad3End.y - currentPoint.y) / (currentPoint.x - quad3End.x)))

        println("3) node at ${currentPoint}, angle ${angle.degrees}")
        currentPoint = Point(currentPoint.x + 2 * cos(angle), currentPoint.y - 2 * sin(angle))


        while ( (quad3End.x >= currentPoint.x) && (quad3End.y < currentPoint.y) ) {
            println("3) node at ${currentPoint}, angle ${angle.degrees}")
            currentPoint = Point(currentPoint.x + 2 * cos(angle), currentPoint.y - 2 * sin(angle))
        }

        val quad4Start = Point(20, 10)
        val quad4End = Point(1, 1)
        currentPoint = quad4Start

        angle = Angle.fromRadians(atan((currentPoint.y - quad4End.y) / (currentPoint.x - quad4End.x)))

        println("4) node at ${currentPoint}, angle ${angle.degrees}")
        currentPoint = Point(currentPoint.x - 2 * cos(angle), currentPoint.y - 2 * sin(angle))

        while ( (quad4End.x < currentPoint.x) && (quad4End.y < currentPoint.y) ) {
            println("4) node at ${currentPoint}, angle ${angle.degrees}")
            currentPoint = Point(currentPoint.x - 2 * cos(angle), currentPoint.y - 2 * sin(angle))
        }
    }
}