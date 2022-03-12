package org.river.exertion.geom.node.nodeMesh

import com.badlogic.gdx.math.Vector3
import ktx.math.compareTo
import ktx.math.plus
import org.river.exertion.Point
import org.river.exertion.Probability
import org.river.exertion.geom.Line3
import org.river.exertion.geom.Line3.Companion.anglesBetween
import org.river.exertion.geom.Line3.Companion.applyNoise
import org.river.exertion.geom.Line3.Companion.getPositionByDistanceAndAngles
import org.river.exertion.geom.node.Node3
import org.river.exertion.geom.node.Node3.Companion.addNode
import org.river.exertion.geom.node.NodeLink
import org.river.exertion.geom.node.NodeLink3
import org.river.exertion.geom.node.NodeLink3.Companion.addNodeLink
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random

class NodeLine3(override val uuid: UUID = UUID.randomUUID(), override val description: String = "nodeLine${Random.nextInt(256)}"
                , override var nodes : MutableSet<Node3> = mutableSetOf(), override var nodeLinks : MutableSet<NodeLink3> = mutableSetOf()
                , var nodeOrder : MutableList<UUID> = mutableListOf()) :
    INodeMesh3 {

    var lineNoise = Vector3(0f, 0f, 0f)

    //build constructor
    constructor(description: String = "nodeLine${Random.nextInt(256)}"
                , firstNode : Node3, lastNode : Node3, lineNoise : Vector3) : this (
    ) {
        val workNodeLine = Pair(firstNode, lastNode).buildNodeLine(lineNoise, description)

        nodes = workNodeLine.nodes
        nodeLinks = workNodeLine.nodeLinks
        nodeOrder = workNodeLine.nodeOrder
        this.lineNoise = lineNoise
    }

    //copy constructor
    constructor(copyNodeLine : NodeLine3
                , updUuid: UUID = copyNodeLine.uuid
                , updDescription: String = copyNodeLine.description) : this (
        uuid = updUuid
        , description = updDescription
    ) {
        nodes = mutableSetOf<Node3>().apply { addAll(copyNodeLine.nodes) }
        nodeLinks = mutableSetOf<NodeLink3>().apply { addAll(copyNodeLine.nodeLinks) }
        nodeOrder = mutableListOf<UUID>().apply { addAll(copyNodeLine.nodeOrder) }
    }

    fun getLines() : MutableList<Line3> {

        val returnList = mutableListOf<Line3>()

        nodeOrder.forEachIndexed { idx, uuid ->
            if (idx > 0) {
                returnList.add(Line3(
                    first = nodes.filter { it.uuid == nodeOrder[idx - 1] }.first().position,
                    second = nodes.filter { it.uuid == uuid }.first().position
                ) )
            }
        }

        return returnList
    }

    fun getPositions() : MutableList<Vector3> {

        val returnList = mutableListOf<Vector3>()

        nodeOrder.forEachIndexed { idx, uuid ->
            if (idx > 0) {
                returnList.add(nodes.filter { it.uuid == nodeOrder[idx - 1] }.first().position)
            }
        }

        return returnList
    }

    fun getPositionVertices() : FloatArray {

        val returnFloatArray = FloatArray(this.nodes.size * 8)
        var arrayIndex = 0

        this.getPositions().forEach {
            returnFloatArray[arrayIndex++] = it.x
            returnFloatArray[arrayIndex++] = it.y
            returnFloatArray[arrayIndex++] = it.z
            returnFloatArray[arrayIndex++] = 0f
            returnFloatArray[arrayIndex++] = 0f
            returnFloatArray[arrayIndex++] = 1f //normal to z
//            returnFloatArray[arrayIndex++] = 0.5f //texturex
//            returnFloatArray[arrayIndex++] = 0.5f //texturey
        }

        return returnFloatArray
    }

    fun getLineLength() = nodes.getLineLength()

    override fun toString() = "${NodeLine3::class.simpleName}(${uuid}) : $description, $nodes, $nodeLinks"

    companion object {

        fun MutableSet<Node3>.getLineLength() : Float {
            val xMin = this.minOf { it.position.x }
            val xMax = this.maxOf { it.position.x }
            val yMin = this.minOf { it.position.y }
            val yMax = this.maxOf { it.position.y }
            val zMin = this.minOf { it.position.z }
            val zMax = this.maxOf { it.position.z }

            return Vector3.dst(xMin, yMin, zMin, xMax, yMax, zMax)
        }

        //noise.x goes from 0 to 100, noise.y is plane compression, 0 to 90, noise.z is noise plane rotation, 0 to 90
        //does node / node need to be "?" ?
        fun Pair<Node3, Node3>.buildNodeLine(noise : Vector3 = Vector3(0f, 0f, 0f), nodeLineDescription : String = this.first.description, linkDistance : Float = NodeLink.consolidateNodeDistance + 1) : NodeLine3 {

            if (this.first.position == this.second.position) return NodeLine3(description = nodeLineDescription, nodes = mutableSetOf(this.first))

            val nodeLineSet = mutableSetOf<Node3>()
            val nodeLineLinkSet = mutableSetOf<NodeLink3>()
            val nodeLineOrder = mutableListOf<UUID>()

            val startNode = this.first
            val endNode = this.second
            val lineLength = mutableSetOf(this.first, this.second).getLineLength()

            val cappedNoise = if (noise.x < 0) 0 else if (noise.x > 100) 100 else noise.x.toInt()

//            println ("nodeLine start: $startNode step $linkDistance")

            nodeLineSet.add( Node3(copyNode = startNode, updDescription = nodeLineDescription) )
            nodeLineOrder.add(startNode.uuid)

            var previousNode = startNode
            var currentNode = startNode

            var previousPosition: Vector3
            var currentPosition: Vector3 = currentNode.position
            var currentNoisePosition : Vector3
            var currentPositionNoiseCircleRadius : Float
            var currentPositionNoiseCircleAngle : Float

            val anglesBetween = Pair(this.first.position, this.second.position).anglesBetween()
            val azimuth = anglesBetween.first
            val polar = anglesBetween.second

            var currentLineLength = mutableSetOf(this.first, Node3(position = currentPosition)).getLineLength()

            while ( currentLineLength < lineLength ) {
                previousNode = currentNode
                previousPosition = currentPosition

                //current position in 1d-middle of line
                currentPosition += Vector3(1f, azimuth, polar).getPositionByDistanceAndAngles()

                //get offset distance due to noise
                currentPositionNoiseCircleRadius = Probability(0F, (cappedNoise / 100f * 1f)).getValue().absoluteValue
                currentPositionNoiseCircleAngle = Probability(180F, 180F).getValue()

                //find noise position
                currentNoisePosition = Pair(previousPosition, currentPosition).applyNoise(currentPositionNoiseCircleRadius, Vector3(currentPositionNoiseCircleAngle, noise.y, noise.z))

                currentNode = Node3(position = currentNoisePosition)

                nodeLineSet.add(Node3(copyNode = currentNode, updDescription = nodeLineDescription))
                nodeLineOrder.add(currentNode.uuid)

                nodeLineLinkSet.addNodeLink(nodeLineSet, previousNode.uuid, currentNode.uuid)
                currentLineLength = mutableSetOf(this.first, Node3(position = currentPosition)).getLineLength()
            }

            nodeLineSet.addNode( Node3(copyNode = endNode, updDescription = nodeLineDescription) )
            nodeLineOrder.add(endNode.uuid)

            nodeLineLinkSet.addNodeLink(nodeLineSet, previousNode.uuid, endNode.uuid)

            //           println ("nodeLine end: $endNode, ${nodeLineList.size} nodes")
            //           nodeLineLinkList.forEach{ println("nodeLine link: $it") }

            return NodeLine3(description = "path${Random.nextInt(256)}", nodes = nodeLineSet, nodeLinks = nodeLineLinkSet, nodeOrder = nodeLineOrder)
        }

    }
}