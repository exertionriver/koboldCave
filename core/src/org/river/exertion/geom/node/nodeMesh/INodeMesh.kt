package org.river.exertion.geom.node.nodeMesh

import org.river.exertion.geom.Line.Companion.intersectsBorder
import org.river.exertion.geom.Line.Companion.isInBorder
import org.river.exertion.geom.node.Node.Companion.consolidateNearNodes
import org.river.exertion.geom.node.Node.Companion.consolidateStackedNodes
import org.river.exertion.geom.node.Node.Companion.getFarthestNode
import org.river.exertion.geom.node.Node.Companion.getNode
import org.river.exertion.geom.node.Node.Companion.getRandomNode
import org.river.exertion.geom.node.Node.Companion.linkNearNodes
import org.river.exertion.geom.node.Node.Companion.processOrphans
import org.river.exertion.geom.node.NodeLink.Companion.consolidateNodeLinks
import org.river.exertion.geom.node.NodeLink.Companion.getNextAngle
import org.river.exertion.geom.node.NodeLink.Companion.getNextNodeAngle
import org.river.exertion.geom.node.NodeLink.Companion.getNodeLinks
import org.river.exertion.geom.node.NodeLink.Companion.removeOrphanLinks
import org.river.exertion.Angle
import org.river.exertion.NextDistancePx
import org.river.exertion.geom.Line
import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.Node.Companion.averagePositionWithinNodes
import org.river.exertion.geom.node.Node.Companion.bridgeSegments
import org.river.exertion.geom.node.Node.Companion.getLineSet
import org.river.exertion.geom.node.Node.Companion.nearestNodesOrderedAsc
import org.river.exertion.geom.node.Node.Companion.randomPosition
import org.river.exertion.geom.node.NodeLink
import org.river.exertion.geom.node.NodeLink.Companion.getLineSet
import org.river.exertion.geom.node.NodeLink.Companion.getRandomNextNodeLinkAngle
import org.river.exertion.geom.node.NodeLink.Companion.nodifyIntersects
import java.util.*

interface INodeMesh {

    val uuid : UUID

    val description : String

    var nodes : MutableSet<Node>

    var nodeLinks : MutableSet<NodeLink>

    //only used for additions
    fun consolidateStackedNodes() { nodeLinks = nodes.consolidateStackedNodes(nodeLinks) }

    fun consolidateNearNodes() { nodeLinks = nodes.consolidateNearNodes(nodeLinks) }

    fun linkNearNodes(linkOrphans : Boolean = true) { nodeLinks = nodes.linkNearNodes(nodeLinks, linkOrphans = linkOrphans) }

    fun nodifyIntersects() { nodes = nodeLinks.nodifyIntersects(nodes) }

    fun consolidateNodeLinks() { nodeLinks = nodeLinks.consolidateNodeLinks(nodes) }

    fun removeOrphans() { nodes = nodes.processOrphans(nodeLinks) ; nodeLinks = nodeLinks.removeOrphanLinks(nodes) }

    fun bridgeSegments() { val bridgeMesh = nodes.bridgeSegments(nodeLinks); nodes.addAll(bridgeMesh.nodes) ; nodeLinks.addAll(bridgeMesh.nodeLinks) }

    fun getRandomNode() = nodes.getRandomNode()

    fun getRandomNextNodeLinkAngle(node : Node) = nodeLinks.getRandomNextNodeLinkAngle(nodes, node)

    fun getFarthestNode(refNode : Node) = nodes.getFarthestNode(refNode)

    fun getNextNodeAngle(refNode : Node, refAngle : Angle) : Pair<Node, Angle> = nodeLinks.getNextNodeAngle(nodes, refNode, refAngle)

    fun getNextAngle(refNode : Node, refAngle : Angle, nextAngle : NodeLink.NextAngle) : Angle = nodeLinks.getNextAngle(nodes, refNode, refAngle, nextAngle)

    fun getLineSet() : MutableSet<Line> = nodes.getLineSet(nodeLinks)

