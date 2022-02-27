package org.river.exertion.geom.node

import org.river.exertion.*
import org.river.exertion.geom.leaf.ILeaf
import org.river.exertion.geom.Line.Companion.angleBetween
import org.river.exertion.geom.node.NodeLink.Companion.addNodeLink
import org.river.exertion.geom.node.NodeLink.Companion.consolidateNodeDistance
import org.river.exertion.geom.node.NodeLink.Companion.getNodeChildrenUuids
import org.river.exertion.geom.node.NodeLink.Companion.getNodeLinks
import org.river.exertion.geom.node.NodeLink.Companion.linkNodeDistance
import org.river.exertion.geom.node.NodeLink.Companion.stackedNodeDistance
import org.river.exertion.geom.Line
import org.river.exertion.geom.node.nodeMesh.INodeMesh
import org.river.exertion.geom.node.nodeMesh.NodeLine
import org.river.exertion.geom.node.nodeMesh.NodeMesh
import java.util.*
import kotlin.random.Random

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

    fun nearestCentroid(centroids : MutableSet<Node>) : Node {
        return if ( centroids.isNotEmpty() ) centroids.nearestNodesOrderedAsc(this)[0] else this
    }

    override fun equals(other: Any?): Boolean {
        return (this.uuid == (other as Node).uuid)
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString() = "${Node::class.simpleName}(${description}_$uuid) : $position"

    fun getNodeChildren(nodes : MutableSet<Node>, nodeLinks : MutableSet<NodeLink>) = nodeLinks.getNodeChildrenUuids(this.uuid, this.uuid).mapNotNull { nodes.getNode(it) }.toMutableSet()

    companion object {


        fun Node.angleBetween(secondNode : Node) : Angle {
            return this.position.angleBetween(secondNode.position)
        }

        fun MutableSet<Node>.getNode(uuid : UUID) : Node? {
            return this.firstOrNull { node -> node.uuid == uuid }
        }

        fun MutableSet<Node>.addNode(nodeToAdd : Node) : Boolean {
            return this.add( nodeToAdd )
        }

        fun MutableSet<Node>.addNodes(nodesToAdd : MutableSet<Node>, nodeDescription : String) : Unit = nodesToAdd.forEach { nodeToAdd -> this.add(
            Node(nodeToAdd, updDescription = nodeDescription)
        ) }

        fun MutableSet<Node>.removeNode(nodeLinks : MutableSet<NodeLink>, uuid : UUID) : Boolean {
            nodeLinks.getNodeLinks(uuid).let { nodeLinks.removeAll(it) }
            return this.remove( getNode(uuid) )
        }

        //used to update position, attributes, etc; UUID remains the same
        fun MutableSet<Node>.updateNode(node : Node) {
            this.associateBy { entry -> entry.uuid }.toMutableMap()[node.uuid] = node
        }

        fun MutableSet<Node>.getLineSet(nodeLinks : MutableSet<NodeLink>) : MutableSet<Line> {

            val returnNodeLineSet : MutableSet<Line> = mutableSetOf()

//            this.forEach { println("node : $it") }

            nodeLinks.forEach { nodeLink ->
//                println("nodeLink : $nodeLink")
                val firstNode = this.getNode(nodeLink.firstNodeUuid)
                val secondNode = this.getNode(nodeLink.secondNodeUuid)

                if ( (firstNode != null) && (secondNode != null) )
                    returnNodeLineSet.add(Line(firstNode.position, secondNode.position) ) }

            return returnNodeLineSet
        }

        //replaces firstUuid node, removes secondUuid node; does not handle updates to NodeLinks
        fun MutableSet<Node>.consolidateNode(nodeLinks : MutableSet<NodeLink>, firstUuid : UUID, secondUuid : UUID) : MutableSet<NodeLink> {
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

        fun MutableSet<Node>.consolidateNearNodes(nodeLinks : MutableSet<NodeLink>) : MutableSet<NodeLink> {
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

        fun MutableSet<Node>.consolidateStackedNodes(nodeLinks : MutableSet<NodeLink>) : MutableSet<NodeLink> {
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

        fun MutableSet<Node>.linkNearNodes(nodeLinks : MutableSet<NodeLink> = mutableSetOf(), nodeMeshToBorder : INodeMesh? = null, orthoBorderDistance : Double = NextDistancePx * 0.2, linkOrphans : Boolean = true) : MutableSet<NodeLink> {
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

            return returnNodeLinks
        }

        fun MutableSet<Node>.averagePositionWithinNodes() : Point {
            val averageX = this.map {node -> node.position.x.toInt()}.average()
            val averageY = this.map {node -> node.position.y.toInt()}.average()

            return Point(averageX.toFloat(), averageY.toFloat())
        }

        fun MutableSet<Node>.randomPosition() : Point {

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

        fun MutableSet<Node>.nearestNodesOrderedAsc(refNode : Node) : MutableList<Node> {

            val nodeDistMap = mutableMapOf<Node, Double>()

            this.forEach { node ->
                val nodeToRefDistance = node.position.dst(refNode.position)

                nodeDistMap[node] = nodeToRefDistance.toDouble()
            }

//            println("nearest nodes found for $refNode")

            return nodeDistMap.toList().sortedBy { (_, dist) -> dist}.toMap().keys.toMutableList()
        }

        fun MutableSet<Node>.getFarthestNode(refNode : Node = Node(position = this.averagePositionWithinNodes())) : Node {

            val nearestNodes = this.nearestNodesOrderedAsc(refNode)

            return if (nearestNodes.size > 1) nearestNodes[nearestNodes.size - 1] else refNode
        }

        fun MutableSet<Node>.getRandomNode() : Node = if (this.isNotEmpty()) this.toList()[Random.nextInt(this.size)] else Node()

        fun MutableSet<Node>.getRandomUnoccupiedNode() : Node = if (this.isNotEmpty()) { val nodeList = this.filter { !it.attributes.occupied }.toList() ; nodeList[Random.nextInt(nodeList.size)] } else Node()

        fun MutableSet<Node>.processOrphans(nodeLinks : MutableSet<NodeLink>) : MutableSet<Node> {

//            println("checking for orphaned Nodes to remove...")
            val returnNodes = mutableSetOf<Node>()

            this.forEach {
                val nodeChildren = it.getNodeChildren(this, nodeLinks)
//                println("$it, ${nodeChildren.size}");
                if (nodeChildren.size > 0) {
                    nodeChildren.forEachIndexed { idx, node ->
//                        print("$idx:$node")
                    }
//                    println()
                    returnNodes.add(it)
                }
            }
            return returnNodes
        }

        fun MutableSet<Node>.bridgeSegments(nodeLinks : MutableSet<NodeLink>) : INodeMesh {

            val returnMesh = NodeMesh()
            val processedNodes = mutableSetOf<Node>()
//                val checkNodes = mutableSetOf<Node>()
//                val previousCheckNodes = mutableSetOf<Node>()
            val bridgeNodes = mutableSetOf<Node>()
            val bridgeNodeLinks = mutableSetOf<NodeLink>()

//                println("processing $roomNodeDescription")

            while (processedNodes.size < this.size) {

                val segmentNodes = mutableSetOf<Node>()
                val randomFirstNode = this.filter { !processedNodes.contains(it) }.first()

                segmentNodes.add(randomFirstNode)
                var prevSegmentSize = 0
                var currSegmentSize = 1

                while (prevSegmentSize < currSegmentSize) {
                    prevSegmentSize = currSegmentSize
                    val nodesToAdd = mutableSetOf<Node>()
                    segmentNodes.forEach { nodesToAdd.addAll( it.getNodeChildren(this, nodeLinks) ) }
                    segmentNodes.addAll(nodesToAdd)
                    currSegmentSize = segmentNodes.size
                }

//                println("processedNodes.size: ${processedNodes.size}; segmentNodes.size: ${segmentNodes.size}")

                //if there is a segment apart from processedNodes
                if ( (processedNodes.size > 0) && (segmentNodes.size > 0) ) {
                    val processedClosestNode = segmentNodes.nearestNodesOrderedAsc(Node(position = processedNodes.averagePositionWithinNodes()))[0]
                    val segmentClosestNode = processedNodes.nearestNodesOrderedAsc(Node(position = segmentNodes.averagePositionWithinNodes()))[0]

                    if (processedClosestNode.position.dst(segmentClosestNode.position) < consolidateNodeDistance * 2) {
                        returnMesh.nodeLinks.add(NodeLink(processedClosestNode, segmentClosestNode))
                    }
                    else {
                        val bridgeLine = NodeLine(firstNode = processedClosestNode, lastNode = segmentClosestNode, lineNoise = 60)

//                        println("bridgeLine.size: ${bridgeLine.nodes.size}")

                        returnMesh.nodes.addAll(bridgeLine.nodes)
                        returnMesh.nodeLinks.addAll(bridgeLine.nodeLinks)
                    }
                }

                processedNodes.addAll(segmentNodes)
            }

            return returnMesh
        }
    }
}