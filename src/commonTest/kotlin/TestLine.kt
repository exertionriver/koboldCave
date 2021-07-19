import com.soywiz.klock.DateTime
import com.soywiz.korge.Korge
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import leaf.ILeaf.Companion.add
import leaf.ILeaf.Companion.prune
import leaf.Leaf
import leaf.Line.Companion.isInRect
import kotlin.random.Random
import kotlin.test.Test

@ExperimentalUnsignedTypes
class TestLine {

    @Test
    fun testIsInRectangleLeaf() {

        val rectA = Point(5,0)
        val rectB = Point(0,2)
        val rectC = Point(6,3)
        val rectD = Point(1,5)

        println("rect: $rectA, $rectB, $rectC, $rectD")

        (0..9).forEach { row ->
            (0..9).forEach { column ->
                if (Point(column, row).isInRect(listOf(rectA, rectB, rectC, rectD)) )//|| Point(row, column).isInRect(listOf(rectB, rectD, rectA, rectC)) )
                        print ("x") else print (".")
            }
            println()
        }
    }
}