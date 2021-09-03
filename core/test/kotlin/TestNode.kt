
import org.junit.jupiter.api.Test
import org.river.exertion.Point
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.nodeLinks
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.nodeMesh
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.nodes
import org.river.exertion.koboldCave.leaf.Leaf
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.NodeLink

class TestNode {

    @ExperimentalUnsignedTypes
    @Test
    fun testNodeList() {
        val leaf = Leaf(topHeight = 5)
        val nodeList = mutableListOf<Node>()

        println("leafList(${leaf.getList().size}):")
        for (listLeaf in leaf.getList()) {
            println(listLeaf)
            nodeList.add(Node(listLeaf))
        }

        println("nodeList(${nodeList.size}):")
        for (listNode in nodeList) {
            println(listNode)
        }
    }

    @ExperimentalUnsignedTypes
    @Test
    fun testNodeLinkList() {
        val leaf = Leaf(topHeight = 3)
        val nodeLinkList = mutableListOf<NodeLink>()

        println("leafList(${leaf.getList().size}):")
        for (listLeaf in leaf.getList()) {
            println(listLeaf)
            nodeLinkList.addAll(listLeaf.nodeLinks(listLeaf.nodes()))
        }

        println("nodeLinkList(${nodeLinkList.size}):")
        for (nodeLink in nodeLinkList) {
            println(nodeLink)
        }
    }


    @ExperimentalUnsignedTypes
    @Test
    fun testNodeConsolidateLink() {
        val startingPoint = Point(512F, 954F)

        val leafFirst = Leaf(topHeight = 3, position = startingPoint)
        val leafSecond = Leaf(topHeight = 3, position = startingPoint)
        val leafThird = Leaf(topHeight = 3, position = startingPoint)

        println("leafNodeListFirst(" + leafFirst.getList().size + "): ")
        for (leafNode in leafFirst.getList())
            println(leafNode)
        println("leafNodeListSecond(" + leafSecond.getList().size + "): ")
        for (leafNode in leafSecond.getList())
            println(leafNode)
        println("leafNodeListThird(" + leafThird.getList().size + "): ")
        for (leafNode in leafThird.getList())
            println(leafNode)

        val nodeMesh = leafFirst.nodeMesh() + leafSecond.nodeMesh() + leafThird.nodeMesh()
        println("nodeMesh(" + nodeMesh.nodes.size + "):")
        nodeMesh.nodes.forEach { println ("node: $it")}
        nodeMesh.nodeLinks.forEach { println ("nodeLink: $it")}

        nodeMesh.consolidateNearNodes()

        println("consolidated nodeMesh(" + nodeMesh.nodes.size + "):")
        nodeMesh.nodes.forEach { println ("node: $it")}
        nodeMesh.nodeLinks.forEach { println ("nodeLink: $it")}

        nodeMesh.linkNearNodes()

        println("linkNodes nodeMesh(" + nodeMesh.nodes.size + "):")
        nodeMesh.nodes.forEach { println ("node: $it")}
        nodeMesh.nodeLinks.forEach { println ("nodeLink: $it")}

    }


}