package org.river.exertion.geom

import com.badlogic.gdx.math.MathUtils.cos
import com.badlogic.gdx.math.MathUtils.sin
import org.river.exertion.*
import kotlin.math.*

class Line(val first : Point, val second: Point) {

//    fun intersects(line: Line) = this.intersects(line)

//    fun asPoints() = Pair(first, second)

//    fun points() = listOf(first, second)

    companion object {

//        fun List<Line>.points() : Set<Point> = this.map { it.first }.toMutableSet().plus(this.map { it.second }.toMutableSet()).toSet()

        // Given three colinear points p, q, r, the function checks if
        // point q lies on line segment 'pr'
        fun onSegment(p: Point, q: Point, r: Point): Boolean {
            return (q.x <= max(p.x, r.x)
                    && q.x >= min(p.x, r.x)
                    && q.y <= max(p.y, r.y)
                    && q.y >= min(p.y, r.y))
        }

        // To find orientation of ordered triplet (p, q, r).
        // The function returns following values
        // 0 --> p, q and r are colinear
        // 1 --> Clockwise
        // 2 --> Counterclockwise
        fun orientation(p: Point, q: Point, r: Point): Int {
            // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
            // for details of below formula.
            val calcOrientation = ((q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y)).toInt()
            if (calcOrientation == 0) return 0 // colinear
            return if (calcOrientation > 0) 1 else 2 // clock or counterclock wise
        }

//       https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
        fun Line.getIntersection(line: Line): Point? {

            val p0x = this.first.x
            val p0y = this.first.y
            val p1x = this.second.x
            val p1y = this.second.y
            val p2x = line.first.x
            val p2y = line.first.y
            val p3x = line.second.x
            val p3y = line.second.y

            val s1x = p1x - p0x
            val s1y = p1y - p0y
            val s2x = p3x - p2x
            val s2y = p3y - p2y

            val s = (-s1y * (p0x - p2x) + s1x * (p0y - p2y)) / (-s2x * s1y + s1x * s2y)
            val t = ( s2x * (p0y - p2y) - s2y * (p0x - p2x)) / (-s2x * s1y + s1x * s2y)

            // Collision detected
            if (s >= 0 && s <= 1 && t >= 0 && t <= 1) return Point(p0x + (t * s1x), p0y + (t * s1y) )

            return null
        }

        fun Line.intersects(line: Line): Boolean {

            return (this.getIntersection(line) != null)
        }

        fun Angle.isQ1() = (this >= 0F) && (this < 90F)
        fun Angle.isQ2() = (this >= 90F) && (this < 180F)
        fun Angle.isQ3() = (this >= 180F) && (this < 270F)
        fun Angle.isQ4() = (this >= 270F) && (this <= 360F)

        fun Line.isQ1() : Boolean {
            return ( (this.second.x - this.first.x) > 0 && (this.second.y - this.first.y) >= 0 )
        }

        fun Line.isQ2() : Boolean {
            return ( (this.second.x - this.first.x) <= 0 && (this.second.y - this.first.y) > 0 )
        }

        fun Line.isQ3() : Boolean {
            return ( (this.second.x - this.first.x) < 0 && (this.second.y - this.first.y) <= 0 )
        }

        fun Line.isQ4() : Boolean {
            return ( (this.second.x - this.first.x) >= 0 && (this.second.y - this.first.y) < 0 )
        }

        fun Line.extend(offset: Int) : Line {
            val slope = (this.second.y - this.first.y) / (this.second.x - this.first.x)
            val arctanSlope = atan(slope)
            val extendByX = sqrt(offset.toFloat().pow(2) - (sin(arctanSlope) * offset).pow(2))
            val extendByY = sqrt(offset.toFloat().pow(2) - (cos(arctanSlope) * offset).pow(2))

            var p3 : Point = this.first
            var p4 : Point = this.second

            when {
                this.isQ1() -> {
                    p3 = Point(this.first.x - extendByX, this.first.y - extendByY)
                    p4 = Point(this.second.x + extendByX, this.second.y + extendByY)
                }
                this.isQ2() -> {
                    p3 = Point(this.first.x + extendByX, this.first.y - extendByY)
                    p4 = Point(this.second.x - extendByX, this.second.y + extendByY)
                }
                this.isQ3() -> {
                    p3 = Point(this.first.x + extendByX, this.first.y + extendByY)
                    p4 = Point(this.second.x - extendByX, this.second.y - extendByY)
                }
                this.isQ4() -> {
                    p3 = Point(this.first.x - extendByX, this.first.y + extendByY)
                    p4 = Point(this.second.x + extendByX, this.second.y - extendByY)
                }
            }

            return Line(p3, p4)
        }

        fun Line.borderPoints(offset: Int) : List<Point> {
            val slope = (this.second.y - this.first.y) / (this.second.x - this.first.x)
            val arctanSlope = atan(slope)
            val orthoExtendByX = sqrt(offset.toFloat().pow(2) - (cos(arctanSlope) * offset).pow(2))
            val orthoExtendByY = sqrt(offset.toFloat().pow(2) - (sin(arctanSlope) * offset).pow(2))

            var p3 : Point = this.first
            var p4 : Point = this.second
            var p5 : Point = this.first
            var p6 : Point = this.second

            val extendedLine = this.extend(offset)

            when {
                this.isQ1() -> {
                    p3 = Point(extendedLine.first.x - orthoExtendByX, extendedLine.first.y + orthoExtendByY)
                    p4 = Point(extendedLine.second.x - orthoExtendByX, extendedLine.second.y + orthoExtendByY)
                    p5 = Point(extendedLine.first.x + orthoExtendByX, extendedLine.first.y - orthoExtendByY)
                    p6 = Point(extendedLine.second.x + orthoExtendByX, extendedLine.second.y - orthoExtendByY)
                }
                this.isQ2() -> {
                    p3 = Point(extendedLine.first.x + orthoExtendByX, extendedLine.first.y + orthoExtendByY)
                    p4 = Point(extendedLine.second.x + orthoExtendByX, extendedLine.second.y + orthoExtendByY)
                    p5 = Point(extendedLine.first.x - orthoExtendByX, extendedLine.first.y - orthoExtendByY)
                    p6 = Point(extendedLine.second.x - orthoExtendByX, extendedLine.second.y - orthoExtendByY)
                }
                this.isQ3() -> {
                    p3 = Point(extendedLine.first.x - orthoExtendByX, extendedLine.first.y + orthoExtendByY)
                    p4 = Point(extendedLine.second.x - orthoExtendByX, extendedLine.second.y + orthoExtendByY)
                    p5 = Point(extendedLine.first.x + orthoExtendByX, extendedLine.first.y - orthoExtendByY)
                    p6 = Point(extendedLine.second.x + orthoExtendByX, extendedLine.second.y - orthoExtendByY)
                }
                this.isQ4() -> {
                    p3 = Point(extendedLine.first.x - orthoExtendByX, extendedLine.first.y - orthoExtendByY)
                    p4 = Point(extendedLine.second.x - orthoExtendByX, extendedLine.second.y - orthoExtendByY)
                    p5 = Point(extendedLine.first.x + orthoExtendByX, extendedLine.first.y + orthoExtendByY)
                    p6 = Point(extendedLine.second.x + orthoExtendByX, extendedLine.second.y + orthoExtendByY)
                }
            }

            return listOf(p3, p4, p5, p6)
        }

        fun Line.borderLines(offset: Int) : List<Line> {
            val points : List<Point> = this.borderPoints(offset)

            return listOf(
                Line(first = points[0], second = points[1]), Line(first = points[2], second = points[3])
                , Line(first = points[0], second = points[2]), Line(first = points[1], second = points[3]) )
        }

        //        https://stackoverflow.com/questions/2752725/finding-whether-a-point-lies-inside-a-rectangle-or-not
        fun Pair<Point, Point>.vector() : Point = Point(this.second.x - this.first.x, this.second.y - this.first.y)

        fun Pair<Point, Point>.dot() : Float {
            return this.first.x * this.second.x + this.first.y * this.second.y
        }

        fun Pair<Point, Point>.vecEqual() : Boolean {
            return this.first.x == this.second.x && this.first.y == this.second.y
        }
        //only first three points in rectPoints are used
        //AB and BC must be perpendicular
        fun Point.isInRect(rectPoints : List<Point>) : Boolean {
            val ab = Pair(rectPoints[0], rectPoints[1]).vector()
            val ad = Pair(rectPoints[0], rectPoints[2]).vector()
            val am = Pair(rectPoints[0], this).vector()

            val insideRect = ( 0 <= Pair(am, ab).dot() ) &&
                    ( Pair(am, ab).dot() <= Pair(ab, ab).dot() ) &&
                    ( 0 <= Pair(am, ad).dot() ) &&
                    ( Pair(am, ad).dot() <= Pair(ad, ad).dot() )

            val onRectPoints = ( Pair(this, rectPoints[0]).vecEqual() ) ||
                    Pair(this, rectPoints[1]).vecEqual() ||
                    Pair(this, rectPoints[2]).vecEqual() ||
                    Pair(this, rectPoints[3]).vecEqual()

            return insideRect || onRectPoints
        }

        fun Point.isInBorder(line : Line, offset : Int) : Boolean {
            val borderPoints = line.borderPoints(offset)

            var inBorder = false

//            println("check borderPoints: $borderPoints contain $this")
            if ( this.isInRect(listOf(borderPoints[0], borderPoints[1], borderPoints[2], borderPoints[3]) ) ) {
//                println("in Border!")
                inBorder = true
            }

            return inBorder
        }

        //returns truncated points, ie. whole number points
        fun Line.pointsInBorder(offset: Int) : Set<Point> {

            val pointsSet = mutableSetOf<Point>()

            val borderPoints = if (offset < 1) this.borderPoints(1) else this.borderPoints(offset)

            val minX = borderPoints.minOf { it.x }.toInt()
            val minY = borderPoints.minOf { it.y }.toInt()
            val maxX = borderPoints.maxOf { it.x }.toInt()
            val maxY = borderPoints.maxOf { it.y }.toInt()

            (minX..maxX).forEach { xIter ->
                (minY..maxY).forEach { yIter ->
                    val checkPoint = Point(xIter.toFloat(), yIter.toFloat())

                    if ( checkPoint.isInRect(listOf(borderPoints[0], borderPoints[1], borderPoints[2], borderPoints[3]) ) ) {
                        pointsSet.add(checkPoint.trunc())
                    }
                }
            }
            return pointsSet.toSet()
        }

        fun Line.intersectsBorder(line : Line, offset : Int) : Boolean {
            val borderLines = line.borderLines(offset)

            var intersection = false

            for (borderLine in borderLines) {
//                println("check borderLine: ${borderLine.first}, ${borderLine.second} intersects with $line")
                if ( this.intersects(borderLine) ) {
//                    println("intersects!")
                    intersection = true
                }
            }

            return intersection
        }

        fun MutableSet<Point>.averagePositionWithinPoints() : Point {
            val averageX = this.map {point -> point.x }.average().toFloat()
            val averageY = this.map {point -> point.y }.average().toFloat()

            return Point(averageX, averageY)
        }

        fun Point.getPositionByDistanceAndAngle(distance: Float, angle: Angle): Point {

            val secondX = when {
                angle.isQ1() -> this.x + distance * cos(angle.radians())
                angle.isQ2() -> this.x - distance * cos( (180F - angle).radians() )
                angle.isQ3() -> this.x - distance * cos( (angle - 180F).radians() )
                else -> this.x + distance * cos((360F - angle).radians() )
            }

            val secondY = when {
                angle.isQ1() -> this.y + distance * sin(angle.radians())
                angle.isQ2() -> this.y + distance * sin((180F - angle).radians() )
                angle.isQ3() -> this.y - distance * sin((angle - 180F).radians() )
                else -> this.y - distance * sin((360F - angle).radians() )
            }

//            println("position: $this, distance: $distance, angle: $angle, new position: ($secondX, $secondY)")

            return Point(secondX, secondY)
        }

        fun Point.getArrayedPositionByAngle(topAngle: Angle, childAngle: Angle): Point {

            val uniformDistanceFromParent = .5 * NextDistancePx

            val angleDiff = abs(topAngle - childAngle)
            val arrayPosition = uniformDistanceFromParent * tan(angleDiff.radians())
            val distanceFromParent = hypot(uniformDistanceFromParent, arrayPosition).toFloat()

            return this.getPositionByDistanceAndAngle(distanceFromParent, childAngle)
        }

        fun Point.angleBetween(secondPoint : Point) : Angle {
            return when {
                Line(this, secondPoint).isQ1() -> { atan((secondPoint.y - this.y) / (secondPoint.x - this.x) ) }
                Line(this, secondPoint).isQ2() -> { 180f.radians() - atan((secondPoint.y - this.y) / (this.x - secondPoint.x) ) }
                Line(this, secondPoint).isQ3() -> { 180f.radians() + atan((secondPoint.y - this.y) / (secondPoint.x - this.x) ) }
                else -> { 360F.radians() - atan((this.y - secondPoint.y) / (secondPoint.x - this.x) ) }
            }.degrees()
        }
    }
}