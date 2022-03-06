package geom

import com.badlogic.gdx.math.Vector3
import org.junit.jupiter.api.Test
import org.river.exertion.geom.Line3.Companion.anglesBetween
import org.river.exertion.geom.Line3.Companion.applyNoise
import org.river.exertion.geom.Line3.Companion.getPositionByDistanceAndAngles


@ExperimentalUnsignedTypes
class TestLine3 {

    @Test
    fun testAnglesBetween() {

        //Q1 / H1
        var firstPoint = Vector3(0f, 0f, 0f)
        var secondPoint = Vector3(11f, 12f, 13f)
        var angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q1, H1: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q2 / H1
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(-11f, 12f, 13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q2, H1: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q3 / H1
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(-11f, -12f, 13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q3, H1: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q4 / H1
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(11f, -12f, 13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q4, H1: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q1 / H2
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(11f, 12f, -13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q1, H2: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q2 / H2
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(-11f, 12f, -13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q2, H2: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q3 / H2
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(-11f, -12f, -13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q3, H2: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q4 / H2
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(11f, -12f, -13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q4, H2: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

    }
    @Test
    fun testPositionByDistAndAngles() {

        var sphCoords = Vector3(-10f, 20f, 30f)
        var newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(5f, 0f, 0f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(10f, 20f, 30f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(15f, 50f, 60f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(20f, 80f, 90f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(25f, 110f, 120f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(30f, 140f, 150f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(-30f, 140f, 150f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(35f, 170f, 180f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(30f, 200f, 30f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(25f, 230f, 60f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(20f, 260f, 90f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(15f, 290f, 120f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(10f, 320f, 150f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

        sphCoords = Vector3(5f, 360f, 180f)
        newPosition = sphCoords.getPositionByDistanceAndAngles()
        println("$sphCoords points to $newPosition")

    }

    @Test
    fun testApplyNoise() {

        var noiseRadius = 3f
        var noiseAngle = 45f
        var firstPosition = Vector3(1f, 1f, 1f)
        var secondPosition = Vector3(7f, 9f, 11f)

        val pi14 = Pair(firstPosition, secondPosition).applyNoise(noiseRadius, noiseAngle)
        println("$noiseRadius at $noiseAngle applied to V($firstPosition -> $secondPosition) yields noise point: $pi14")

        noiseAngle = 90f
        val pi24 = Pair(firstPosition, secondPosition).applyNoise(noiseRadius, noiseAngle)
        println("$noiseRadius at $noiseAngle applied to V($firstPosition -> $secondPosition) yields noise point: $pi24")

        noiseAngle = 135f
        val pi34 = Pair(firstPosition, secondPosition).applyNoise(noiseRadius, noiseAngle)
        println("$noiseRadius at $noiseAngle applied to V($firstPosition -> $secondPosition) yields noise point: $pi34")

        noiseAngle = 180f
        val pi44 = Pair(firstPosition, secondPosition).applyNoise(noiseRadius, noiseAngle)
        println("$noiseRadius at $noiseAngle applied to V($firstPosition -> $secondPosition) yields noise point: $pi44")

        noiseAngle = 225f
        val pi54 = Pair(firstPosition, secondPosition).applyNoise(noiseRadius, noiseAngle)
        println("$noiseRadius at $noiseAngle applied to V($firstPosition -> $secondPosition) yields noise point: $pi54")

        noiseAngle = 270f
        val pi64 = Pair(firstPosition, secondPosition).applyNoise(noiseRadius, noiseAngle)
        println("$noiseRadius at $noiseAngle applied to V($firstPosition -> $secondPosition) yields noise point: $pi64")

        noiseAngle = 315f
        val pi74 = Pair(firstPosition, secondPosition).applyNoise(noiseRadius, noiseAngle)
        println("$noiseRadius at $noiseAngle applied to V($firstPosition -> $secondPosition) yields noise point: $pi74")

        noiseAngle = 360f
        val pi84 = Pair(firstPosition, secondPosition).applyNoise(noiseRadius, noiseAngle)
        println("$noiseRadius at $noiseAngle applied to V($firstPosition -> $secondPosition) yields noise point: $pi84")
    }
}