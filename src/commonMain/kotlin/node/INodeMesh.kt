package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import node.Node.Companion.addNodes
import node.Node.Companion.buildNodePaths
import node.Node.Companion.cluster
import node.Node.Companion.consolidateNearNodes
import node.Node.Companion.consolidateStackedNodes
import node.Node.Companion.getFarthestNode
import node.Node.Companion.getNodeLineList
import node.Node.Companion.linkNearNodes
import node.Node.Companion.nearestNodesOrderedAsc
import node.NodeLink.Companion.addNodeLinks
import node.NodeLink.Companion.buildNodeLinkLines
import node.NodeLink.Companion.getNodeLinks

@ExperimentalUnsignedTypes
interface INodeMesh {

    val uuid : UUID

    val description : String

    var nodes : MutableList<Node>

    var nodeLinks : MutableList<NodeLink>

    fun linkNearNodes(linkOrphans : Boolean = true) { nodeLinks = nodes.linkNearNodes(linkOrphans) }

    fun consolidateNearNodes() { nodeLinks = nodes.consolidateNearNodes(nodeLinks) }

    fun consolidateStackedNodes() { nodeLinks = nodes.consolidateStackedNodes(nodeLinks) }

    fun getClusters(rooms : Int = 4, maxIterations : Int = 4) : Map<Node, MutableList<Node>> = nodes.cluster(rooms, maxIterations)

    fun getFarthestNode(refNode : Node) = nodes.getFarthestNode(refNode)

    fun getNodeLineList() : List<Pair<Point, Point>?> = nodes.getNodeLineList(nodeLinks)

    fun buildNodeLinkLines(noise : Int = 0) { this.addMesh( nodeLinks.buildNodeLinkLines(nodes, noise) ) }

    companion object {

        fun INodeMesh.addMesh(nodeMeshToAdd : INodeMesh) {
            this.nodes.addNodes(nodeMeshToAdd.nodes)
            this.nodeLinks.addNodeLinks(nodeMeshToAdd.nodeLinks)

            this.nodes = this.nodes.distinct().toMutableList()
            this.consolidateStackedNodes()
        }

        fun INodeMesh.replaceMesh(nodeMeshToAdd : INodeMesh) {
            this.nodes = nodeMeshToAdd.nodes
            this.nodeLinks = nodeMeshToAdd.nodeLinks

            this.nodes = this.nodes.distinct().toMutableList()
            this.consolidateStackedNodes()
        }

        fun INodeMesh.absorbMesh(centerNode : Node, radius : Double, nodeMeshToAbsorb : INodeMesh) {

            val nodesWithinRange = nodeMeshToAbsorb.nodes.filter {node -> Point.distance(node.position, centerNode.position) <= radius }.toMutableList()
            val nodeLinksWithinRange = nodeMeshToAbsorb.nodeLinks.getNodeLinks(nodesWithinRange.map { it.uuid } )

            this.addMesh(nodeMeshToAdd = NodeMesh(nodes= nodesWithinRange, nodeLinks = nodeLinksWithinRange) )
            this.consolidateStackedNodes()
        }

    }
}