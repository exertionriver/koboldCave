package org.river.exertion.koboldCave.node

import org.river.exertion.*
import org.river.exertion.koboldCave.leaf.ILeaf
import org.river.exertion.koboldCave.Line.Companion.angleBetween
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.addNodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.consolidateNodeDistance
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNodeChildrenUuids
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNodeLinks
import org.river.exertion.koboldCave.node.NodeLink.Companion.linkNodeDistance
import org.river.exertion.koboldCave.node.NodeLink.Companion.stackedNodeDistance
import org.river.exertion.koboldCave.Line
import org.river.exertion.koboldCave.node.nodeMesh.INodeMesh
import java.util.*
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Node(val uuid: UUID = UUID.randomUUID(), val position : Point, val description : String = "Node${Random.nextInt(2048)}"
           , var attributes : NodeAttributes = NodeAttributes() ) {

    constructor(initNodeType : NodeAttributes.NodeType) : this() {
        attributes.nodeType = initNodeType
    }

    constructor(leaf : ILeaf, description : String = "${Node::class.simpleName}${Random.nextInt(2048)}") : this (
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

    constructor() : this(position = Point(0F,0F))

    fun nearestCentroid(centroids : List<Node>) : Node {
        return if ( centroids.isNotEmpty() ) centroids.nearestNodesOrderedAsc(this)[0] else this
    }

    override fun equals(other: Any?): Boolean {
        return (this.uuid == (other as Node).uuid)
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString() = "${Node::class.simpleName}(${description}_$uuid) : $position"

    fun getNodeChildren(nodes : MutableList<Node>, nodeLinks : MutableList<NodeLink>) = nodeLinks.getNodeChildrenUuids(this.uuid, this.uuid).mapNotNull { nodes.getNode(it) }

    companion object {


        fun Node.angleBetween(secondNode : Node) : Angle {
            return this.position.angleBetween(secondNode.position)
        }

        fun MutableList<Node>.getNode(uuid : UUID) : Node? {
            return this.firstOrNull { node -> node.uuid == uuid }
        }

        fun MutableList<Node>.addNode(nodeToAdd : Node) : Boolean {
            return this.add( nodeToAdd )
        }

        fun MutableList<Node>.addNodes(nodesToAdd : MutableList<Node>, nodeDescription : String) : Unit = nodesToAdd.forEach { nodeToAdd -> this.add(
            Node(nodeToAdd, updDescription = nodeDescription)
        ) }

        fun MutableList<Node>.removeNode(nodeLinks : MutableList<NodeLink>, uuid : UUID) : Boolean {
            nodeLinks.getNodeLinks(uuid).let { nodeLinks.removeAll(it) }
            return this.remove( getNode(uuid) )
        }

        //used to update position, attributes, etc; UUID remains the same
        fun MutableList<Node>.updateNode(node : Node) {
            this.associateBy { entry -> entry.uuid }.toMutableMap()[node.uuid] = node
        }

        fun MutableList<Node>.getLineList(nodeLinks : List<NodeLink>) : List<Line> {

            val returnNodeLineList : MutableList<Line> = mutableListOf()

//            this.forEach { println("node : $it") }

            nodeLinks.forEach { nodeLink ->
//                println("nodeLink : $nodeLink")
                val firstNode = this.getNode(nodeLink.firstNodeUuid)
                val secondNode = this.getNode(nodeLink.secondNodeUuid)

                if ( (firstNode != null) && (secondNode != null) )
                    returnNodeLineList.add(Line(firstNode.position, secondNode.position) ) }

            return returnNodeLineList
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
                if (!secondNodeChildrenUuids.isEmpty()) {
                    secondNodeChildrenUuids.forEach { secondNodeChildUuid -> nodeLinks.addNodeLink(this, firstUuid, secondNodeChildUuid) }
                }

                //update first node with new position, between two nodes
                this.updateNode( Node( firstNode, updPosition = middle(firstNode.position, secondNode.position) ) )

 //               println("post-consolidated first node: $firstNode")
 //               println("post-consolidated first node links: ${nodeLinks.getNodeLinks(firstUuid)}")

 //               println("post-consolidated second node: $secondNode")
 //               println("post-consolidated second node links: ${nodeLinks.getNodeLinks(secondUuid)}")

                //remove second node and links
                this.removeNode(nodeLinks, secondUuid)
            }

            return nodeLinks
        }

        fun MutableList<Node>.consolidateNearNodes(nodeLinks : MutableList<NodeLink>) : MutableList<NodeLink> {
//        println("checking for near nodes to consolidate...")
            var returnNodeLinks = nodeLinks
            val checkNodes = this

            checkNodes.sortedBy { it.uuid.toString() }.forEach { refNode ->
                this.filter { it.uuid.toString() > refNode.uuid.toString() }.forEach { checkNode ->
                    //                checkNodes.sortedBy { it.uuid.toString() }.forEach { checkNode ->
//                    if (checkNode.uuid.toString() > refNode.uuid.toString()) {
                        if ( refNode.position.dst(checkNode.position) <= consolidateNodeDistance ) {
//                            println("consolidating ${refNode.uuid} and ${checkNode.uuid}")
                            returnNodeLinks = this.consolidateNode(nodeLinks, refNode.uuid, checkNode.uuid)
  //                      }
                    }
                }
            }

            return returnNodeLinks
        }

        fun MutableList<Node>.consolidateStackedNodes(nodeLinks : MutableList<NodeLink>) : MutableList<NodeLink> {
//        println("checking for stacked nodes to consolidate...")
            var returnNodeLinks = nodeLinks
//            val checkNodes = this.toList()

            this.sortedBy { it.uuid.toString() }.forEach { refNode ->
                this.filter { it.uuid.toString() > refNode.uuid.toString() }.forEach { checkNode ->
//                checkNodes.sortedBy { it.uuid.toString() }.forEach { checkNode ->
       //             if (checkNode.uuid.toString() > refNode.uuid.toString()) {
                        if (checkNode.position.dst(refNode.position).toInt() <= stackedNodeDistance) { //basically same node, "stacked"
//                            println("consolidating ${refNode.uuid} and ${checkNode.uuid}")
                            returnNodeLinks = this.consolidateNode(nodeLinks, refNode.uuid, checkNode.uuid)
                        }
         //           }
                }
            }

            return returnNodeLinks
        }

        //TODO: retain previous links
        fun MutableList<Node>.linkNearNodes(nodeLinks : MutableList<NodeLink> = mutableListOf(), nodeMeshToBorder : INodeMesh? = null, orthoBorderDistance : Double = ILeaf.NextDistancePx * 0.2, linkOrphans : Boolean = true) : MutableList<NodeLink> {
//            println("checking for nodes to link...")
            val returnNodeLinks = nodeLinks
//            val checkNodes = this.toList()

//            val linkDistance = if (nodeMeshToBorder == null) linkNodeDistance else linkNodeDistance * 2

            lateinit var closestNode : Node

            this.sortedBy { it.uuid.toString() }.forEach { refNode ->
            //    val sortedCheckNodes = checkNodes.nearestNodesOrderedAsc(refNode)
                this.filter { it.uuid.toString() > refNode.uuid.toString() }.forEach { checkNode ->
//                checkNodes.sortedBy { it.uuid.toString() }.forEach { checkNode ->
                        if (checkNode.position.dst(refNode.position).toInt() <= linkNodeDistance) {
//                            println("linking ${refNode.uuid} and ${checkNode.uuid}")
                       //     if (nodeMeshToBorder == null)
                                returnNodeLinks.addNodeLink(this, refNode.uuid, checkNode.uuid)
                        }
                    }
            }

            return returnNodeLinks.toList().distinct().toMutableList()
        }

        fun MutableList<Node>.averagePositionWithinNodes() : Point {
            val averageX = this.map {node -> node.position.x.toInt()}.average()
            val averageY = this.map {node -> node.position.y.toInt()}.average()

            return Point(averageX.toFloat(), averageY.toFloat())
        }

        fun MutableList<Node>.randomPosition() : Point {

            val minXY = Point(10000F, 10000F)
            val maxXY = Point(0F, 0F)

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

            return Point(randomXInRange.toFloat(), randomYInRange.toFloat())
        }

        fun List<Node>.nearestNodesOrderedAsc(refNode : Node) : MutableList<Node> {

            val nodeDistMap = mutableMapOf<Node, Double>()

            this.forEach { node ->
                val nodeToRefDistance = node.position.dst(refNode.position)

                nodeDistMap[node] = nodeToRefDistance.toDouble()
            }

//            println("nearest nodes found for $refNode")

            return nodeDistMap.toList().sortedBy { (_, dist) -> dist}.toMap().keys.toMutableList()
        }

        fun List<Node>.getFarthestNode(refNode : Node = Node(position = this.toMutableList().averagePositionWithinNodes())) : Node {

            val nearestNodes = this.nearestNodesOrderedAsc(refNode)

            return if (nearestNodes.size > 1) nearestNodes[nearestNodes.size - 1] else refNode
        }

        fun List<Node>.getRandomNode() : Node = if (this.isNotEmpty()) this[Random.nextInt(this.size)] else Node()

        fun MutableList<Node>.removeOrphans(nodeLinks : MutableList<NodeLink>, minPercent : Double) : MutableList<Node> {

//            println("checking for orphaned Nodes to remove...")

            val nodeCountThreshhold = this.size * minPercent

            val processedNodes = mutableSetOf<Node>()
            val checkNodes = mutableSetOf<Node>()
            val previousCheckNodes = mutableSetOf<Node>()
            val returnNodes = mutableListOf<Node>()

            while (processedNodes.size < this.size) {

                val nodesRemaining = this
                nodesRemaining.removeAll(returnNodes)

                checkNodes.clear()
                previousCheckNodes.clear()

                previousCheckNodes.add(nodesRemaining.getRandomNode())
//                println("adding randomNode, pcn:$previousCheckNodes")

                while(checkNodes != previousCheckNodes) {
                    previousCheckNodes.addAll(checkNodes)
//                    println("pcn(${previousCheckNodes.size}): $previousCheckNodes")

                    previousCheckNodes.forEach { previousCheckNode ->
//                        println("adding $previousCheckNode children to checkNodes")

                        val nodeChildren = previousCheckNode.getNodeChildren(this, nodeLinks)

//                        println("nodeChildren: $nodeChildren")

                        if (nodeChildren.isNullOrEmpty())
                            checkNodes.add(previousCheckNode)
                        else
                            checkNodes.addAll(nodeChildren)
                    }

//                    println("cn(${checkNodes.size}): $checkNodes")
                }

                if (checkNodes.size > nodeCountThreshhold) {
//                    println("${checkNodes.size} < $nodeCountThreshhold: removing orphaned nodes: $checkNodes")
                    returnNodes.addAll(checkNodes)
                }

                processedNodes.addAll(checkNodes)

            }

            return returnNodes
        }

        fun MutableList<Node>.adoptRoomOrphans(nodeLinks : MutableList<NodeLink>, roomNodes : Map<String, MutableList<Node>>) : MutableList<Node> {

            println("checking for orphaned room Nodes to adopt...")

            val returnNodes = mutableListOf<Node>()

            roomNodes.keys.forEach { roomNodeDescription ->

                val nodeCountThreshhold = roomNodes[roomNodeDescription]!!.size * 0.25

                val processedNodes = mutableSetOf<Node>()
                val checkNodes = mutableSetOf<Node>()
                val previousCheckNodes = mutableSetOf<Node>()
                val returnRoomNodes = mutableSetOf<Node>()

//                println("processing $roomNodeDescription")

                //first, identify connected nodes
                while (processedNodes.size < roomNodes[roomNodeDescription]!!.size) {

                    val nodesRemaining = roomNodes[roomNodeDescription]!!
                    nodesRemaining.removeAll(returnRoomNodes)

                    checkNodes.clear()
                    previousCheckNodes.clear()

                    previousCheckNodes.add(nodesRemaining.getRandomNode())
//                println("adding randomNode, pcn:$previousCheckNodes")

                    while(checkNodes != previousCheckNodes) {
                        previousCheckNodes.addAll(checkNodes)
//                    println("pcn(${previousCheckNodes.size}): $previousCheckNodes")

                        previousCheckNodes.forEach { previousCheckNode ->
//                        println("adding $previousCheckNode children to checkNodes")

                            val nodeChildren = previousCheckNode.getNodeChildren(roomNodes[roomNodeDescription]!!, nodeLinks).filter { childNode -> childNode.description == roomNodeDescription }

//                        println("nodeChildren: $nodeChildren")

                            if (nodeChildren.isNullOrEmpty())
                                checkNodes.add(previousCheckNode)
                            else
                                checkNodes.addAll(nodeChildren)
                        }

//                    println("cn(${checkNodes.size}): $checkNodes")
                    }

                    if (checkNodes.size > nodeCountThreshhold) {
//                    println("${checkNodes.size} < $nodeCountThreshhold: removing orphaned nodes: $checkNodes")
                        returnRoomNodes.addAll(checkNodes)
                    }

                    processedNodes.addAll(checkNodes)

                }

                //next, associated disconnected nodes with other rooms
                val adoptNodes = processedNodes.minus(returnRoomNodes)

                //create adoptNode associated with child room
                adoptNodes.forEach { adoptNode ->

                    var adoptRoom = adoptNode.description
                    var adoptNodeScan = adoptNode
                    val adoptNodeScanList = mutableListOf<Node>()

                    while (adoptRoom == adoptNode.description) {
                        val adoptRoomChildren = adoptNodeScan.getNodeChildren(this, nodeLinks).filter{ nodeChild -> nodeChild.description != roomNodeDescription }

                        if (!adoptRoomChildren.isNullOrEmpty()) {
                            adoptRoom = adoptRoomChildren[0].description
                            println("adoptNode: $adoptNode going to ${adoptRoomChildren[0].description}")
                            returnRoomNodes.add(Node(adoptNode, updDescription = adoptRoomChildren[0].description))
                        } else {
                            adoptNodeScanList.add(adoptNodeScan)
                            adoptNodeScan = adoptNodeScan.getNodeChildren(this, nodeLinks).filter{ nodeChild -> !adoptNodeScanList.contains(nodeChild) }[0]
                        }
                    }


                }

                returnNodes.addAll(returnRoomNodes)
            }

            return returnNodes
        }

        fun MutableList<Node>.moveNodes(offset : Point) : MutableList<Node> {

            val returnNodes = mutableListOf<Node>()

            this.forEach { returnNodes.add(Node(it, updPosition = it.position + offset))}

            return returnNodes
        }

        fun MutableList<Node>.scaleNodes(pivot : Point = this.averagePositionWithinNodes(), scale : Float) : MutableList<Node> {

            val returnNodes = mutableListOf<Node>()

//            println ("pivot: $pivot, scale: $scale")

            this.forEach { node ->
                val secondPoint = node.position

                val distance = pivot.dst(secondPoint)
                val scaledDistance = distance * scale
                val angleBetween = pivot.angleBetween(node.position)

//                println ("distance: $distance, scaledDistance: $scaledDistance, angleBetween: $angleBetween")

                val scaledPoint = pivot.getPositionByDistanceAndAngle(scaledDistance, angleBetween)

//                println ("node $node scaled : (${scaledPoint.x}, ${scaledPoint.y})")

                returnNodes.add( Node(node, updPosition = Point(scaledPoint.x, scaledPoint.y) ) )
            }

            return returnNodes
        }
    }
}