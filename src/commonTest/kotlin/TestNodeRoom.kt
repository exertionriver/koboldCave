import leaf.Leaf
import node.NodeMesh
import kotlin.test.Test

class TestNodeRoom {

    @ExperimentalUnsignedTypes
    @Test
    fun testNodeCluster() {
        val leaf = Leaf(initHeight = 3)
//        println("leaf(" +leaf.childrenLeafNodes.count() + "): " + leaf)
/*
        println("leafNodeList(" + leaf.getLeafNodeList().size + "): ")
        for (leafNode in leaf.getLeafNodeList())
            println(leafNode)

        val nodeMesh = NodeMesh(leafNodes = leaf.getNodeList())
        println("nodeMesh(${nodeMesh.leafNodes.size})")//: $nodeMesh")

        val rooms = 3

        println("nodeRooms(${rooms}): ${nodeMesh.getClusteredNodes(rooms = 3, maxIterations = 3)}")
*/
    }

}