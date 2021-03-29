import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import node.NodeMesh
import kotlin.test.Test

class TestNodeRoom {

    @ExperimentalUnsignedTypes
    @Test
    fun testNodeCluster() {
        val leaf = Leaf(initHeight = 3)
        println("leaf(" +leaf.childrenLeaves.count() + "): " + leaf)

        println("leafList(" + leaf.getLeafList().size + "): ")
        for (listLeaf in leaf.getLeafList())
            println(listLeaf)

        val nodeMesh = leaf.getLeafList().nodeMesh()
        println("nodeMesh(${nodeMesh.nodes.size})")
        for (node in nodeMesh.nodes)
            println(node)
        for (nodeLink in nodeMesh.nodeLinks)
            println(nodeLink)

        val rooms = 3

        println("nodeRooms(${rooms}): ${nodeMesh.getClusters(rooms = 3, maxIterations = 3)}")

    }

}