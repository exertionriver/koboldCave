package leaf

import com.soywiz.korma.geom.Point
import leaf.Line.Companion.extend
import leaf.Line.Companion.isQ1
import kotlin.math.*

class Line(val first : Point, val second: Point) {

    fun intersects(line: Line) = Pair(this.first, this.second).intersects(Pair(line.first, line.second))

    fun borders(offsetThreshold: Int) = Pair(this.first, this.second).borders(offsetThreshold)

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

        fun Pair<Point, Point>.extend(offset: Int) : Line {
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

            return Line(first = p3, second = p4)
        }

        fun Pair<Point, Point>.borders(offset: Int): List<Line> {
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

            return listOf( Line(first = p3, second = p4), Line(first = p5, second = p6), Line(first=p3, second=p5), Line(first=p4, second=p6) )
        }
    }
}