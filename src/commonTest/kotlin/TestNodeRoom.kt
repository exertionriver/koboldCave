import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import kotlin.test.Test

class TestNodeRoom {

    @ExperimentalUnsignedTypes
    @Test
    fun testNodeCluster() {
        val leaf = Leaf(topHeight = 3)
        println("leaf(" +leaf.children.count() + "): " + leaf)

        println("leafList(" + leaf.getList().size + "): ")
        for (listLeaf in leaf.getList())
            println(listLeaf)

        val nodeMesh = leaf.getList().nodeMesh()
        println("nodeMesh(${nodeMesh.nodes.size})")
        for (node in nodeMesh.nodes)
            println(node)
        for (nodeLink in nodeMesh.nodeLinks)
            println(nodeLink)

        val rooms = 3

        println("nodeRooms(${rooms}): ${nodeMesh.getClusters(rooms = 3, maxIterations = 3)}")

    }

}