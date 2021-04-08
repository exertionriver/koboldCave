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
import leaf.Lace
import leaf.Lash

object RenderLaceLash {

    @ExperimentalUnsignedTypes
    suspend fun renderLaceLashStationary() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingPoint = Point(512.0, 512.0)

        (1..3).toList().forEach {

            val lace = Lace(topHeight = 8, topAngle = Angle.fromDegrees(90), position = startingPoint)
            val lash = Lash(topHeight = 8, topAngle = Angle.fromDegrees(90), position = startingPoint)

            graphics {
                stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {
                    for (line in lace.getLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }
                stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {
                    for (listLace in lace.getList()) {
                        circle(listLace.position, radius = 5.0)
                    }
                }
                stroke(Colors["#368431"], StrokeInfo(thickness = 3.0)) {
                    for (line in lash.getLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                }
                stroke(Colors["#4df041"], StrokeInfo(thickness = 3.0)) {
                    for (listLash in lash.getList() ) {
                        circle(listLash.position, radius = 5.0)
                    }
                }
            }
            delay(TimeSpan(1500.0))
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderLaceLashAngled() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

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

                val lace = Lace(topHeight = 6, topAngle = Angle.fromDegrees(180), angleFromParent = Angle.fromDegrees(it.key), position = it.value )
                val lash = Lash(topHeight = 6, topAngle = Angle.fromDegrees(180), angleFromParent = Angle.fromDegrees(it.key), position = it.value )

//                println ("tree: ${Angle.fromDegrees(it.key)}, ${it.value}")

                graphics {
                    stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {
                        for (line in lace.getLineList() ) {
                            if (line != null) line(line.first, line.second)
                        }
                    }
                    stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {
                        for (listLace in lace.getList()) {
                            circle(listLace.position, radius = 5.0)
                        }
                    }
                    stroke(Colors["#368431"], StrokeInfo(thickness = 3.0)) {
                        for (line in lash.getLineList()) {
                            if (line != null) line(line.first, line.second)
                        }
                    }
                    stroke(Colors["#4df041"], StrokeInfo(thickness = 3.0)) {
                        for (listLash in lash.getList() ) {
                            circle(listLash.position, radius = 5.0)
                        }
                    }

                    delay(TimeSpan(1500.0))
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderLaceCircle() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val centerPoint = Point(512.0, 512.0)

        val leafHeight = 7

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
    @ExperimentalUnsignedTypes
    suspend fun renderLashCircle() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val centerPoint = Point(512.0, 512.0)

        val leafHeight = 7

        val leafPoints = leafHeight + 1

        val leafMap = mutableMapOf<Angle, Point>()

        (0 until leafPoints).toList().forEach{ leafIndex ->
            val angleOnCircle = Angle.fromDegrees( 360 / leafPoints * leafIndex ).normalized

            //angleInMap points back to the center of the circle
            leafMap[(Angle.fromDegrees(180) + angleOnCircle).normalized] = ILeaf.getChildPosition(centerPoint, (leafHeight - 2) * NextDistancePx, angleOnCircle)
        }

        leafMap.forEach {

            val leaf = Lash(topHeight = leafHeight, topAngle = it.key, angleFromParent = it.key, position = it.value )

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