package geom

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import org.junit.jupiter.api.Test
import org.river.exertion.*
import org.river.exertion.geom.Line
import org.river.exertion.geom.Line.Companion.angleBetween
import org.river.exertion.geom.Line.Companion.getIntersection
import org.river.exertion.geom.Line.Companion.isInRect
import org.river.exertion.geom.Line3.Companion.anglesBetween
import org.river.exertion.geom.Line3.Companion.applyNoise
import org.river.exertion.geom.Line3.Companion.getPositionByDistanceAndAngles


@ExperimentalUnsignedTypes
class TestNodeLine3 {

    @Test
    fun testAnglesBetween() {

        //Q1 / Q5
        var firstPoint = Vector3(0f, 0f, 0f)
        var secondPoint = Vector3(11f, 12f, 13f)
        var angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q1, Q5: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q2 / Q5
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(-11f, 12f, 13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q2, Q5: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q3 / Q6
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(-11f, -12f, 13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q3, Q6: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q4 / Q6
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(11f, -12f, 13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q4, Q6: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q1 / Q8
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(11f, 12f, -13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q1, Q8: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q2 / Q8
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(-11f, 12f, -13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q2, Q8: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q3 / Q7
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(-11f, -12f, -13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q3, Q7: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

        //Q4 / Q7
        firstPoint = Vector3(0f, 0f, 0f)
        secondPoint = Vector3(11f, -12f, -13f)
        angles = Pair(firstPoint, secondPoint).anglesBetween()
        println("Q4, Q7: r=${firstPoint.dst(secondPoint)}, tht(az)=${angles.first}, phi(po)=${angles.second}")

    }

    @Test
    fun testPositionByDistAndAngles() {

        var sphCoords = Vector3(5f, 0f, 0f)
        var newPosition = sphCoords.getPositionByDistanceAndAngles()
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



}
