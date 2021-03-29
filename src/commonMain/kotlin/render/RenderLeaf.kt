package render

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
import leaf.ILeaf.Companion.addLeaf
import leaf.ILeaf.Companion.graftLeaf
import leaf.Leaf
import kotlin.random.Random

object RenderLeaf {

    @ExperimentalUnsignedTypes
    suspend fun renderLeafStationary() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingPoint = Point(512.0, 512.0)

        (1..3).toList().forEach {

            val leaf = Leaf(initHeight = 6, position = startingPoint)

            graphics {
                stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {

                    for (line in leaf.getLeafLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }
                stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                    for (listLeaf in leaf.getLeafList() ) {
                        circle(listLeaf.position, radius = 5.0)
                    }
                }
            }
            delay(TimeSpan(1500.0))
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderAddLeafStationary() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val firstStartingPoint = Point(64.0, 64.0)
        val secondStartingPoint = Point(256.0, 256.0)

        val thirdStartingPoint = Point(512.0, 512.0)
        val fourthStartingPoint = Point(768.0, 768.0)

        val firstLeaf = Leaf(initHeight = 5, position = firstStartingPoint)
        val secondLeaf = Leaf(initHeight = 5, position = secondStartingPoint)

        val thirdLeaf = Leaf(initHeight = 5, position = thirdStartingPoint)
        val fourthLeaf = Leaf(initHeight = 5, position = fourthStartingPoint)

        val firstRandLeafIdx = Random.nextInt(firstLeaf.getLeafList().size)
        val thirdRandLeafIdx = Random.nextInt(thirdLeaf.getLeafList().size)

        firstLeaf.getLeafList()[firstRandLeafIdx].addLeaf(secondLeaf)
        thirdLeaf.getLeafList()[thirdRandLeafIdx].graftLeaf(fourthLeaf)

        graphics {
            stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {

                for (line in firstLeaf.getLeafLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                for (leaf in firstLeaf.getLeafList() ) {
                    circle(leaf.position, radius = 5.0)
                }
            }

            stroke(Colors["#268184"], StrokeInfo(thickness = 3.0)) {

                for (line in secondLeaf.getLeafLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(Colors["#59eef0"], StrokeInfo(thickness = 3.0)) {

                for (leaf in secondLeaf.getLeafList() ) {
                    circle(leaf.position, radius = 5.0)
                }
            }

            stroke(Colors["#818436"], StrokeInfo(thickness = 3.0)) {

                for (line in thirdLeaf.getLeafLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(Colors["#f0f057"], StrokeInfo(thickness = 3.0)) {

                for (leaf in thirdLeaf.getLeafList() ) {
                    circle(leaf.position, radius = 5.0)
                }
            }

            stroke(Colors["#844a32"], StrokeInfo(thickness = 3.0)) {

                for (line in fourthLeaf.getLeafLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(Colors["#f08154"], StrokeInfo(thickness = 3.0)) {

                for (leaf in fourthLeaf.getLeafList() ) {
                    circle(leaf.position, radius = 5.0)
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderLeafAngled() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingPoint = Point(512.0, 974.0)

        val startingMap = mapOf(
            90 to startingPoint
            , 45 to Point(512 - 462 * sin(Angle.fromDegrees(45)), 512 + 462 * sin(Angle.fromDegrees(45)) )
            , 0 to Point(50.0, 512.0)
            , 315 to Point(512 - 462 * sin(Angle.fromDegrees(45)), 512 - 462 * sin(Angle.fromDegrees(45)) )
            , 270 to Point(512.0, 50.0)
            , 225 to Point(512 + 462 * sin(Angle.fromDegrees(45)), 512 - 462 * sin(Angle.fromDegrees(45)) )
            , 180 to Point(974.0, 512.0)
            , 135 to Point(512 + 462 * sin(Angle.fromDegrees(45)), 512 + 462 * sin(Angle.fromDegrees(45)) )
        )

        while (true) {
            startingMap.forEach {

                val leaf = Leaf(initHeight = 4, angleFromParent = Angle.fromDegrees(it.key), position = it.value )

//                println ("tree: ${Angle.fromDegrees(it.key)}, ${it.value}")

                graphics {

                    stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {

                        for (line in leaf.getLeafLineList() ) {
                            if (line != null) line(line.first, line.second)
                        }
                    }

                    stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                        for (listLeaf in leaf.getLeafList() ) {
                            circle(listLeaf.position, radius = 5.0)
                        }
                    }

                    delay(TimeSpan(1500.0))

                }
            }
        }
    }
}