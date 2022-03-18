package org.river.exertion.geom.node.nodeMesh

import org.river.exertion.Probability
import org.river.exertion.Point
import org.river.exertion.geom.Line
import org.river.exertion.geom.Line.Companion.isQ1
import org.river.exertion.geom.Line.Companion.isQ2
import org.river.exertion.geom.Line.Companion.isQ3
import org.river.exertion.geom.Line.Companion.isQ4
import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.Node.Companion.addNode
import org.river.exertion.geom.node.NodeLink
import org.river.exertion.geom.node.NodeLink.Companion.addNodeLink
import org.river.exertion.geom.node.NodeLink.Companion.removeOrphanLinks
import org.river.exertion.radians
import java.util.*
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

class NodeLine(override val uuid: UUID = UUID.randomUUID(), override val description: String = "nodeLine${Random.nextInt(256)}"
               , override var nodes : MutableSet<Node> = mutableSetOf(), override var nodeLinks : MutableSet<NodeLink> = mutableSetOf()
                , var nodeOrder : MutableList<UUID> = mutableListOf()) :
    INodeMesh {

    var lineNoise : Int = 0

    //build constructor
    constructor(description: String = "nodeLine${Random.nextInt(256)}"
                , firstNode : Node, lastNode : Node, lineNoise : Int) : this (
    ) {
        val workNodeLine = Pair(firstNode, lastNode).buildNodeLine(lineNoise, description)

        nodes = workNodeLine.nodes
        nodeLinks = workNodeLine.nodeLinks
        nodeOrder = workNodeLine.nodeOrder
        this.lineNoise = lineNoise
    }

    //copy constructor
    constructor(copyNodeLine : NodeLine
                , updUuid: UUID = copyNodeLine.uuid
                , updDescription: String = copyNodeLine.description) : this (
        uuid = updUuid
        , description = updDescription
    ) {
        nodes = mutableSetOf<Node>().apply { addAll(copyNodeLine.nodes) }
        nodeLinks = mutableSetOf<NodeLink>().apply { addAll(copyNodeLine.nodeLinks) }
        nodeOrder = mutableListOf<UUID>().apply { addAll(copyNodeLine.nodeOrder) }
    }

    fun getLineLength() = nodes.getLineLength()

    operator fun plus(secondLine : NodeLine) : NodeLine {
        val workNodeLine = this

        val workNodes = mutableSetOf<Node>().apply { addAll(workNodeLine.nodes); addAll(secondLine.nodes) }
        val workNodeLinks = mutableSetOf<NodeLink>().apply { addAll(workNodeLine.nodeLinks); addAll(secondLine.nodeLinks) }

        return NodeLine(description ="${workNodeLine.description} + ${secondLine.description}", nodes = workNodes, nodeLinks = workNodeLinks)
    }

    operator fun minus(secondLine : NodeLine) : NodeLine {
        val workNodeLine = this

        val workNodes = mutableSetOf<Node>().apply { addAll(workNodeLine.nodes); removeAll(secondLine.nodes) }
        val workNodeLinks = mutableSetOf<NodeLink>().apply { addAll(workNodeLine.nodeLinks); this.removeOrphanLinks(workNodes) }

        return NodeLine(description ="${workNodeLine.description} + ${secondLine.description}", nodes = workNodes, nodeLinks = workNodeLinks).apply { consolidateStackedNodes() }
    }

    override fun toString() = "${NodeLine::class.simpleName}(${uuid}) : $description, $nodes, $nodeLinks"

    companion object {

        fun MutableSet<Node>.getLineLength() : Float {
            val xMin = this.minOf { it.position.x }
            val xMax = this.maxOf { it.position.x }
            val yMin = this.minOf { it.position.y }
            val yMax = this.maxOf { it.position.y }

            return hypot(xMax - xMin, yMax - yMin)
        }

        //noise goes from 0 to 100
        //does node / node need to be "?" ?
        fun Pair<Node, Node>.buildNodeLine(noise : Int = 0, nodeLineDescription : String = this.first.description, linkDistance : Float = NodeLink.consolidateNodeDistance + 1) : NodeLine {

            if (this.first.position == this.second.position) return NodeLine(description = nodeLineDescription, nodes = mutableSetOf(this.first))

            val nodeLineSet = mutableSetOf<Node>()
            val nodeLineLinkSet = mutableSetOf<NodeLink>()
            val nodeLineOrder = mutableListOf<UUID>()

            val startNode = this.first
            val endNode = this.second
            val lineLength = mutableSetOf(this.first, this.second).getLineLength()

            val cappedNoise = if (noise < 0) 0 else if (noise > 100) 100 else noise

//            println ("nodeLine start: $startNode step $linkDistance")

            nodeLineSet.addNode( Node(copyNode = startNode, updDescription = nodeLineDescription) )
            nodeLineOrder.add(startNode.uuid)

            var previousNode = startNode
            var currentNode = startNode

            var currentPosition : Point = currentNode.position
            var currentNoisePosition : Point
            var currentPositionOffsetDistance : Float

//            println("noise: $noise, node1Pos:${startNode.position}, node2Pos:${endNode.position}, lineLength: $lineLength")

            when {
                Line(startNode.position, endNode.position).isQ4() -> {
                    val angle = atan((startNode.position.y - endNode.position.y) / (endNode.position.x - startNode.position.x))

                    //current position in middle of line
                    currentPosition = Point(currentPosition.x + linkDistance * cos(angle), currentPosition.y - linkDistance * sin(angle))

                    //get offset distance due to noise
                    currentPositionOffsetDistance = Probability(0F, (cappedNoise / 100 * linkDistance) ).getValue()

                    //find noise position at right angle of line angle
                    currentNoisePosition = Point ( currentPosition.x - currentPositionOffsetDistance * cos(90F.radians() - angle)
                        , currentPosition.y - currentPositionOffsetDistance * sin(90F.radians() - angle)
                    )

                    currentNode = Node(position = currentNoisePosition)
//                    println("1) node at ${currentNode}, angle ${angle.degrees}")

                    var currentLineLength = mutableSetOf(this.first, currentNode).getLineLength()
//                    println("currentLineLength = $currentLineLength")

                    while ( currentLineLength < lineLength ) {
                        nodeLineSet.addNode( Node(copyNode = currentNode, updDescription = nodeLineDescription) )
                        nodeLineOrder.add(currentNode.uuid)

                        nodeLineLinkSet.addNodeLink(nodeLineSet, previousNode.uuid, currentNode.uuid)
                        previousNode = currentNode

                        //current position in middle of line
                        currentPosition = Point(currentPosition.x + linkDistance * cos(angle), currentPosition.y - linkDistance * sin(angle))

                        //get offset distance due to noise
                        currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                        //find cappedNoise position at right angle of line angle
                        currentNoisePosition = Point ( currentPosition.x - currentPositionOffsetDistance * cos(90F.radians() - angle)
                            , currentPosition.y - currentPositionOffsetDistance * sin(90F.radians() - angle)
                        )

                        currentNode = Node(position = currentNoisePosition)
//                       println("1) node at ${currentNode}, angle ${angle.degrees}")

                        currentLineLength = mutableSetOf(this.first, currentNode).getLineLength()
//                        println("currentLineLength = $currentLineLength")
                    }
                }
                Line(startNode.position, endNode.position).isQ3() -> {
                    val angle = atan((startNode.position.y - endNode.position.y) / (startNode.position.x - endNode.position.x))

                    //current position in middle of line
                    currentPosition = Point(currentPosition.x - linkDistance * cos(angle), currentPosition.y - linkDistance * sin(angle))

                    //get offset distance due to noise
                    currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                    //find noise position at right angle of line angle
                    currentNoisePosition = Point ( currentPosition.x - currentPositionOffsetDistance * cos(90F.radians() - angle)
                        , currentPosition.y + currentPositionOffsetDistance * sin(90F.radians() - angle) )

                    currentNode = Node( position = currentNoisePosition )
                    //                   println("2) node at ${currentNode}, angle ${angle.degrees}")

                    var currentLineLength = mutableSetOf(this.first, currentNode).getLineLength()
 //                   println("currentLineLength = $currentLineLength")

                    while ( currentLineLength < lineLength ) {
                        nodeLineSet.addNode( Node(copyNode = currentNode, updDescription = nodeLineDescription))
                        nodeLineOrder.add(currentNode.uuid)

                        nodeLineLinkSet.addNodeLink(nodeLineSet, previousNode.uuid, currentNode.uuid)
                        previousNode = currentNode

                        //current position in middle of line
                        currentPosition = Point(currentPosition.x - linkDistance * cos(angle), currentPosition.y - linkDistance * sin(angle))

                        //get offset distance due to noise
                        currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                        //find noise position at right angle of line angle
                        currentNoisePosition = Point ( currentPosition.x - currentPositionOffsetDistance * cos(90F.radians() - angle)
                            , currentPosition.y + currentPositionOffsetDistance * sin(90F.radians() - angle)
                        )

                        currentNode = Node( position = currentNoisePosition )
                        //                       println("2) node at ${currentNode}, angle ${angle.degrees}")

                        currentLineLength = mutableSetOf(this.first, currentNode).getLineLength()
 //                       println("currentLineLength = $currentLineLength")
                    }
                }
                Line(startNode.position, endNode.position).isQ2() -> {
                    val angle = atan((endNode.position.y - startNode.position.y) / (startNode.position.x - endNode.position.x))

                    //current position in middle of line
                    currentPosition = Point(currentPosition.x - linkDistance * cos(angle), currentPosition.y + linkDistance * sin(angle))

                    //get offset distance due to noise
                    currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                    //find noise position at right angle of line angle
                    currentNoisePosition = Point ( currentPosition.x + currentPositionOffsetDistance * cos(90F.radians() - angle)
                        , currentPosition.y + currentPositionOffsetDistance * sin(90F.radians() - angle)
                    )

                    currentNode = Node( position = currentNoisePosition )
                    //                  println("3) node at ${currentNode}, angle ${angle.degrees}")

                    var currentLineLength = mutableSetOf(this.first, currentNode).getLineLength()
 //                   println("currentLineLength = $currentLineLength")

                    while ( currentLineLength < lineLength ) {
                        nodeLineSet.addNode( Node(copyNode = currentNode, updDescription = nodeLineDescription) )
                        nodeLineOrder.add(currentNode.uuid)

                        nodeLineLinkSet.addNodeLink(nodeLineSet, previousNode.uuid, currentNode.uuid)
                        previousNode = currentNode

                        //current position in middle of line
                        currentPosition = Point(currentPosition.x - linkDistance * cos(angle), currentPosition.y + linkDistance * sin(angle))

                        //get offset distance due to noise
                        currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                        //find noise position at right angle of line angle
                        currentNoisePosition = Point ( currentPosition.x + currentPositionOffsetDistance * cos(90F.radians() - angle)
                            , currentPosition.y + currentPositionOffsetDistance * sin(90F.radians() - angle)
                        )

                        currentNode = Node(position = currentNoisePosition )
                        //                    println("3) node at ${currentNode}, angle ${angle.degrees}")

                        currentLineLength = mutableSetOf(this.first, currentNode).getLineLength()
 //                       println("currentLineLength = $currentLineLength")
                    }
                }
                Line(startNode.position, endNode.position).isQ1() -> {
                    val angle = atan((endNode.position.y - startNode.position.y) / (endNode.position.x - startNode.position.x))

                    //current position in middle of line
                    currentPosition = Point(currentPosition.x + linkDistance * cos(angle), currentPosition.y + linkDistance * sin(angle))

                    //get offset distance due to noise
                    currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                    //find noise position at right angle of line angle
                    currentNoisePosition = Point ( currentPosition.x + currentPositionOffsetDistance * cos(90F.radians() - angle)
                        , currentPosition.y - currentPositionOffsetDistance * sin(90F.radians() - angle)
                    )

                    currentNode = Node(position = currentNoisePosition)
                    //                  println("4) node at ${currentNode}, angle ${angle.degrees}")

                    var currentLineLength = mutableSetOf(this.first, currentNode).getLineLength()
 //                   println("currentLineLength = $currentLineLength")

                    while ( currentLineLength < lineLength ) {
                        nodeLineSet.addNode( Node(copyNode = currentNode, updDescription = nodeLineDescription) )
                        nodeLineOrder.add(currentNode.uuid)

                        nodeLineLinkSet.addNodeLink(nodeLineSet, previousNode.uuid, currentNode.uuid)
                        previousNode = currentNode

                        //current position in middle of line
                        currentPosition = Point(currentPosition.x + linkDistance * cos(angle), currentPosition.y + linkDistance * sin(angle))

                        //get offset distance due to noise
                        currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                        //find noise position at right angle of line angle
                        currentNoisePosition = Point ( currentPosition.x + currentPositionOffsetDistance * cos(90F.radians() - angle)
                            , currentPosition.y - currentPositionOffsetDistance * sin(90F.radians() - angle)
                        )

                        currentNode = Node(position = currentNoisePosition)
                        //                   println("4) node at ${currentNode}, angle ${angle.degrees}")

                        currentLineLength = mutableSetOf(this.first, currentNode).getLineLength()
  //                      println("currentLineLength = $currentLineLength")
                    }
                }
            }

            nodeLineSet.addNode( Node(copyNode = endNode, updDescription = nodeLineDescription) )
            nodeLineOrder.add(endNode.uuid)

            nodeLineLinkSet.addNodeLink(nodeLineSet, previousNode.uuid, endNode.uuid)

            //           println ("nodeLine end: $endNode, ${nodeLineList.size} nodes")
            //           nodeLineLinkList.forEach{ println("nodeLine link: $it") }

            return NodeLine(description = "path${Random.nextInt(256)}", nodes = nodeLineSet, nodeLinks = nodeLineLinkSet, nodeOrder = nodeLineOrder)
        }

    }
}