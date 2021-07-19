import com.soywiz.klock.DateTime
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import leaf.ILeaf.Companion.add
import leaf.ILeaf.Companion.prune
import leaf.Leaf
import kotlin.random.Random
import kotlin.test.Test

@ExperimentalUnsignedTypes
class TestLeaf {

    @Test
    fun testLeaf() {
        val leaf = Leaf(topHeight = 3)
        println(leaf)
        println("leaf size : " + leaf.getList().size)
        for(listLeaf in leaf.getList()) {
            println(listLeaf)
        }
    }

    @Test
    fun testRandLeaf() {
        Random(DateTime.now().milliseconds)

        val leaf = Leaf(topHeight = 4)
        println(leaf)
        println("leaf size : " + leaf.getList().size)
        for(listLeaf in leaf.getList()) {
            println(listLeaf)
        }

        val randLeafIdx = Random.nextInt(leaf.getList().size)
        println("rand idx: $randLeafIdx")
        val randLeaf = leaf.getList()[randLeafIdx]
        println("rand leaf uuid: $randLeaf")
        println("rand leaf parent uuid: ${randLeaf.getParent()}")

        randLeaf.getChildrenList()?.forEach { println("rand leaf child uuid: ${it.uuid}") }
    }

    @ExperimentalUnsignedTypes
    @Test
    fun testAddRandLeaf() {
        Random(DateTime.now().milliseconds)

        val firstLeaf = Leaf(topHeight = 3)
        println("first leaf size : " + firstLeaf.getList().size)
        for(listLeaf in firstLeaf.getList()) {
            println(listLeaf)
        }

        val secondLeaf = Leaf(topHeight = 3)
        println("second leaf size : " + secondLeaf.getList().size)
        for(listLeaf in secondLeaf.getList()) {
            println(listLeaf)
        }

        val randLeafIdx = Random.nextInt(firstLeaf.getList().size)

        val randLeaf = firstLeaf.getList()[randLeafIdx]
        println("rand first idx: $randLeafIdx")
        println("rand leaf uuid: ${randLeaf.uuid}")
        println("rand leaf parent uuid: ${randLeaf.getParent()?.uuid}")

        randLeaf.getChildrenList()?.forEach { println("rand leaf child uuid: ${it.uuid}") }
        println("firstLeaf curHeight: " + firstLeaf.height)

        val updRandLeaf = firstLeaf.getList()[randLeafIdx].add(secondLeaf)
        println("upd rand leaf uuid: ${updRandLeaf.uuid}")
        println("upd rand leaf parent uuid: ${updRandLeaf.getParent()?.uuid}")

        println("updated first leaf size : " + firstLeaf.getList().size)
        println("upd firstLeaf curHeight: " + firstLeaf.height)
        for(listLeaf in firstLeaf.getList()) {
            println("upd firstLeaf: $listLeaf")
        }

        updRandLeaf.getChildrenList()?.forEach { println("upd rand leaf child uuid: ${it.uuid}") }
    }

    @Test
    fun testLeafCoords() {
        val startingPoint = Point(512.0, 954.0)

        val firstLeaf = Leaf(topHeight = 2, position = startingPoint, angleFromParent = Angle.fromDegrees(0))

        val secondLeaf = Leaf(topHeight = 2, position = startingPoint, angleFromParent = Angle.fromDegrees(90))

        val thirdLeaf = Leaf(topHeight = 2, position = startingPoint, angleFromParent = Angle.fromDegrees(180))

        val fourthLeaf = Leaf(topHeight = 2, position = startingPoint, angleFromParent = Angle.fromDegrees(270))

        println(firstLeaf)

        firstLeaf.getList().forEach { leaf -> println("firstLeaf: $leaf") }
        secondLeaf.getList().forEach { leaf -> println("secondLeaf: $leaf") }
        thirdLeaf.getList().forEach { leaf -> println("thirdLeaf: $leaf") }
        fourthLeaf.getList().forEach { leaf -> println("fourthLeaf: $leaf") }

        firstLeaf.getLineList().forEach { leafLine -> println("leafLine: $leafLine") }

    }

    @Test
    fun testPrune() {
        val startingPoint = Point(512.0, 954.0)

        val firstLeaf = Leaf(topHeight = 12, position = startingPoint, angleFromParent = Angle.fromDegrees(0))

        firstLeaf.getList().prune()
    }
}