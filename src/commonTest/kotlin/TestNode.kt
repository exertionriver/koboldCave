import node.NodeMesh
import com.soywiz.korma.geom.Point
import leaf.Leaf
import kotlin.test.Test

class TestNode {

    @ExperimentalUnsignedTypes
    @Test
    fun testNodeConsolidate() {
        val leaf = Leaf(initHeight = 5)
//        println("leaf(" +leaf.childrenLeafNodes.count() + "): " + leaf)
/*
        println("leafNodeList(" + leaf.getLeafList().size + "): ")
        for (leafNode in leaf.getLeafList())
            println(leafNode)

        val nodeMesh = NodeMesh(leafNodes = leaf.getNodeList())
        println("nodeMesh(" + nodeMesh.leafNodes.size + "): " + nodeMesh)

        println("nodeMeshConsolidated(" + nodeMesh.getConsolidatedLeafNodes().size + "): " + nodeMesh.getConsolidatedLeafNodes())
*/
    }

    @ExperimentalUnsignedTypes
    @Test
    fun testNodeMultiConsolidate() {
        val startingPoint = Point(512.0, 954.0)

        val leafFirst = Leaf(initHeight = 5, position = startingPoint)
        val leafSecond = Leaf(initHeight = 5, position = startingPoint)
        val leafThird = Leaf(initHeight = 5, position = startingPoint)

        println("leafNodeListFirst(" + leafFirst.getLeafList().size + "): ")
        for (leafNode in leafFirst.getLeafList())
            println(leafNode)
        println("leafNodeListSecond(" + leafSecond.getLeafList().size + "): ")
        for (leafNode in leafSecond.getLeafList())
            println(leafNode)
        println("leafNodeListThird(" + leafThird.getLeafList().size + "): ")
        for (leafNode in leafThird.getLeafList())
            println(leafNode)
/*
        val nodeMesh = NodeMesh(leafNodes = leafFirst.getNodeList().plus(leafSecond.getNodeList()).plus(leafThird.getNodeList()))
        println("nodeMesh(" + nodeMesh.leafNodes.size + "): " + nodeMesh)

        println("nodeMeshConsolidated(" + nodeMesh.getConsolidatedLeafNodes().size + "): " + nodeMesh.getConsolidatedLeafNodes())
*/
    }


}