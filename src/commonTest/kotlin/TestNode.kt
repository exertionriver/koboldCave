import com.soywiz.korma.geom.Point
import kotlin.test.Test

class TestNode {

    @ExperimentalUnsignedTypes
    @Test
    fun testNodeConsolidate() {
        val leaf = OakLeaf(initHeight = 5)
//        println("leaf(" +leaf.childrenLeafNodes.count() + "): " + leaf)

        println("leafNodeList(" + leaf.getLeafNodeList().size + "): ")
        for (leafNode in leaf.getLeafNodeList())
            println(leafNode)

        val nodeMesh = NodeMesh(oakLeafNodes = leaf.getNodeList())
        println("nodeMesh(" + nodeMesh.oakLeafNodes.size + "): " + nodeMesh)

        println("nodeMeshConsolidated(" + nodeMesh.getConsolidatedOakLeafNodes().size + "): " + nodeMesh.getConsolidatedOakLeafNodes())

    }

    @ExperimentalUnsignedTypes
    @Test
    fun testNodeMultiConsolidate() {
        val startingPoint = Point(512.0, 954.0)

        val leafFirst = OakLeaf(initHeight = 5, startingPosition = startingPoint)
        val leafSecond = OakLeaf(initHeight = 5, startingPosition = startingPoint)
        val leafThird = OakLeaf(initHeight = 5, startingPosition = startingPoint)

        println("leafNodeListFirst(" + leafFirst.getLeafNodeList().size + "): ")
        for (leafNode in leafFirst.getLeafNodeList())
            println(leafNode)
        println("leafNodeListSecond(" + leafSecond.getLeafNodeList().size + "): ")
        for (leafNode in leafSecond.getLeafNodeList())
            println(leafNode)
        println("leafNodeListThird(" + leafThird.getLeafNodeList().size + "): ")
        for (leafNode in leafThird.getLeafNodeList())
            println(leafNode)

        val nodeMesh = NodeMesh(oakLeafNodes = leafFirst.getNodeList().plus(leafSecond.getNodeList()).plus(leafThird.getNodeList()))
        println("nodeMesh(" + nodeMesh.oakLeafNodes.size + "): " + nodeMesh)

        println("nodeMeshConsolidated(" + nodeMesh.getConsolidatedOakLeafNodes().size + "): " + nodeMesh.getConsolidatedOakLeafNodes())

    }


}