package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import leaf.ILeaf
import node.NodeLink.Companion.addNodeLink
import node.NodeLink.Companion.areNodesLinked
import node.NodeLink.Companion.getNodeChildrenUuids
import node.NodeLink.Companion.getNodeLinks
import node.NodeLink.Companion.linkNodeDistance
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Node(val uuid: UUID = UUID.randomUUID(Random.Default), val position : Point) {

    constructor(leaf : ILeaf) : this (
        uuid = leaf.uuid
        , position = leaf.position
    )

    constructor(copyNode : Node
        , updUuid : UUID = copyNode.uuid
        , updPosition : Point = copyNode.position) : this (
        uuid = updUuid
        , position = updPosition
    )

    fun nearestCentroid(centroids : List<Node>) : Node {

        var minimumDistance = 1024.0
        lateinit var nearestCentroid : Node

        centroids.forEach { centroid ->
            val currentDistance = position.distanceTo(centroid.position)

            if (currentDistance < minimumDistance) {
                minimumDistance = currentDistance
                nearestCentroid = centroid
            }
        }

        return nearestCentroid
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

        fun MutableList<Node>.getNode(uuid : UUID) : Node? {
            return this.firstOrNull { node -> node.uuid == uuid }
        }

        fun MutableList<Node>.addNode(node : Node) : Boolean = this.add(node)

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

            nodeLinks.forEach { nodeLink -> returnNodeLineList.add(Pair(this.getNode(nodeLink.firstNodeUuid)!!.position, this.getNode(nodeLink.secondNodeUuid)!!.position) ) }

            return returnNodeLineList
        }

        fun MutableList<Node>.linkNodes(linkOrphans : Boolean = true) : MutableList<NodeLink> {

            println("checking for nodes to re-link...")

            val nodeLinks : MutableList<NodeLink> = mutableListOf()
            lateinit var closestNode : Node

            this.forEach { outer ->
                closestNode = Node (outer, updPosition = outer.position + Point(10000, 10000) ) //faraway point

                println("linking outer: $outer, $closestNode")
                this.forEach { inner ->
                    //in case outer node is orphaned
                    if ( !nodeLinks.areNodesLinked(outer.uuid, inner.uuid) && (outer.uuid != inner.uuid) ) {
                        if ( Point.distance(inner.position, outer.position) <= Point.distance(outer.position, closestNode.position) ) {
                            closestNode = inner
                            println("new closestNode: $outer, $closestNode")
                        }

                        if (Point.distance(inner.position, outer.position).toInt() <= linkNodeDistance) {
                            nodeLinks.addNodeLink(outer.uuid, inner.uuid)
                        }
                    }
                }
                //if outer node is orphaned, link to closest node
                if ( (this.size > 1) && (nodeLinks.getNodeLinks(outer.uuid).isNullOrEmpty() ) && linkOrphans ) {
                    println ("adding link for orphan: $outer, $closestNode")
                    nodeLinks.addNodeLink(outer.uuid, closestNode.uuid)
                }
            }
            return nodeLinks
        }

        //replaces firstUuid node, removes secondUuid node; does not handle updates to NodeLinks
        fun MutableList<Node>.consolidateNode(nodeLinks : MutableList<NodeLink>, firstUuid : UUID, secondUuid : UUID) : MutableList<NodeLink> {
            println("pre-consolidation nodes: $this")

            val firstNode = this.getNode(firstUuid)
            val secondNode = this.getNode(secondUuid)

            if ( (firstNode != null) && (secondNode != null) ) {

                println("pre-consolidated first node: $firstNode")
                println("pre-consolidated first node links: ${nodeLinks.getNodeLinks(firstUuid)}")

                println("pre-consolidated second node: $secondNode")
                println("pre-consolidated second node links: ${nodeLinks.getNodeLinks(secondUuid)}")

                //get UUIDs that second node links to, excluding link to node at firstUuid
                val secondNodeChildrenUuids = nodeLinks.getNodeChildrenUuids(secondUuid, firstUuid)

                //cycle through second node UUIDs, create link to first UUID node if none exists
                if (!secondNodeChildrenUuids.isNullOrEmpty()) {
                    secondNodeChildrenUuids.forEach { secondNodeChildUuid ->
                        if (!nodeLinks.areNodesLinked(firstUuid, secondNodeChildUuid)) nodeLinks.addNodeLink(firstUuid, secondNodeChildUuid)
                    }
                }

                //update first node with new position, between two nodes
                this.updateNode( Node( firstNode, updPosition = Point.middle(firstNode.position, secondNode.position) ) )

                println("post-consolidated first node: $firstNode")
                println("post-consolidated first node links: ${nodeLinks.getNodeLinks(firstUuid)}")

                println("post-consolidated second node: $secondNode")
                println("post-consolidated second node links: ${nodeLinks.getNodeLinks(secondUuid)}")

                //remove second node and links
                this.removeNode(nodeLinks, secondUuid)
            }

            return nodeLinks
        }

        fun MutableList<Node>.consolidateNodes(nodeLinks : MutableList<NodeLink>) : MutableList<NodeLink> {
//        println("checking for nodes to consolidate...")
            var returnNodeLinks = nodeLinks

            nodeLinks.filter { link -> link.getDistance(this)!! <= NodeLink.consolidateNodeDistance }.forEach { consolidateNodeLink ->
                returnNodeLinks = this.consolidateNode(nodeLinks, consolidateNodeLink.firstNodeUuid, consolidateNodeLink.secondNodeUuid)
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

            println("max: $maxXY, min: $minXY")

            val randomXInRange = Random.nextInt(maxXY.x.toInt() - minXY.x.toInt() ) + minXY.x.toInt()
            val randomYInRange = Random.nextInt(maxXY.y.toInt() - minXY.y.toInt() ) + minXY.y.toInt()

            println("randomX: $randomXInRange, randomY: $randomYInRange")

            return Point(randomXInRange, randomYInRange)
        }

        fun MutableList<Node>.cluster(rooms : Int = 4, maxIterations : Int = 4) : MutableMap<Node, MutableList<Node>> {

            val centroids = MutableList(size = rooms) { Node(position = this.randomPosition() ) }
            val nodeClusters = mutableMapOf<Node, MutableList<Node>>()

//        println("init nodeRooms: $nodeRooms")
            (0 until rooms).toList().forEach {
                nodeClusters[centroids[it]] = mutableListOf()
            }

            (0 until maxIterations).toList().forEach { iteration ->
                val isLastIteration: Boolean = iteration == maxIterations - 1

                centroids.forEach { centroid -> nodeClusters[centroid]!!.clear() }

                this.forEach { node -> nodeClusters[node.nearestCentroid(centroids)]!!.add(node) }

//            println("iteration $iteration:")
//            println(nodeRooms)

                if (!isLastIteration) centroids.forEach { centroid -> nodeClusters[centroid]!!.averagePositionWithinNodes() }
            }

            return nodeClusters
        }

        /*
        fun getNodeLine(parentLeaf : CaveLeaf) : List<Node> {

            val nodeLineList = mutableListOf<Node>()
            var currentPoint = Point(parentLeaf.position.x, parentLeaf.position.y)
            val lineEndPoint = Point(this.position.x, this.position.y)
            val nodeLength = randNodeLengthFromParent()

            var previousNode = parentLeaf.getNode(limitRecursion = true)
            var currentNode = emptyNode()

    //        println (this.uuid.toString() + "nodeLine + [${parentOakLeaf.position}, ${this.position}] step $nodeLength")

            when {
                (lineEndPoint.x >= currentPoint.x) && (lineEndPoint.y >= currentPoint.y) -> {
                    val angle = Angle.fromRadians(atan( (lineEndPoint.y - currentPoint.y) / (lineEndPoint.x - currentPoint.x) ) )

    //                println("1) node at ${currentPoint}, angle ${angle.degrees}")
                    currentPoint = Point(currentPoint.x + nodeLength * cos(angle), currentPoint.y + nodeLength * sin(angle))

                    while ((lineEndPoint.x >= currentPoint.x) && (lineEndPoint.y >= currentPoint.y) ) {
    //                    println("1) node at ${currentPoint}, angle ${angle.degrees}")
                        currentNode = Node(position = currentPoint, childNodes = mutableListOf(previousNode))
                        previousNode.childNodes.add(currentNode)
                        nodeLineList.add(currentNode)
                        previousNode = currentNode
                        currentPoint = Point(currentPoint.x + nodeLength * cos(angle), currentPoint.y + nodeLength * sin(angle))
                    }
                }
                (lineEndPoint.x < currentPoint.x) && (lineEndPoint.y >= currentPoint.y) -> {
                    val angle = Angle.fromRadians(atan((currentPoint.y - lineEndPoint.y) / (lineEndPoint.x - currentPoint.x)))

    //                println("2) node at ${currentPoint}, angle ${angle.degrees}")
                    currentPoint = Point(currentPoint.x - nodeLength * cos(angle), currentPoint.y + nodeLength * sin(angle))

                    while ( (lineEndPoint.x < currentPoint.x) && (lineEndPoint.y >= currentPoint.y)  ) {
    //                    println("2) node at ${currentPoint}, angle ${angle.degrees}")
                        currentNode = Node(position = currentPoint, childNodes = mutableListOf(previousNode))
                        previousNode.childNodes.add(currentNode)
                        nodeLineList.add(currentNode)
                        previousNode = currentNode
                        currentPoint = Point(currentPoint.x - nodeLength * cos(angle), currentPoint.y + nodeLength * sin(angle))
                    }
                }
                (lineEndPoint.x >= currentPoint.x) && (lineEndPoint.y < currentPoint.y) -> {
                    val angle = Angle.fromRadians(atan((lineEndPoint.y - currentPoint.y) / (currentPoint.x - lineEndPoint.x)))

    //                println("3) node at ${currentPoint}, angle ${angle.degrees}")
                    currentPoint = Point(currentPoint.x + nodeLength * cos(angle), currentPoint.y - nodeLength * sin(angle))

                    while ( (lineEndPoint.x >= currentPoint.x) && (lineEndPoint.y < currentPoint.y) ) {
    //                    println("3) node at ${currentPoint}, angle ${angle.degrees}")
                        currentNode = Node(position = currentPoint, childNodes = mutableListOf(previousNode))
                        previousNode.childNodes.add(currentNode)
                        nodeLineList.add(currentNode)
                        previousNode = currentNode
                        currentPoint = Point(currentPoint.x + nodeLength * cos(angle), currentPoint.y - nodeLength * sin(angle))
                    }
                }
                (lineEndPoint.x < currentPoint.x) && (lineEndPoint.y < currentPoint.y) -> {
                    val angle = Angle.fromRadians(atan((currentPoint.y - lineEndPoint.y) / (currentPoint.x - lineEndPoint.x)))

    //                println("4) node at ${currentPoint}, angle ${angle.degrees}")
                    currentPoint = Point(currentPoint.x - nodeLength * cos(angle), currentPoint.y - 2 * sin(angle))

                    while ( (lineEndPoint.x < currentPoint.x) && (lineEndPoint.y < currentPoint.y)  ) {
    //                    println("4) node at ${currentPoint}, angle ${angle.degrees}")
                        currentNode = Node(position = currentPoint, childNodes = mutableListOf(previousNode))
                        previousNode.childNodes.add(currentNode)
                        nodeLineList.add(currentNode)
                        previousNode = currentNode
                        currentPoint = Point(currentPoint.x - nodeLength * cos(angle), currentPoint.y - nodeLength * sin(angle))
                    }
                }
            }

            currentNode.childNodes.add(this.getNode(limitRecursion = true))

            return nodeLineList
        }


         */
    }
}