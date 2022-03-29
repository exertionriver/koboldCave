package geom

import com.badlogic.gdx.math.MathUtils
import org.junit.jupiter.api.Test
import org.river.exertion.*
import org.river.exertion.geom.Line
import org.river.exertion.geom.Line.Companion.angleBetween
import org.river.exertion.geom.Line.Companion.getIntersection
import org.river.exertion.geom.Line.Companion.isInRect


@ExperimentalUnsignedTypes
class TestLine {

    @Test
    fun testIsInRectangleLeaf() {

        val rectA = Point(5f,0f)
        val rectB = Point(0f,2f)
        val rectC = Point(6f,3f)
        val rectD = Point(1f,5f)

        println("rect: $rectA, $rectB, $rectC, $rectD")

        (0..9).forEach { row ->
            (0..9).forEach { column ->
                if (Point(column.toFloat(), row.toFloat()).isInRect(listOf(rectA, rectB, rectC, rectD)) )
                        print ("x") else print (".")
            }
            println()
        }
    }

    @Test
    fun testIntersectsNew() {

        val rectA = Point(5f,0f)
        val rectB = Point(0f,2f)
        val rectC = Point(6f,3f)
        val rectD = Point(1f,5f)

        println("rect: $rectA, $rectB, $rectC, $rectD")

//        val intersect = Pair(rectA, rectD).getIntersection(Pair(rectB, rectC)) //intersect in middle
//        val intersect = Pair(rectB, rectD).getIntersection(Pair(rectD, rectC)) //intersect at edge
        val intersect = Line(rectB, rectD).getIntersection(Line(rectA, rectC)) //no intersect

        println("intersect: $intersect")

        (0..9).forEach { row ->
            (0..9).forEach { column ->
                when {
                    ( (intersect != null) && (column == intersect.x.toInt() && row == intersect.y.toInt()) ) -> print ("x")
                    ( (column.toFloat() == rectA.x && row.toFloat() == rectA.y) ) -> print ("o")
                    ( (column.toFloat() == rectB.x && row.toFloat() == rectB.y) ) -> print ("o")
                    ( (column.toFloat() == rectC.x && row.toFloat() == rectC.y) ) -> print ("o")
                    ( (column.toFloat() == rectD.x && row.toFloat() == rectD.y) ) -> print ("o")
                    else -> print (".")
                }
            }
            println()
        }
    }

    @Test
    fun testAngleBetween() {

        val centerPoint = Point(Game.initViewportWidth / 2, Game.initViewportHeight / 2)
        val xOffset = Point(Game.initViewportWidth / 4, 0F)
        val yOffset = Point(0F, Game.initViewportWidth / 4) // to make it appear circular

        val startingList = listOf(
            centerPoint - xOffset
            , centerPoint + Point(-xOffset.x * MathUtils.sin(45F.radians()), -yOffset.y * MathUtils.sin(45F.radians()))
            , centerPoint - yOffset
            , centerPoint + Point(xOffset.x * MathUtils.sin(45F.radians()), -yOffset.y * MathUtils.sin(45F.radians()))
            , centerPoint + xOffset
            , centerPoint + Point(xOffset.x * MathUtils.sin(45F.radians()), yOffset.y * MathUtils.sin(45F.radians()))
            , centerPoint + yOffset
            , centerPoint + Point(-xOffset.x * MathUtils.sin(45F.radians()), yOffset.y * MathUtils.sin(45F.radians()))
        )

        startingList.forEachIndexed { idx, _ -> println ("angleBetween ${startingList[idx]} and $centerPoint : ${startingList[idx].angleBetween(centerPoint)}")}
    }

    @Test
    fun testAngleBetweenRads() {

        val centerPoint = Point(Game.initViewportWidth / 2, Game.initViewportHeight / 2)
        val xOffset = Point(Game.initViewportWidth / 4, 0F)
        val yOffset = Point(0F, Game.initViewportWidth / 4) // to make it appear circular

        val startingList = listOf(
            centerPoint - xOffset
            , centerPoint + Point(-xOffset.x * MathUtils.sin(45F.radians()), -yOffset.y * MathUtils.sin(45F.radians()))
            , centerPoint - yOffset
            , centerPoint + Point(xOffset.x * MathUtils.sin(45F.radians()), -yOffset.y * MathUtils.sin(45F.radians()))
            , centerPoint + xOffset
            , centerPoint + Point(xOffset.x * MathUtils.sin(45F.radians()), yOffset.y * MathUtils.sin(45F.radians()))
            , centerPoint + yOffset
            , centerPoint + Point(-xOffset.x * MathUtils.sin(45F.radians()), yOffset.y * MathUtils.sin(45F.radians()))
        )

        startingList.forEachIndexed { idx, _ -> println ("angleBetween ${startingList[idx]} and $centerPoint : ${startingList[idx].angleBetween(centerPoint).radians()}")}
    }

}