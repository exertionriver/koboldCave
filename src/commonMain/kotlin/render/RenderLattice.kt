package render

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.Korge
import com.soywiz.korge.view.graphics
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import leaf.ILeaf
import leaf.ILeaf.Companion.NextDistancePx
import leaf.ILeaf.Companion.add
import leaf.ILeaf.Companion.graft
import leaf.Leaf
import leaf.Lace
import leaf.Lattice
import kotlin.random.Random

object RenderLattice {

    @ExperimentalUnsignedTypes
    suspend fun renderLatticeStationary() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingPoint = Point(512.0, 512.0)

        (1..3).toList().forEach {

            val lattice = Lattice(topHeight = 6, position = startingPoint, topAngle = Angle.fromDegrees(270) )

            graphics {
                stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {

                    for (line in lattice.getLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }
                stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                    for (listLattice in lattice.getList() ) {
                        circle(listLattice.position, radius = 5.0)
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

        val firstLeaf = Leaf(topHeight = 5, position = firstStartingPoint)
        val secondLeaf = Leaf(topHeight = 5, position = secondStartingPoint)

        val thirdLeaf = Leaf(topHeight = 5, position = thirdStartingPoint)
        val fourthLeaf = Leaf(topHeight = 5, position = fourthStartingPoint)

        val firstRandLeafIdx = Random.nextInt(firstLeaf.getList().size)
        val thirdRandLeafIdx = Random.nextInt(thirdLeaf.getList().size)

        firstLeaf.getList()[firstRandLeafIdx].add(secondLeaf)
        thirdLeaf.getList()[thirdRandLeafIdx].graft(fourthLeaf)

        graphics {
            stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {

                for (line in firstLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                for (leaf in firstLeaf.getList() ) {
                    circle(leaf.position, radius = 5.0)
                }
            }

            stroke(Colors["#268184"], StrokeInfo(thickness = 3.0)) {

                for (line in secondLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(Colors["#59eef0"], StrokeInfo(thickness = 3.0)) {

                for (leaf in secondLeaf.getList() ) {
                    circle(leaf.position, radius = 5.0)
                }
            }

            stroke(Colors["#818436"], StrokeInfo(thickness = 3.0)) {

                for (line in thirdLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(Colors["#f0f057"], StrokeInfo(thickness = 3.0)) {

                for (leaf in thirdLeaf.getList() ) {
                    circle(leaf.position, radius = 5.0)
                }
            }

            stroke(Colors["#844a32"], StrokeInfo(thickness = 3.0)) {

                for (line in fourthLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(Colors["#f08154"], StrokeInfo(thickness = 3.0)) {

                for (leaf in fourthLeaf.getList() ) {
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

                val leaf = Leaf(topHeight = 4, angleFromParent = Angle.fromDegrees(it.key), position = it.value )

//                println ("tree: ${Angle.fromDegrees(it.key)}, ${it.value}")

                graphics {

                    stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {

                        for (line in leaf.getLineList() ) {
                            if (line != null) line(line.first, line.second)
                        }
                    }

                    stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                        for (listLeaf in leaf.getList() ) {
                            circle(listLeaf.position, radius = 5.0)
                        }
                    }

                    delay(TimeSpan(1500.0))

                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderStreamCircle() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val centerPoint = Point(512.0, 512.0)

        val leafHeight = 9

        val leafPoints = leafHeight + 1

        val leafMap = mutableMapOf<Angle, Point>()

        (0 until leafPoints).toList().forEach{ leafIndex ->
            val angleOnCircle = Angle.fromDegrees( 360 / leafPoints * leafIndex ).normalized

            //angleInMap points back to the center of the circle
            leafMap[(Angle.fromDegrees(180) + angleOnCircle).normalized] = ILeaf.getChildPosition(centerPoint, (leafHeight - 2) * NextDistancePx, angleOnCircle)
        }

        leafMap.forEach {

            val leaf = Lace(topHeight = leafHeight, topAngle = it.key, angleFromParent = it.key, position = it.value )

//                println ("tree: ${it.key.degrees}, ${it.value}")

            val finalPoints = mutableListOf<Point>()

            graphics {

                stroke(Colors["#848323"], StrokeInfo(thickness = 1.0)) {

                    line(it.value, centerPoint)
                }
                
                stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {

                    for (line in leaf.getLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                    val leafList = leaf.getList()
                    val leafListSize = leafList.size

                    leafList.forEachIndexed { leafIndex, listLeaf ->
                        circle(listLeaf.position, radius = 5.0)

                        if (leafIndex == leafListSize - 1) finalPoints.add(listLeaf.position)
                    }
                }
                stroke(Colors["#842b27"], StrokeInfo(thickness = 3.0)) {

                    finalPoints.forEach { finalPoint ->
                        line(finalPoint, centerPoint)
                    }
                }
            }
        }
    }
}