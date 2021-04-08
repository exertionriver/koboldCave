import com.soywiz.korma.geom.Point
import leaf.ILeaf.Companion.nodeLinks
import leaf.ILeaf.Companion.nodeMesh
import leaf.ILeaf.Companion.nodes
import leaf.Leaf
import node.Node
import node.NodeLink
import kotlin.test.Test

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
        val startingPoint = Point(512.0, 954.0)

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

        val nodeMesh = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList()).nodeMesh()
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