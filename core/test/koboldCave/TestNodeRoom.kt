package koboldCave

import org.junit.jupiter.api.Test
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.nodeMesh
import org.river.exertion.koboldCave.leaf.Leaf
import org.river.exertion.koboldCave.node.nodeMesh.INodeMesh.Companion.clusterMesh

class TestNodeRoom {

    @ExperimentalUnsignedTypes
    @Test
    fun testNodeCluster() {
        val leaf = Leaf(topHeight = 3)
        println("leaf(" +leaf.children.count() + "): " + leaf)

        println("leafList(" + leaf.getList().size + "): ")
        for (listLeaf in leaf.getList())
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