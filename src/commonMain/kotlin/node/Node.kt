package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Angle.Companion.fromRadians
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.minus
import com.soywiz.korma.geom.plus
import leaf.ILeaf
import node.INodeMesh.Companion.addMesh
import node.Node.Companion.updateNode
import node.NodeLink.Companion.addNodeLink
import node.NodeLink.Companion.areNodesLinked
import node.NodeLink.Companion.buildNodeLinkLine
import node.NodeLink.Companion.getNodeChildrenUuids
import node.NodeLink.Companion.getNodeLinks
import node.NodeLink.Companion.linkNodeDistance
import kotlin.math.atan
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Node(val uuid: UUID = UUID.randomUUID(Random.Default), val position : Point, val description : String = "Node${Random.nextInt(2048)}") {

    constructor(leaf : ILeaf, description : String = "LeafNode${Random.nextInt(2048)}") : this (
        uuid = leaf.uuid
        , position = leaf.position
        , description = description
    )

    constructor(copyNode : Node
        , updUuid : UUID = copyNode.uuid
        , updPosition : Point = copyNode.position
        , updDescription : String = copyNode.description) : this (
        uuid = updUuid
        , position = updPosition
        , description = updDescription
    )

    constructor() : this(position = Point(0,0))

    fun nearestCentroid(centroids : List<Node>) : Node {

/*        var minimumDistance = 1024.0
        lateinit var nearestCentroid : Node

        centroids.forEach { centroid ->
            val currentDistance = position.distanceTo(centroid.position)

            if (currentDistance < minimumDistance) {
                minimumDistance = currentDistance
                nearestCentroid = centroid
            }
        }
*/

        return centroids.nearestNodesOrderedAsc(this)[0]
    }

    override fun equals(other: Any?): Boolean {
        return (this.uuid == (other as Node).uuid)
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString() = "${Node::class.simpleName}($uuid) : $position"

    companion object {
        fun emptyNode() = Node(position = Point(0, 0))

        fun Node.angleBetween(secondNode : Node) : Angle {
            return when {
                (secondNode.position.x >= this.position.x) && (secondNode.position.y < this.position.y) ->
                    fromRadians(atan((this.position.y - secondNode.position.y) / (secondNode.position.x - this.position.x)))
                (secondNode.position.x < this.position.x) && (secondNode.position.y < this.position.y) ->
                    Angle.fromDegrees(180) - fromRadians(atan((this.position.y - secondNode.position.y) / (this.position.x - secondNode.position.x)))
                (secondNode.position.x < this.position.x) && (secondNode.position.y >= this.position.y) ->
                    fromRadians(atan((secondNode.position.y - this.position.y) / (this.position.x - secondNode.position.x))) + Angle.fromDegrees(180)
                (secondNode.position.x >= this.position.x) && (secondNode.position.y >= this.position.y) ->
                    Angle.fromDegrees(360) - fromRadians(atan((secondNode.position.y - this.position.y) / (secondNode.position.x - this.position.x)))
                else -> Angle.fromDegrees(0)
            }
        }

        fun MutableList<Node>.getNode(uuid : UUID) : Node? {
            return this.firstOrNull { node -> node.uuid == uuid }
        }

        fun MutableList<Node>.addNode(nodeToAdd : Node, nodeDescription : String) : Boolean = this.add(Node(nodeToAdd, updDescription = nodeDescription))

        fun MutableList<Node>.addNodes(nodesToAdd : MutableList<Node>, nodeDescription : String) : Unit = nodesToAdd.forEach { nodeToAdd -> this.add(Node(nodeToAdd, updDescription = nodeDescription)) }

        fun MutableList<Node>.removeNode(nodeLinks : MutableList<NodeLink>, uuid : UUID) {
            nodeLinks.getNodeLinks(uuid).let { nodeLinks.removeAll(it) }
            this.remove( getNode(uuid) )
        }

        fun MutableList<Node>.updateNode(node : Node) {
            this.remove(node)
            this.add(node)
        }

        fun MutableList<Node>.getNodeLineList(nodeLinks : List<NodeLink>) : List<Pair<Point, Point>?> {

            val returnNodeLineList : MutableList<Pair<Point, Point>> = mutableListOf()

//            this.forEach { println("node : $it") }

            nodeLinks.forEach { nodeLink ->
//                println("nodeLink : $nodeLink")
                val firstNode = this.getNode(nodeLink.firstNodeUuid)
                val secondNode = this.getNode(nodeLink.secondNodeUuid)

                if ( (firstNode != null) && (secondNode != null) )
                    returnNodeLineList.add(Pair(firstNode.position, secondNode.position) ) }

            return returnNodeLineList
        }

        fun MutableList<Node>.linkNearNodes(linkOrphans : Boolean = true) : MutableList<NodeLink> {

//            println("checking for nodes to re-link...")

            val nodeLinks : MutableList<NodeLink> = mutableListOf()
            lateinit var closestNode : Node

            this.forEach { outer ->
                closestNode = Node (outer, updPosition = outer.position + Point(10000, 10000) ) //faraway point

//                println("linking outer: $outer, $closestNode")
                this.forEach { inner ->
                    //in case outer node is orphaned
                    if ( !nodeLinks.areNodesLinked(outer.uuid, inner.uuid) && (outer.uuid != inner.uuid) ) {
                        if ( Point.distance(inner.position, outer.position) <= Point.distance(outer.position, closestNode.position) ) {
                            closestNode = inner
//                            println("new closestNode: $outer, $closestNode")
                        }

                        if (Point.distance(inner.position, outer.position).toInt() <= linkNodeDistance) {
                            nodeLinks.addNodeLink(this, outer.uuid, inner.uuid)
                        }
                    }
                }
                //if outer node is orphaned, link to closest node
                if ( (this.size > 1) && (nodeLinks.getNodeLinks(outer.uuid).isNullOrEmpty() ) && linkOrphans ) {
//                    println ("adding link for orphan: $outer, $closestNode")
                    nodeLinks.addNodeLink(this, outer.uuid, closestNode.uuid)
                }
            }
            return nodeLinks
        }

        //replaces firstUuid node, removes secondUuid node; does not handle updates to NodeLinks
        fun MutableList<Node>.consolidateNode(nodeLinks : MutableList<NodeLink>, firstUuid : UUID, secondUuid : UUID) : MutableList<NodeLink> {
//            println("pre-consolidation nodes: $this")

            val firstNode = this.getNode(firstUuid)
            val secondNode = this.getNode(secondUuid)

            if ( (firstNode != null) && (secondNode != null) ) {

//                println("pre-consolidated first node: $firstNode")
//                println("pre-consolidated first node links: ${nodeLinks.getNodeLinks(firstUuid)}")

//                println("pre-consolidated second node: $secondNode")
//                println("pre-consolidated second node links: ${nodeLinks.getNodeLinks(secondUuid)}")

                //get UUIDs that second node links to, excluding link to node at firstUuid
                val secondNodeChildrenUuids = nodeLinks.getNodeChildrenUuids(secondUuid, firstUuid)

                //cycle through second node UUIDs, create link to first UUID node if none exists
                if (!secondNodeChildrenUuids.isNullOrEmpty()) {
                    secondNodeChildrenUuids.forEach { secondNodeChildUuid -> nodeLinks.addNodeLink(this, firstUuid, secondNodeChildUuid) }
                }

                //update first node with new position, between two nodes
                this.updateNode( Node( firstNode, updPosition = Point.middle(firstNode.position, secondNode.position) ) )

//                println("post-consolidated first node: $firstNode")
//                println("post-consolidated first node links: ${nodeLinks.getNodeLinks(firstUuid)}")

//                println("post-consolidated second node: $secondNode")
//                println("post-consolidated second node links: ${nodeLinks.getNodeLinks(secondUuid)}")

                //remove second node and links
                this.removeNode(nodeLinks, secondUuid)
            }

            return nodeLinks
        }

        fun MutableList<Node>.consolidateNearNodes(nodeLinks : MutableList<NodeLink>) : MutableList<NodeLink> {
//        println("checking for nodes to consolidate...")
            var returnNodeLinks = nodeLinks

            nodeLinks.filter { link -> link.getDistance(this) ?: NodeLink.consolidateNodeDistance + 1.0 <= NodeLink.consolidateNodeDistance }.forEach { consolidateNodeLink ->
                returnNodeLinks = this.consolidateNode(nodeLinks, consolidateNodeLink.firstNodeUuid, consolidateNodeLink.secondNodeUuid)
            }

            return returnNodeLinks
        }

        fun MutableList<Node>.consolidateStackedNodes(nodeLinks : MutableList<NodeLink>) : MutableList<NodeLink> {
//        println("checking for nodes to consolidate...")
            var returnNodeLinks = nodeLinks
            val checkNodes = this.toList()

            checkNodes.forEach { outer ->
                if (getNode(outer.uuid) != null)
                    checkNodes.forEach { inner ->
                        if (getNode(inner.uuid) != null)
                            if ( !nodeLinks.areNodesLinked(outer.uuid, inner.uuid) && (outer.uuid != inner.uuid) ) {
                                if (Point.distance(inner.position, outer.position).toInt() <= 0.1) {
                                    returnNodeLinks = this.consolidateNode(nodeLinks, outer.uuid, inner.uuid)
                                }
                            }
                    }
            }

            return returnNodeLinks
        }

        fun MutableList<Node>.averagePositionWithinNodes() : Point {
            val averageX = this.map {node -> node.position.x.toInt()}.average()
            val averageY = this.map {node -> node.position.y.toInt()}.average()

            return Point(averageX, averageY)
        }

        fun MutableList<Node>.randomPosition() : Point {

            val minXY = Point(10000, 10000)
            val maxXY = Point(0, 0)

            this.forEach { node ->
                if (node.position.x > maxXY.x) maxXY.x = node.position.x
                if (node.position.y > maxXY.y) maxXY.y = node.position.y
                if (node.position.x < minXY.x) minXY.x = node.position.x
                if (node.position.y < minXY.y) minXY.y = node.position.y
            }

//            println("max: $maxXY, min: $minXY")

            val randomXInRange = Random.nextInt(maxXY.x.toInt() - minXY.x.toInt() ) + minXY.x.toInt()
            val randomYInRange = Random.nextInt(maxXY.y.toInt() - minXY.y.toInt() ) + minXY.y.toInt()

//            println("randomX: $randomXInRange, randomY: $randomYInRange")

            return Point(randomXInRange, randomYInRange)
        }

        fun List<Node>.nearestNodesOrderedAsc(refNode : Node) : MutableList<Node> {

            val nodeDistMap = mutableMapOf<Node, Double>()

            this.forEach { node ->
                val nodeToRefDistance = node.position.distanceTo(refNode.position)

                nodeDistMap[node] = nodeToRefDistance
            }

//            println("nearest nodes found for $refNode")

            return nodeDistMap.toList().sortedBy { (_, dist) -> dist}.toMap().keys.toMutableList()
        }

        fun MutableList<Node>.cluster(rooms : Int = 4, maxIterations : Int = 4, roomIdx : Int = 0) : MutableMap<Node, MutableList<Node>> {

            var roomIdxVar = roomIdx

            val centroids = MutableList(size = rooms) { Node(position = this.randomPosition(), description = "Room${roomIdxVar++}" ) }
            val nodeClusters = mutableMapOf<Node, MutableList<Node>>()

//        println("init nodeRooms: $nodeRooms")
            (0 until rooms).toList().forEach {
                nodeClusters[centroids[it]] = mutableListOf()
            }

            (0 until maxIterations).toList().forEach { iteration ->
                val isLastIteration: Boolean = iteration == maxIterations - 1

                centroids.forEach { centroid -> nodeClusters[centroid]!!.clear() }

                this.forEach { node ->
                    val nearestNodeDescription = node.nearestCentroid(centroids).description
                    nodeClusters[node.nearestCentroid(centroids)]!!.add(Node (node, updDescription = node.nearestCentroid(centroids).description))
//                    println ("iteration: $iteration, nearestNodeDescription: $nearestNodeDescription, node: $node")
                }

//            println("iteration $iteration:")
//            println(nodeRooms)

                if (!isLastIteration) centroids.forEach { centroid -> nodeClusters[centroid]!!.averagePositionWithinNodes() }
            }

            return nodeClusters
        }

        fun List<Node>.buildNodePaths(noise : Int = 0, paths : Int = 4, nodeDescription : String) : INodeMesh {

            val returnPathMeshes = NodeMesh("pathMesh${Random.nextInt(256)}")

            println("building node paths..")

            this.forEach { node ->

                val nearestNodes = this.nearestNodesOrderedAsc(node)

                (0 until paths).forEach { idx ->
                    if ( (idx >= 0) && (idx < nearestNodes.size) )
                        returnPathMeshes.addMesh( Pair(node, nearestNodes[idx]).buildNodeLinkLine(noise, nodeDescription) )
                }

            }
            println("nodepaths built..!")

            return returnPathMeshes
        }

        fun List<Node>.getFarthestNode(refNode : Node) : Node {

            val nearestNodes = this.nearestNodesOrderedAsc(refNode)

            return if (nearestNodes.size > 1) nearestNodes[nearestNodes.size - 1] else refNode
        }

        fun List<Node>.getRandomNode() : Node = this[Random.nextInt(this.size)]
    }
}