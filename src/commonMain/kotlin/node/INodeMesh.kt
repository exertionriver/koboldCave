package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import node.Node.Companion.cluster
import node.Node.Companion.consolidateNearNodes
import node.Node.Companion.consolidateStackedNodes
import node.Node.Companion.getNodeLineList
import node.Node.Companion.linkNodes

@ExperimentalUnsignedTypes
interface INodeMesh {

    val uuid : UUID

    val nodes : MutableList<Node>

    var nodeLinks : MutableList<NodeLink>

    fun linkNodes(linkOrphans : Boolean = true) { nodeLinks = nodes.linkNodes(linkOrphans) }

    fun consolidateNearNodes() { nodeLinks = nodes.consolidateNearNodes(nodeLinks) }

    fun consolidateStackedNodes() { nodeLinks = nodes.consolidateStackedNodes(nodeLinks) }

    fun getClusters(rooms : Int = 4, maxIterations : Int = 4) : Map<Node, MutableList<Node>> = nodes.cluster(rooms, maxIterations)

    fun getNodeLineList() : List<Pair<Point, Point>?> = nodes.getNodeLineList(nodeLinks)

}