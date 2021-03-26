import com.soywiz.klock.DateTime
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.cos
import com.soywiz.korma.geom.sin
import leaf.ILeaf.Companion.addLeaf
import leaf.Leaf
import kotlin.random.Random
import kotlin.test.Test

@ExperimentalUnsignedTypes
class TestLeaf {

    @Test
    fun testLeaf() {
        val leaf = Leaf(initHeight = 3)
        println(leaf)
        println("leaf size : " + leaf.getLeafList().size)
        for(listLeaf in leaf.getLeafList()) {
            println(listLeaf)
        }
    }

    @Test
    fun testRandLeaf() {
        Random(DateTime.now().milliseconds)

        val leaf = Leaf(initHeight = 4)
        println(leaf)
        println("leaf size : " + leaf.getLeafList().size)
        for(listLeaf in leaf.getLeafList()) {
            println(listLeaf)
        }

        val randLeafIdx = Random.nextInt(leaf.getLeafList().size)
        println("rand idx: $randLeafIdx")
        val randLeaf = leaf.getLeafList()[randLeafIdx]
        println("rand leaf uuid: $randLeaf")
        println("rand leaf parent uuid: ${randLeaf.getParentLeaf()}")

        randLeaf.getChildrenLeavesList()?.forEach { println("rand leaf child uuid: ${it.uuid}") }
    }

    @ExperimentalUnsignedTypes
    @Test
    fun testAddRandLeaf() {
        Random(DateTime.now().milliseconds)

        val firstLeaf = Leaf(initHeight = 3)
        println("first leaf size : " + firstLeaf.getLeafList().size)
        for(listLeaf in firstLeaf.getLeafList()) {
            println(listLeaf)
        }

        val secondLeaf = Leaf(initHeight = 3)
        println("second leaf size : " + secondLeaf.getLeafList().size)
        for(listLeaf in secondLeaf.getLeafList()) {
            println(listLeaf)
        }

        val randLeafIdx = Random.nextInt(firstLeaf.getLeafList().size)

        val randLeaf = firstLeaf.getLeafList()[randLeafIdx]
        println("rand first idx: $randLeafIdx")
        println("rand leaf uuid: ${randLeaf.uuid}")
        println("rand leaf parent uuid: ${randLeaf.getParentLeaf()?.uuid}")

        randLeaf.getChildrenLeavesList()?.forEach { println("rand leaf child uuid: ${it.uuid}") }
        println("firstLeaf curHeight: " + firstLeaf.getCurrentHeight())

        val updRandLeaf = firstLeaf.getLeafList()[randLeafIdx].addLeaf(secondLeaf)
        println("upd rand leaf uuid: ${updRandLeaf.uuid}")
        println("upd rand leaf parent uuid: ${updRandLeaf.getParentLeaf()?.uuid}")

        println("updated first leaf size : " + firstLeaf.getLeafList().size)
        println("upd firstLeaf curHeight: " + firstLeaf.getCurrentHeight())
        for(listLeaf in firstLeaf.getLeafList()) {
            println("upd firstLeaf: $listLeaf")
        }

        updRandLeaf.getChildrenLeavesList()?.forEach { println("upd rand leaf child uuid: ${it.uuid}") }
    }

    @Test
    fun testLeafCoords() {
        val startingPoint = Point(512.0, 954.0)

        val firstLeaf = Leaf(initHeight = 2, position = startingPoint, angleFromParent = Angle.fromDegrees(0))

        val secondLeaf = Leaf(initHeight = 2, position = startingPoint, angleFromParent = Angle.fromDegrees(90))

        val thirdLeaf = Leaf(initHeight = 2, position = startingPoint, angleFromParent = Angle.fromDegrees(180))

        val fourthLeaf = Leaf(initHeight = 2, position = startingPoint, angleFromParent = Angle.fromDegrees(270))

        println(firstLeaf)

        firstLeaf.getLeafList().forEach { leaf -> println("firstLeaf: $leaf") }
        secondLeaf.getLeafList().forEach { leaf -> println("secondLeaf: $leaf") }
        thirdLeaf.getLeafList().forEach { leaf -> println("thirdLeaf: $leaf") }
        fourthLeaf.getLeafList().forEach { leaf -> println("fourthLeaf: $leaf") }

        firstLeaf.getLeafLineList().forEach { leafLine -> println("leafLine: $leafLine") }

    }
}