package leaf

import com.soywiz.korma.geom.Point
import kotlin.math.max
import kotlin.math.min

//https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
object Line {

    // Given three colinear points p, q, r, the function checks if
    // point q lies on line segment 'pr'
    fun onSegment(p: Point, q: Point, r: Point): Boolean {
        return (q.x <= max(p.x, r.x)
                && q.x >= min(p.x, r.x)
                && q.y <= max(p.y, r.y)
                && q.y >= min(p.y, r.y) )
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

    fun Pair<Point, Point>.intersects(line: Pair<Point, Point>) : Boolean {
        val intersect = doIntersect(this.first, this.second, line.first, line.second)

  //      println ("intersect(${this.first}, ${this.second} to ${line.first}, ${line.second}) == $intersect")

        return intersect
    }


}