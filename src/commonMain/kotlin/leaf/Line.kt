package leaf

import com.soywiz.korma.geom.*
import leaf.Line.Companion.borderLines
import leaf.Line.Companion.extend
import leaf.Line.Companion.isQ1
import node.Node
import kotlin.math.*
import kotlin.math.cos

class Line(val first : Point, val second: Point) {

    fun intersects(line: Line) = Pair(this.first, this.second).intersects(Pair(line.first, line.second))

    fun asPoints() = Pair(first, second)

    companion object {

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

        //https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/

        // The main function that returns true if line segment 'p1q1'
        // and 'p2q2' intersect.
        fun doIntersect(p1: Point, q1: Point, p2: Point, q2: Point): Boolean {
            // Find the four orientations needed for general and
            // special cases
            val o1 = orientation(p1, q1, p2)
            val o2 = orientation(p1, q1, q2)
            val o3 = orientation(p2, q2, p1)
            val o4 = orientation(p2, q2, q1)

            // General case
            if (o1 != o2 && o3 != o4) return true

            // Special Cases
            // p1, q1 and p2 are colinear and p2 lies on segment p1q1
            if (o1 == 0 && onSegment(p1, p2, q1)) return true

            // p1, q1 and q2 are colinear and q2 lies on segment p1q1
            if (o2 == 0 && onSegment(p1, q2, q1)) return true

            // p2, q2 and p1 are colinear and p1 lies on segment p2q2
            if (o3 == 0 && onSegment(p2, p1, q2)) return true

            // p2, q2 and q1 are colinear and q1 lies on segment p2q2

            return o4 == 0 && onSegment(p2, q1, q2)
            // Doesn't fall in any of the above cases
        }

        fun Pair<Point, Point>.intersects(line: Pair<Point, Point>): Boolean {
            val intersect = doIntersect(this.first, this.second, line.first, line.second)

            //      println ("intersect(${this.first}, ${this.second} to ${line.first}, ${line.second}) == $intersect")

            return intersect
        }

        fun Pair<Point, Point>.isQ1() : Boolean {
            return ( (this.second.x - this.first.x) > 0  && (this.second.y - this.first.y) >= 0 )
        }

        fun Pair<Point, Point>.isQ2() : Boolean {
            return ( (this.second.x - this.first.x) <= 0  && (this.second.y - this.first.y) > 0 )
        }

        fun Pair<Point, Point>.isQ3() : Boolean {
            return ( (this.second.x - this.first.x) > 0  && (this.second.y - this.first.y) <= 0 )
        }

        fun Pair<Point, Point>.isQ4() : Boolean {
            return ( (this.second.x - this.first.x) >= 0  && (this.second.y - this.first.y) < 0 )
        }

        fun Pair<Point, Point>.extend(offset: Int) : Pair<Point, Point> {
            val slope = (this.second.y - this.first.y) / (this.second.x - this.first.x)
            val arctanSlope = atan(slope)
            val extendByX = sqrt(offset.toDouble().pow(2) - (sin(arctanSlope) * offset).pow(2))
            val extendByY = sqrt(offset.toDouble().pow(2) - (cos(arctanSlope) * offset).pow(2))

            var p3 : Point = this.first
            var p4 : Point = this.second

            when {
                this.isQ1() -> {
                    p3 = Point(x = this.first.x - extendByX, y = this.first.y - extendByY)
                    p4 = Point(x = this.second.x + extendByX, y = this.second.y + extendByY)
                }
                this.isQ2() -> {
                    p3 = Point(x = this.first.x + extendByX, y = this.first.y - extendByY)
                    p4 = Point(x = this.second.x - extendByX, y = this.second.y + extendByY)
                }
                this.isQ3() -> {
                    p3 = Point(x = this.first.x + extendByX, y = this.first.y + extendByY)
                    p4 = Point(x = this.second.x - extendByX, y = this.second.y - extendByY)
                }
                this.isQ4() -> {
                    p3 = Point(x = this.first.x - extendByX, y = this.first.y + extendByY)
                    p4 = Point(x = this.second.x + extendByX, y = this.second.y - extendByY)
                }
            }

            return Pair(p3, p4)
        }

        fun Pair<Point, Point>.borderPoints(offset: Int) : List<Point> {
            val slope = (this.second.y - this.first.y) / (this.second.x - this.first.x)
            val arctanSlope = atan(slope)
            val orthoExtendByX = sqrt(offset.toDouble().pow(2) - (cos(arctanSlope) * offset).pow(2))
            val orthoExtendByY = sqrt(offset.toDouble().pow(2) - (sin(arctanSlope) * offset).pow(2))

            var p3 : Point = this.first
            var p4 : Point = this.second
            var p5 : Point = this.first
            var p6 : Point = this.second

            val extendedLine = this.extend(offset)

            when {
                this.isQ1() -> {
                    p3 = Point(x = extendedLine.first.x - orthoExtendByX, y = extendedLine.first.y + orthoExtendByY)
                    p4 = Point(x = extendedLine.second.x - orthoExtendByX, y = extendedLine.second.y + orthoExtendByY)
                    p5 = Point(x = extendedLine.first.x + orthoExtendByX, y = extendedLine.first.y - orthoExtendByY)
                    p6 = Point(x = extendedLine.second.x + orthoExtendByX, y = extendedLine.second.y - orthoExtendByY)
                }
                this.isQ2() -> {
                    p3 = Point(x = extendedLine.first.x + orthoExtendByX, y = extendedLine.first.y + orthoExtendByY)
                    p4 = Point(x = extendedLine.second.x + orthoExtendByX, y = extendedLine.second.y + orthoExtendByY)
                    p5 = Point(x = extendedLine.first.x - orthoExtendByX, y = extendedLine.first.y - orthoExtendByY)
                    p6 = Point(x = extendedLine.second.x - orthoExtendByX, y = extendedLine.second.y - orthoExtendByY)
                }
                this.isQ3() -> {
                    p3 = Point(x = extendedLine.first.x + orthoExtendByX, y = extendedLine.first.y - orthoExtendByY)
                    p4 = Point(x = extendedLine.second.x + orthoExtendByX, y = extendedLine.second.y - orthoExtendByY)
                    p5 = Point(x = extendedLine.first.x - orthoExtendByX, y = extendedLine.first.y + orthoExtendByY)
                    p6 = Point(x = extendedLine.second.x - orthoExtendByX, y = extendedLine.second.y + orthoExtendByY)
                }
                this.isQ4() -> {
                    p3 = Point(x = extendedLine.first.x - orthoExtendByX, y = extendedLine.first.y - orthoExtendByY)
                    p4 = Point(x = extendedLine.second.x - orthoExtendByX, y = extendedLine.second.y - orthoExtendByY)
                    p5 = Point(x = extendedLine.first.x + orthoExtendByX, y = extendedLine.first.y + orthoExtendByY)
                    p6 = Point(x = extendedLine.second.x + orthoExtendByX, y = extendedLine.second.y + orthoExtendByY)
                }
            }

            return listOf(p3, p4, p5, p6)
        }

        fun Pair<Point, Point>.borderLines(offset: Int) : List<Line> {
            val points : List<Point> = Pair(this.first, this.second).borderPoints(offset)

            return listOf(Line(first = points[0], second = points[2]), Line(first = points[1], second = points[3])
                , Line(first = points[0], second = points[1]), Line(first = points[2], second = points[3]) )
        }

        //        https://stackoverflow.com/questions/2752725/finding-whether-a-point-lies-inside-a-rectangle-or-not
        fun Pair<Point, Point>.vector() : Point = Point(x = this.second.x - this.first.x, y = this.second.y - this.first.y)

        fun Pair<Line, Line>.dot() : Double {
            val u = Pair(this.first.first, this.first.second).vector()
            val v = Pair(this.second.first, this.second.second).vector()

            return u.x * v.x + u.y * v.y
        }

        //only first three points in rectPoints are used
        //AB and BC must be perpendicular
        fun Point.isInRect(rectPoints : List<Point>) : Boolean {
            val ab = Line(rectPoints[0], rectPoints[1])
            val am = Line(rectPoints[0], this)
            val bc = Line(rectPoints[1], rectPoints[3])
            val bm = Line(rectPoints[1], this)

            return ( ( 0 <= Pair(ab, am).dot() ) &&
                    ( Pair(ab, am).dot() <= Pair(ab, ab).dot() ) &&
                    ( 0 <= Pair(bc, bm).dot() ) &&
                    ( Pair(bc, bm).dot() <= Pair(bc, bc).dot() ) )
        }

        fun Point.isInBorder(line : Pair<Point, Point>, offset : Int) : Boolean {
            val borderPoints = line.borderPoints(offset)

//            println("check borderPoints: $borderPoints contain $this")
            return this.isInRect(listOf(borderPoints[0], borderPoints[1], borderPoints[2], borderPoints[3]) )
        }

        fun Pair<Point, Point>.intersectsBorder(line : Pair<Point, Point>, offset : Int) : Boolean {
            val borderLines = line.borderLines(offset)

            var intersection = false

            for (borderLine in borderLines) {
//                println("check borderLine: ${borderLine.first}, ${borderLine.second} intersects with $line")
                if ( this.intersects(borderLine.asPoints()) ) intersection = true
            }

            return intersection
        }

        fun MutableList<Point>.averagePositionWithinPoints() : Point {
            val averageX = this.map {point -> point.x.toInt()}.average()
            val averageY = this.map {point -> point.y.toInt()}.average()

            return Point(averageX, averageY)
        }

        fun Angle.isQ1() = (this.degrees >= 0) && (this.degrees < 90)
        fun Angle.isQ2() = (this.degrees >= 90) && (this.degrees < 180)
        fun Angle.isQ3() = (this.degrees >= 180) && (this.degrees < 270)
        fun Angle.isQ4() = (this.degrees >= 270) && (this.degrees <= 360)


        fun getPositionByDistanceAndAngle(first: Point, distance: Int, angle: Angle): Point {

            val secondX = when {
                angle.isQ1() -> first.x + distance * cos(angle)
                angle.isQ2() -> first.x - distance * cos(Angle.fromDegrees(180) - angle )
                angle.isQ3() -> first.x - distance * cos(angle - Angle.fromDegrees(180) )
                else -> first.x + distance * cos(Angle.fromDegrees(360) - angle )
            }

            val secondY = when {
                angle.isQ1() -> first.y - distance * sin(angle)
                angle.isQ2() -> first.y - distance * sin(Angle.fromDegrees(180) - angle )
                angle.isQ3() -> first.y + distance * sin(angle - Angle.fromDegrees(180) )
                else -> first.y + distance * sin(Angle.fromDegrees(360) - angle)
            }

            return Point(secondX, secondY)
        }
    }
}