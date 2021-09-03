package org.river.exertion.koboldCave.node.nodeMesh

import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx
import org.river.exertion.koboldCave.Line.Companion.intersectsBorder
import org.river.exertion.koboldCave.Line.Companion.isInBorder
import org.river.exertion.koboldCave.node.Node.Companion.consolidateNearNodes
import org.river.exertion.koboldCave.node.Node.Companion.consolidateStackedNodes
import org.river.exertion.koboldCave.node.Node.Companion.getFarthestNode
import org.river.exertion.koboldCave.node.Node.Companion.getNode
import org.river.exertion.koboldCave.node.Node.Companion.getLineList
import org.river.exertion.koboldCave.node.Node.Companion.getRandomNode
import org.river.exertion.koboldCave.node.Node.Companion.linkNearNodes
import org.river.exertion.koboldCave.node.Node.Companion.removeNode
import org.river.exertion.koboldCave.node.Node.Companion.removeOrphans
import org.river.exertion.koboldCave.node.NodeLink.Companion.consolidateNodeLinks
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextNodeAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getLineList
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNodeLinks
import org.river.exertion.koboldCave.node.NodeLink.Companion.getRandomNextNodeAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.removeNodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.removeOrphanLinks
import org.river.exertion.Angle
import org.river.exertion.koboldCave.Line
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.Node.Companion.averagePositionWithinNodes
import org.river.exertion.koboldCave.node.Node.Companion.randomPosition
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.nodifyIntersects
import java.util.*

@ExperimentalUnsignedTypes
interface INodeMesh {

    val uuid : UUID

    val description : String

    var nodes : MutableList<Node>

    var nodeLinks : MutableList<NodeLink>

    //only used for additions
    fun consolidateStackedNodes() { nodeLinks = nodes.consolidateStackedNodes(nodeLinks) }

    fun consolidateNearNodes() { nodeLinks = nodes.consolidateNearNodes(nodeLinks) }

    fun linkNearNodes(linkOrphans : Boolean = true) { nodeLinks = nodes.linkNearNodes(nodeLinks, linkOrphans = linkOrphans) }

    fun nodifyIntersects() { nodes = nodeLinks.nodifyIntersects(nodes) }

    fun consolidateNodeLinks() { nodeLinks = nodeLinks.consolidateNodeLinks(nodes) }

    fun removeOrphans() { nodes = nodes.removeOrphans(nodeLinks, minPercent = 0.4); nodeLinks = nodeLinks.removeOrphanLinks(nodes) }

    fun getRandomNode() = nodes.getRandomNode()

    fun getRandomNextNodeAngle(node : Node) = nodeLinks.getRandomNextNodeAngle(nodes, node)

    fun getFarthestNode(refNode : Node) = nodes.getFarthestNode(refNode)

    fun getNextNodeAngle(refNode : Node, refAngle : Angle) : Pair<Node, Angle> = nodeLinks.getNextNodeAngle(nodes, refNode, refAngle)

    fun getNextAngle(refNode : Node, refAngle : Angle, rangeAngle : Angle) : Angle = nodeLinks.getNextAngle(nodes, refNode, refAngle, rangeAngle)

    fun getLineList() : List<Line> = nodes.getLineList(nodeLinks)

