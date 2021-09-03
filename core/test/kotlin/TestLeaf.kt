import org.junit.jupiter.api.Test
import org.river.exertion.Point
import org.river.exertion.koboldCave.leaf.Leaf
import java.time.LocalDateTime
import kotlin.random.Random


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
        Random(LocalDateTime.now().nano)

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
        println("rand leaf parent uuid: ${randLeaf.parent}")

        randLeaf.getChildrenList()?.forEach { println("rand leaf child uuid: ${it.uuid}") }
    }

    @Test
    fun testLeafCoords() {
        val startingPoint = Point(512f, 954f)

        val firstLeaf = Leaf(topHeight = 2, position = startingPoint, topAngle = 0f)

        val secondLeaf = Leaf(topHeight = 2, position = startingPoint, topAngle = 90f)

        val thirdLeaf = Leaf(topHeight = 2, position = startingPoint, topAngle = 180f)

        val fourthLeaf = Leaf(topHeight = 2, position = startingPoint, topAngle = 270f)

        println(firstLeaf)

        firstLeaf.getList().forEach { leaf -> println("firstLeaf: $leaf") }
        secondLeaf.getList().forEach { leaf -> println("secondLeaf: $leaf") }
        thirdLeaf.getList().forEach { leaf -> println("thirdLeaf: $leaf") }
        fourthLeaf.getList().forEach { leaf -> println("fourthLeaf: $leaf") }

        firstLeaf.getLineList().forEach { leafLine -> println("leafLine: $leafLine") }

    }

}