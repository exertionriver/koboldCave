import node.NodeMesh
import com.soywiz.korma.geom.Point
import leaf.ILeaf.Companion.nodeLinks
import leaf.Leaf
import node.Node
import node.NodeLink
import kotlin.test.Test

class TestNode {

    @ExperimentalUnsignedTypes
    @Test
    fun testNodeList() {
        val leaf = Leaf(initHeight = 5)
        val nodeList = mutableListOf<Node>()

        println("leafList(${leaf.getLeafList().size}):")
        for (listLeaf in leaf.getLeafList()) {
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
        val leaf = Leaf(initHeight = 3)
        val nodeLinkList = mutableListOf<NodeLink>()

        println("leafList(${leaf.getLeafList().size}):")
        for (listLeaf in leaf.getLeafList()) {
            println(listLeaf)
            nodeLinkList.addAll(listLeaf.nodeLinks())
        }

        println("nodeLinkList(${nodeLinkList.size}):")
        for (nodeLink in nodeLinkList) {
            println(nodeLink)
        }
    }


    @ExperimentalUnsignedTypes
    @Test
    fun testNodeMultiConsolidate() {
        val startingPoint = Point(512.0, 954.0)

        val leafFirst = Leaf(initHeight = 3, position = startingPoint)
        val leafSecond = Leaf(initHeight = 3, position = startingPoint)
        val leafThird = Leaf(initHeight = 3, position = startingPoint)

        println("leafNodeListFirst(" + leafFirst.getLeafList().size + "): ")
        for (leafNode in leafFirst.getLeafList())
            println(leafNode)
        println("leafNodeListSecond(" + leafSecond.getLeafList().size + "): ")
        for (leafNode in leafSecond.getLeafList())
            println(leafNode)
        println("leafNodeListThird(" + leafThird.getLeafList().size + "): ")
        for (leafNode in leafThird.getLeafList())
            println(leafNode)

        val nodeMesh = NodeMesh(leafFirst.getLeafList().plus(leafSecond.getLeafList()).plus(leafThird.getLeafList()))
        println("nodeMesh(" + nodeMesh.nodes.size + "):")
        nodeMesh.nodes.forEach { println ("node: $it")}
        nodeMesh.nodeLinks.forEach { println ("nodeLink: $it")}

        nodeMesh.consolidateNodes()

        println("consolidated nodeMesh(" + nodeMesh.nodes.size + "):")
        nodeMesh.nodes.forEach { println ("node: $it")}
        nodeMesh.nodeLinks.forEach { println ("nodeLink: $it")}

        nodeMesh.linkNodes()

        println("linkNodes nodeMesh(" + nodeMesh.nodes.size + "):")
        nodeMesh.nodes.forEach { println ("node: $it")}
        nodeMesh.nodeLinks.forEach { println ("nodeLink: $it")}

    }


}