    companion object {

        fun INodeMesh.processMesh() {
            this.consolidateNearNodes()
            this.linkNearNodes()
            this.nodifyIntersects()
            this.consolidateNearNodes()
            this.consolidateNodeLinks()
            this.nodifyIntersects()
            this.removeOrphans()
        }

        fun INodeMesh.setBordering(nodeMeshToBorder : INodeMesh, orthoBorderDistance : Double = NextDistancePx * 0.2) : INodeMesh {

//            val refMeshNodeLinks = nodeMeshToBorder.nodeLinks
            val refMeshNodeLines = nodeMeshToBorder.nodeLinks.getLineList(nodeMeshToBorder.nodes).filterNotNull()

            val thisNodes = this.nodes
            val iterNodes = mutableListOf<Node>().apply { addAll(thisNodes) }

            iterNodes.forEach { node ->
                //get nodelinks associated with this node
                val borderingLeafNodeLinks = this.nodeLinks.getNodeLinks(node.uuid)

                //check each line related to the closest node
                refMeshNodeLines.forEach { refMeshNodeLine ->
                    //check if this node falls within the borders of any line related to closest ref node
//                    println("node within border? node:$node, refLine:$closestRefNodeLine")
                    if (node.position.isInBorder(refMeshNodeLine, orthoBorderDistance.toInt())) {
                        //if node in border, remove from result nodeMesh
                        this.nodes.removeNode(this.nodeLinks, node.uuid)
                        //                    println("node within border! node:$node, refLine:$closestRefNodeLine")
                    }

                    //for each of these links,
                    borderingLeafNodeLinks.forEach { borderingLeafNodeLink ->

                        //get the first and second nodes
                        val firstNode = this.nodes.getNode(borderingLeafNodeLink.firstNodeUuid)
                        val secondNode = this.nodes.getNode(borderingLeafNodeLink.secondNodeUuid)

                        //if these nodes are not null
                        if ( (firstNode != null) && (secondNode != null) ) {
                            //check if the nodeLink intersects with borderline
//                              println("intersects? nodeLink:(${firstNode.position}, ${secondNode.position}), refLine:$closestRefNodeLine")
                            if ( Line(firstNode.position, secondNode.position)
                                    .intersectsBorder(refMeshNodeLine, orthoBorderDistance.toInt()) ) {
                                //if nodeLink intersects border, remove from result nodeMesh
                                this.nodeLinks.removeNodeLink(borderingLeafNodeLink)
//                                    println("intersection! nodeLink:(${firstNode.position}, ${secondNode.position}), refLine:$closestRefNodeLine")
                            }
                        }
                    }
                }
            }
            return this
        }

        //returns a map of centroids and their NodeRooms
        fun INodeMesh.clusterMesh(centroids : MutableList<Node> = mutableListOf()) : MutableMap<Node, NodeRoom> {

            val rooms = if (centroids.isEmpty()) (this.nodes.size / 16) else centroids.size
            val maxIterations = rooms

//            println("rooms: $rooms node.size: ${this.nodes.size} $centroids")

//            if (rooms <= 1) return mutableMapOf(Node(position = this.nodes.averagePositionWithinNodes()) to NodeRoomMesh(this))

            val returnCentroids = if (centroids.size > 0)
                MutableList(size = centroids.size) { idx -> Node(position = centroids[idx].position, description = "Room$idx" ) }
            else
                MutableList(size = rooms) { idx -> Node(position = this.nodes.randomPosition(), description = "Room$idx" ) }

//            returnCentroids.forEachIndexed { idx, it -> println ("centroid $idx: $it") }

            val nodeClusters = mutableMapOf<Node, NodeRoom>()

//        println("init nodeRooms: $nodeRooms")
            (0 until rooms).toList().forEach {
                nodeClusters[returnCentroids[it]] = NodeRoom()
            }

            (0 until maxIterations).toList().forEach { iteration ->
                val isLastIteration: Boolean = iteration == maxIterations - 1

                returnCentroids.forEach { returnCentroid -> nodeClusters[returnCentroid]!!.nodes.clear() }

                this.nodes.forEach { node ->
                    val nearestNodeDescription = node.nearestCentroid(returnCentroids).description

                    nodeClusters[node.nearestCentroid(returnCentroids)]!!.nodes.add(Node (node, updDescription = node.description + "." + nearestNodeDescription))
//                    println ("iteration: $iteration, nearestNodeDescription: $nearestNodeDescription, node: $node")
                }

//            println("iteration $iteration:")
//            println(nodeRooms)

                if (!isLastIteration) returnCentroids.forEach { returnCentroid -> nodeClusters[returnCentroid]!!.nodes.averagePositionWithinNodes() }
            }

//            println("clusteredMesh: ${this.nodes.size}, ${returnCentroids.size}")

            return nodeClusters
        }
    }
}