import com.soywiz.klock.DateTime
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.cos
import com.soywiz.korma.geom.sin
import kotlin.random.Random
import kotlin.test.Test

@ExperimentalUnsignedTypes
class TestLeaf {

    @Test
    fun testOakLeaf() {
        val leaf = OakLeaf(initHeight = 3)
        println(leaf)
        println("node size : " + leaf.getLeafNodeList().size)
        for(node in leaf.getLeafNodeList()) {
            println(node.uuid)
        }
    }

    @Test
    fun testDropAndNav() {
        Random(DateTime.now().milliseconds)

        val leaf = OakLeaf(initHeight = 4)
        println(leaf)
        println("node size : " + leaf.getLeafNodeList().size)
        for(leafNode in leaf.getLeafNodeList()) {
            println(leafNode.uuid)
        }

        val randLeafIdx = Random.nextInt(leaf.getLeafNodeList().size)
        println("rand idx: $randLeafIdx")
        val randLeaf = leaf.getLeafNodeList()[randLeafIdx]
        println("rand leaf uuid: ${randLeaf.uuid}")
        println("rand leaf parent uuid: ${randLeaf.parentLeafNode?.uuid}")

        randLeaf.childrenLeafNodes.forEach { println("rand leaf child uuid: ${it?.uuid}") }
    }

    @ExperimentalUnsignedTypes
    @Test
    fun testAddLeaf() {
        Random(DateTime.now().milliseconds)

        val firstLeaf = OakLeaf(initHeight = 3)
        println(firstLeaf)
        println("first leaf node size : " + firstLeaf.getLeafNodeList().size)
        for(node in firstLeaf.getLeafNodeList()) {
            println(node.uuid)
        }

        val secondLeaf = OakLeaf(initHeight = 3)
        println(secondLeaf)
        println("second leaf node size : " + secondLeaf.getLeafNodeList().size)
        for(node in secondLeaf.getLeafNodeList()) {
            println(node.uuid)
        }

        val randLeafIdx = Random.nextInt(firstLeaf.getLeafNodeList().size)

        val randLeaf = firstLeaf.getLeafNodeList()[randLeafIdx]
        println("rand first idx: $randLeafIdx")
        println("rand leaf uuid: ${randLeaf.uuid}")
        println("rand leaf parent uuid: ${randLeaf.parentLeafNode?.uuid}")

        randLeaf.childrenLeafNodes.forEach { println("rand leaf child uuid: ${it?.uuid}") }
        println("firstLeaf curHeight: " + firstLeaf.getCurrentHeight())

        firstLeaf.getLeafNodeList()[randLeafIdx].childrenLeafNodes.add(firstLeaf.getLeafNodeList()[randLeafIdx].childrenLeafNodes.size, secondLeaf)
        println("updated first leaf node size : " + firstLeaf.getLeafNodeList().size)
        for(node in firstLeaf.getLeafNodeList()) {
            println(node.uuid)
        }

        val updRandLeaf = firstLeaf.getLeafNodeList()[randLeafIdx]
        println("upd rand leaf uuid: ${randLeaf.uuid}")
        println("upd rand leaf parent uuid: ${randLeaf.parentLeafNode?.uuid}")

        updRandLeaf.childrenLeafNodes.forEach { println("upd rand leaf child uuid: ${it?.uuid}") }
        println("upd firstLeaf curHeight: " + firstLeaf.getCurrentHeight())
    }

    @Test
    fun testLeafCoords() {
        val incrementLength = 20 //px
        val startingPoint = Point(512.0, 954.0)

        val nextXLeft = startingPoint.x + 5 * incrementLength * sin(Angle.Companion.fromDegrees(-20))
        val nextYLeft = startingPoint.y - 5 * incrementLength * cos(Angle.Companion.fromDegrees(-20))
        println ("$nextXLeft, $nextYLeft")

        val nextXRight = nextXLeft + 3 * incrementLength * sin(Angle.Companion.fromDegrees(30))
        val nextYRight = nextYLeft - 3 * incrementLength * cos(Angle.Companion.fromDegrees(30))
        println ("$nextXRight, $nextYRight")

        val leaf = OakLeaf(initHeight = 2, startingPosition = startingPoint)
        println(leaf)

        println(leaf.getLeafNodeList())

        println(leaf.getLeafLineList())

    }
}