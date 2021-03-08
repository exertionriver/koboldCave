import com.soywiz.klock.TimeSpan
import com.soywiz.korge.Korge
import com.soywiz.korge.view.graphics
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.sin
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line

object RenderOakLeaf {

    @ExperimentalUnsignedTypes
    suspend fun renderOakLeafStationary() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingPoint = Point(512.0, 974.0)

        while (true) {

            val leaf = OakLeaf(initHeight = 3, startingPosition = startingPoint)

            graphics {
                stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                    for (node in leaf.getLeafNodeList() ) {
                        circle(node.position, radius = 5.0)
                    }
                }
                stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {

                    for (line in leaf.getLeafLineList() ) {
                        line(line.first, line.second)
                    }
                }
            }
            delay(TimeSpan(1500.0))
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderOakLeafAngled() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingPoint = Point(512.0, 974.0)

        val startingMap = mapOf(
            0 to startingPoint
            , 45 to Point(512 - 462 * sin(Angle.fromDegrees(45)), 512 + 462 * sin(Angle.fromDegrees(45)) )
            , 90 to Point(50.0, 512.0)
            , 135 to Point(512 - 462 * sin(Angle.fromDegrees(45)), 512 - 462 * sin(Angle.fromDegrees(45)) )
            , 180 to Point(512.0, 50.0)
            , 225 to Point(512 + 462 * sin(Angle.fromDegrees(45)), 512 - 462 * sin(Angle.fromDegrees(45)) )
            , 270 to Point(974.0, 512.0)
            , 315 to Point(512 + 462 * sin(Angle.fromDegrees(45)), 512 + 462 * sin(Angle.fromDegrees(45)) )
        )

        while (true) {
            startingMap.forEach {

                val leaf = OakLeaf(initHeight = 7, startingAngle = Angle.fromDegrees(it.key), startingPosition = it.value, relativeAngle = true)
                graphics {

                    stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                        for (node in leaf.getLeafNodeList() ) {
                            circle(node.position, radius = 5.0)
                        }
                    }

                    stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {

                        for (line in leaf.getLeafLineList() ) {
                            line(line.first, line.second)
                        }
                    }
                    delay(TimeSpan(1500.0))

                }
            }
        }
    }
}