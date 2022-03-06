package org.river.exertion.geom.node

import com.badlogic.gdx.math.Vector3
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
import org.river.exertion.geom.Line3
import org.river.exertion.geom.node.NodeLink3.Companion.addNodeLink
import org.river.exertion.geom.node.NodeLink3.Companion.getNodeChildrenUuids
import org.river.exertion.geom.node.NodeLink3.Companion.getNodeLinks
import org.river.exertion.geom.node.nodeMesh.INodeMesh
import org.river.exertion.geom.node.nodeMesh.NodeLine
import org.river.exertion.geom.node.nodeMesh.NodeLine3
import org.river.exertion.geom.node.nodeMesh.NodeMesh
import java.util.*
import kotlin.random.Random

class Node3(val uuid: UUID = UUID.randomUUID(), val position : Vector3, val description : String = "Node${Random.nextInt(2048)}"
            , var attributes : NodeAttributes = NodeAttributes() ) {

    constructor(initNodeType : NodeAttributes.NodeType) : this() {
        attributes.nodeType = initNodeType
    }

    constructor(leaf : ILeaf, description : String = "${Node3::class.simpleName}${Random.nextInt(2048)}") : this (
        uuid = leaf.uuid
        , position = Vector3(leaf.position.x, leaf.position.y, 0f)
        , description = description
    )

    constructor(copyNode : Node3
        , updUuid : UUID = copyNode.uuid
        , updPosition : Vector3 = copyNode.position
        , updDescription : String = copyNode.description) : this (
        uuid = updUuid
        , position = updPosition
        , description = updDescription
    )

    constructor() : this(position = Vector3(0f,0f, 0f))

    fun nearestCentroid(centroids : MutableSet<Node3>) : Node3 {
        return if ( centroids.isNotEmpty() ) centroids.nearestNodesOrderedAsc(this)[0] else this
    }

    override fun equals(other: Any?): Boolean {
        return (this.uuid == (other as Node3).uuid)
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString() = "${Node3::class.simpleName}(${description}_$uuid) : $position"

    fun getNodeChildren(nodes : MutableSet<Node3>, nodeLinks : MutableSet<NodeLink>) = nodeLinks.getNodeChildrenUuids(this.uuid, this.uuid).mapNotNull { nodes.getNode(it) }.toMutableSet()

    companion object {


        fun Node3.angleBetween(secondNode : Node3) : Angle {
            //placeholder
//            return Pair(this.position, secondNode.position).angleBetween().first()
            return 0f
        }

        fun MutableSet<Node3>.getNode(uuid : UUID) : Node3? {
            return this.firstOrNull { node -> node.uuid == uuid }
        }

        fun MutableSet<Node3>.addNode(nodeToAdd : Node3) : Boolean {
            return this.add( nodeToAdd )
        }

        fun MutableSet<Node3>.addNodes(nodesToAdd : MutableSet<Node3>, nodeDescription : String) : Unit = nodesToAdd.forEach { nodeToAdd -> this.add(
            Node3(nodeToAdd, updDescription = nodeDescription)
        ) }

        fun MutableSet<Node3>.removeNode(nodeLinks : MutableSet<NodeLink3>, uuid : UUID) : Boolean {
            nodeLinks.getNodeLinks(uuid).let { nodeLinks.removeAll(it) }
            return this.remove( getNode(uuid) )
        }

        //used to update position, attributes, etc; UUID remains the same
        fun MutableSet<Node3>.updateNode(node : Node3) {
            this.associateBy { entry -> entry.uuid }.toMutableMap()[node.uuid] = node
        }

        fun MutableSet<Node3>.getLineSet(nodeLinks : MutableSet<NodeLink>) : MutableSet<Line3> {

            val returnNodeLineSet : MutableSet<Line3> = mutableSetOf()

//            this.forEach { println("node : $it") }

            nodeLinks.forEach { nodeLink ->
//                println("nodeLink : $nodeLink")
                val firstNode = this.getNode(nodeLink.firstNodeUuid)
                val secondNode = this.getNode(nodeLink.secondNodeUuid)

                if ( (firstNode != null) && (secondNode != null) )
                    returnNodeLineSet.add(Line3(firstNode.position, secondNode.position) ) }

            return returnNodeLineSet
        }

        fun MutableSet<Node3>.averagePositionWithinNodes() : Vector3 {
            val averageX = this.map {node -> node.position.x.toInt()}.average()
            val averageY = this.map {node -> node.position.y.toInt()}.average()
            val averageZ = this.map {node -> node.position.z.toInt()}.average()

            return Vector3(averageX.toFloat(), averageY.toFloat(), averageZ.toFloat())
        }

        fun MutableSet<Node3>.nearestNodesOrderedAsc(refNode : Node3) : MutableList<Node3> {

            val nodeDistMap = mutableMapOf<Node3, Double>()

            this.forEach { node ->
                val nodeToRefDistance = node.position.dst(refNode.position)

                nodeDistMap[node] = nodeToRefDistance.toDouble()
            }

//            println("nearest nodes found for $refNode")

            return nodeDistMap.toList().sortedBy { (_, dist) -> dist}.toMap().keys.toMutableList()
        }

        fun MutableSet<Node3>.getFarthestNode(refNode : Node3 = Node3(position = this.averagePositionWithinNodes())) : Node3 {

            val nearestNodes = this.nearestNodesOrderedAsc(refNode)

            return if (nearestNodes.size > 1) nearestNodes[nearestNodes.size - 1] else refNode
        }

        fun MutableSet<Node3>.getRandomNode() : Node3 = if (this.isNotEmpty()) this.toList()[Random.nextInt(this.size)] else Node3()

        fun MutableSet<Node3>.getRandomUnoccupiedNode() : Node3 = if (this.isNotEmpty()) { val nodeList = this.filter { !it.attributes.occupied }.toList() ; nodeList[Random.nextInt(nodeList.size)] } else Node3()

    }
}