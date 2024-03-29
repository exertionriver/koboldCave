package geom

import org.junit.jupiter.api.Test
import org.river.exertion.geom.leaf.ILeaf.Companion.nodeMesh
import org.river.exertion.geom.leaf.Leaf
import org.river.exertion.geom.node.nodeMesh.INodeMesh.Companion.clusterMesh

class TestNodeRoom {

    @ExperimentalUnsignedTypes
    @Test
    fun testNodeCluster() {
        val leaf = Leaf(topHeight = 3)
        println("leaf(" +leaf.children.count() + "): " + leaf)

        println("leafList(" + leaf.getSet().size + "): ")
        for (listLeaf in leaf.getSet())
            println(listLeaf)

        val nodeMesh = leaf.nodeMesh()
        println("nodeMesh(${nodeMesh.nodes.size})")
        for (node in nodeMesh.nodes)
            println(node)
        for (nodeLink in nodeMesh.nodeLinks)
            println(nodeLink)

        val rooms = 3

        println("nodeRooms(${rooms}): ${nodeMesh.clusterMesh()}")

    }

}