    companion object {

        fun INodeMesh.processMesh() {
            this.consolidateNearNodes()
            this.linkNearNodes()
            this.nodifyIntersects()
            this.consolidateNearNodes()
            this.consolidateNodeLinks()
            this.nodifyIntersects()
            this.removeOrphans()
            this.bridgeSegments()
            this.consolidateNearNodes()
        }

        fun INodeMesh.setBordering(nodeMeshToBorder : INodeMesh, orthoBorderDistance : Double = NextDistancePx * 0.3, refNode : Node = Node(position = nodeMeshToBorder.nodes.averagePositionWithinNodes())) : INodeMesh {

            //get node mesh to border's nearest node to the refNode
            val borderingNodes = this.nodes.nearestNodesOrderedAsc(refNode)
            val nodesToRemove = mutableSetOf<Node>()
            val nodeLinksToRemove = mutableSetOf<NodeLink>()

            val refMeshNodeLines = nodeMeshToBorder.nodeLinks.getLineSet(nodeMeshToBorder.nodes)

//            val thisNodes = this.nodes
//            val iterNodes = mutableListOf<Node>().apply { addAll(thisNodes) }

            borderingNodes.forEach { borderingNode ->

                if (nodesToRemove.contains(borderingNode)) {
                    val borderingNodeLinks = this.nodeLinks.getNodeLinks(borderingNode.uuid)

                    nodeLinksToRemove.addAll(borderingNodeLinks)
                } else {
                    //get nodelinks associated with this node
                    val borderingNodeLinks = this.nodeLinks.getNodeLinks(borderingNode.uuid)

                    //check each line related to the closest node
                    refMeshNodeLines.forEach { refMeshNodeLine ->
                        //check if this node falls within the borders of any line related to closest ref node
//                    println("node within border? node:$node, refLine:$closestRefNodeLine")
                        if (borderingNode.position.isInBorder(refMeshNodeLine, orthoBorderDistance.toInt())) {
                            //if node in border, remove from result nodeMesh
                            nodesToRemove.add(borderingNode)
//                        this.nodes.removeNode(this.nodeLinks, node.uuid)
                            //                    println("node within border! node:$node, refLine:$closestRefNodeLine")
                        }

                        //for each of these links,
                        borderingNodeLinks.forEach { borderingNodeLink ->

                            //get the first and second nodes
                            val firstNode = this.nodes.getNode(borderingNodeLink.firstNodeUuid)
                            val secondNode = this.nodes.getNode(borderingNodeLink.secondNodeUuid)

                            //if these nodes are not null
                            if ( (firstNode != null) && (secondNode != null) ) {
                                //check if the nodeLink intersects with borderline
//                              println("intersects? nodeLink:(${firstNode.position}, ${secondNode.position}), refLine:$closestRefNodeLine")
                                if (nodesToRemove.contains(firstNode) || nodesToRemove.contains(secondNode)) {
                                    if (borderingNodes.indexOf(firstNode) > borderingNodes.indexOf(secondNode)) nodesToRemove.add(firstNode) else nodesToRemove.add(secondNode)
                                    nodeLinksToRemove.add(borderingNodeLink)
                                } else if ( Line(firstNode.position, secondNode.position).intersectsBorder(refMeshNodeLine, orthoBorderDistance.toInt()) ) {
                                    //if nodeLink intersects border, remove from result nodeMesh
                                    if (borderingNodes.indexOf(firstNode) > borderingNodes.indexOf(secondNode)) nodesToRemove.add(firstNode) else nodesToRemove.add(secondNode)
                                    nodeLinksToRemove.add(borderingNodeLink)
//                                this.nodeLinks.removeNodeLink(borderingLeafNodeLink)
//                                    println("intersection! nodeLink:(${firstNode.position}, ${secondNode.position}), refLine:$closestRefNodeLine")
                                }
                            }
                        }
                    }
                }
            }
            this.nodes.removeAll(nodesToRemove)
            this.nodeLinks.removeAll(nodeLinksToRemove)
            this.removeOrphans()
            return this
        }

        //returns a map of centroids and their NodeRooms
        fun INodeMesh.clusterMesh(centroids : MutableSet<Node> = mutableSetOf()) : MutableMap<Node, NodeRoom> {

            val rooms = if (centroids.isEmpty()) (this.nodes.size / 16) else centroids.size
            val maxIterations = rooms

//            println("rooms: $rooms node.size: ${this.nodes.size} $centroids")

//            if (rooms <= 1) return mutableMapOf(Node(position = this.nodes.averagePositionWithinNodes()) to NodeRoomMesh(this))

            val returnCentroids = if (centroids.size > 0)
                MutableList(size = centroids.size) { idx -> Node(position = centroids.toList()[idx].position, description = "Room$idx" ) }
            else
                MutableList(size = rooms) { idx -> Node(position = this.nodes.randomPosition(), description = "Room$idx" ) }

//            returnCentroids.forEachIndexed { idx, it -> println ("centroid $idx: $it") }

            val nodeClusters = mutableMapOf<Node, NodeRoom>()

//        println("init nodeRooms: $nodeRooms")
            (0 until rooms).forEach {
                nodeClusters[returnCentroids[it]] = NodeRoom()
            }

            (0 until maxIterations).forEach { iteration ->
                val isLastIteration: Boolean = iteration == maxIterations - 1

                returnCentroids.forEach { returnCentroid -> nodeClusters[returnCentroid]!!.nodes.clear() }

                this.nodes.forEach { node ->
                    val nearestNodeDescription = node.nearestCentroid(returnCentroids.toMutableSet()).description

                    nodeClusters[node.nearestCentroid(returnCentroids.toMutableSet())]!!.nodes.add(Node (node, updDescription = node.description + "." + nearestNodeDescription))
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