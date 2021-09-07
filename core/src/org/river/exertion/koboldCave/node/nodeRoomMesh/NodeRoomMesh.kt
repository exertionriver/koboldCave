package org.river.exertion.koboldCave.node.nodeRoomMesh

import org.river.exertion.Angle
import org.river.exertion.Point
import org.river.exertion.koboldCave.Line
import org.river.exertion.koboldCave.Line.Companion.borderLines
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.Line.Companion.isInBorder
import org.river.exertion.koboldCave.Line.Companion.points
import org.river.exertion.koboldCave.Probability
import org.river.exertion.koboldCave.ProbabilitySelect
import org.river.exertion.koboldCave.leaf.ILeaf
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.Node.Companion.nearestNodesOrderedAsc
import org.river.exertion.koboldCave.node.Node.Companion.updateNode
import org.river.exertion.koboldCave.node.NodeAttributes
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.addNodeLink
import org.river.exertion.koboldCave.node.nodeMesh.NodeMesh
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom.Companion.buildFloors
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom.Companion.buildWalls
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom.Companion.buildWallsLos
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoomLink
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWalls
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsLos
import org.river.exertion.plus
import org.river.exertion.trunc
import java.util.*
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeRoomMesh(override val uuid: UUID = UUID.randomUUID(), override val description: String = "nodeRoomMesh${Random.nextInt(256)}"
                   , override var nodeRooms : MutableList<NodeRoom> = mutableListOf(), override var nodeRoomLinks : MutableList<NodeRoomLink> = mutableListOf()
                   , var nodesMap : MutableMap<Node, UUID> = mutableMapOf(), var nodeLinks : MutableList<NodeLink> = mutableListOf() ) :
    INodeRoomMesh {

    val maxRoomExits = 18
    var currentRoomExits = 0

    val activatedExitNodes = mutableListOf<Node>()
    val exitNodes = mutableListOf<Node>()

    var currentWall : MutableMap<Point, Point> = mutableMapOf()
    var pastWall : MutableMap<Point, Point> = mutableMapOf()
    var currentWallFade : MutableMap<Point, Point> = mutableMapOf()
    var pastWallFade : MutableMap<Point, Point> = mutableMapOf()

    val floorNodes = mutableListOf<Node>()
    var currentFloor : MutableMap<Point, Point> = mutableMapOf()
    var pastFloor : MutableMap<Point, Point> = mutableMapOf()

    //build constructor
    constructor(nodeRoom : NodeRoom) : this (
    ) {
        this.nodeRooms.add(nodeRoom)

        nodeRoom.nodes.forEach { nodesMap[it] = nodeRoom.uuid }
        nodeRoom.nodeLinks.forEach { nodeLinks.add ( it ) }
        nodeRoom.getExitNodes().forEach { exitNodes.add ( it ) }
        currentRoomExits += nodeRoom.getExitNodes().size

        println("new NodeRoomMesh: ${this.nodeRooms.size}, ${this.nodeRoomLinks.size}")
    }

    //copy constructor
    constructor(copyNodeRoomMesh : NodeRoomMesh
                , updUuid: UUID = copyNodeRoomMesh.uuid
                , updDescription: String = copyNodeRoomMesh.description) : this (
        uuid = updUuid
        , description = updDescription
    ) {
        nodeRooms = mutableListOf<NodeRoom>().apply { addAll(copyNodeRoomMesh.nodeRooms) }
        nodeRoomLinks = mutableListOf<NodeRoomLink>().apply { addAll(copyNodeRoomMesh.nodeRoomLinks) }
    }

    operator fun plus(secondRoomMesh : NodeRoomMesh) : NodeRoomMesh {
        val workNodeRoomMesh = this

        val workNodeRooms = mutableListOf<NodeRoom>().apply { addAll(workNodeRoomMesh.nodeRooms); addAll(secondRoomMesh.nodeRooms) }
        val workNodeRoomLinks = mutableListOf<NodeRoomLink>().apply { addAll(workNodeRoomMesh.nodeRoomLinks); addAll(secondRoomMesh.nodeRoomLinks) }

        return NodeRoomMesh(description ="${workNodeRoomMesh.description} + ${secondRoomMesh.description}", nodeRooms = workNodeRooms, nodeRoomLinks = workNodeRoomLinks)//.apply { consolidateStackedNodes() }
    }

    operator fun minus(secondRoomMesh : NodeRoomMesh) : NodeRoomMesh {
        val workNodeRoomMesh = this

        val workNodeRooms = mutableListOf<NodeRoom>().apply { addAll(workNodeRoomMesh.nodeRooms); removeAll(secondRoomMesh.nodeRooms) }
        val workNodeRoomLinks = mutableListOf<NodeRoomLink>().apply { addAll(workNodeRoomMesh.nodeRoomLinks) } //; this.removeOrphanLinks(workNodes) }

        return NodeRoomMesh(description ="${workNodeRoomMesh.description} + ${secondRoomMesh.description}", nodeRooms = workNodeRooms, nodeRoomLinks = workNodeRoomLinks)

    }

    //called by the room being exited, with the exit node
    fun activateExitNode(nodeRoomIdx : Int, roomExitNode : Node) {

        this.activatedExitNodes.add(roomExitNode)

        val newNodeRoomAngle = this.nodeRooms[nodeRoomIdx].centroid.angleBetween(roomExitNode)
        val newNodeRoomPosition = roomExitNode.position.getPositionByDistanceAndAngle(NextDistancePx, newNodeRoomAngle)

        val newNodeRoom = NodeRoom(height = 3, centerPoint = newNodeRoomPosition, borderRooms = getAllRooms(), exitsAllowed = maxRoomExits - currentRoomExits)
        println("new NodeRoom created! node count: ${newNodeRoom.nodes.size}")

        if (newNodeRoom.nodes.size > 1) {

            this.nodeRooms.add(newNodeRoom)
            val newNodeRoomIdx = nodeRooms.size - 1

            newNodeRoom.nodes.forEach { nodesMap[it] = newNodeRoom.uuid }
            newNodeRoom.nodeLinks.forEach { nodeLinks.add ( it ) }
            newNodeRoom.getExitNodes().forEach { exitNodes.add ( it ) }
            currentRoomExits += newNodeRoom.getExitNodes().size

            println("new NodeRoomMesh: ${this.nodeRooms.size}, ${this.nodeRoomLinks.size}")

            //next, bridge between the two NodeRooms with NodeLink
            val nearestNewRoomNode = newNodeRoom.nodes.nearestNodesOrderedAsc(roomExitNode)[0]
            val newNodeLink = NodeLink(roomExitNode.uuid, nearestNewRoomNode.uuid)

            this.nodeLinks.add(newNodeLink)
            this.nodeRoomLinks.add(NodeRoomLink(this.uuid, newNodeRoom.uuid))
            this.nodeRooms[nodeRoomIdx].nodeLinks.addNodeLink(this.nodesMap.keys.toMutableList(), roomExitNode.uuid, nearestNewRoomNode.uuid )
            this.nodeRooms[newNodeRoomIdx].nodeLinks.addNodeLink(this.nodesMap.keys.toMutableList(), roomExitNode.uuid, nearestNewRoomNode.uuid )

            println("new NodeRoom linked!")

            nearestNewRoomNode.attributes.nodeType = NodeAttributes.NodeType.EXIT
            this.nodesMap[nearestNewRoomNode] = newNodeRoom.uuid
            this.activatedExitNodes.add(nearestNewRoomNode)
        }
    }

    override fun toString() = "${NodeRoomMesh::class.simpleName}(${uuid}) : $description, ${nodeRooms}, $nodeRoomLinks"

    fun getAllRooms() : NodeRoom = nodeRooms.reduce { allRooms, nodeRoom -> nodeRoom + allRooms }

    fun getInactivatedExitNodes() = exitNodes.filter { !activatedExitNodes.contains(it) }

    fun inactiveExitNodesInRange(currentNode : Node) = getInactivatedExitNodes().filter { it == currentNode || it.getNodeChildren(this.nodesMap.keys.toMutableList(), this.nodeLinks).contains(currentNode) }

    fun getCurrentRoomIdx(currentNode : Node) = nodeRooms.indexOfFirst { it.uuid == nodesMap[currentNode] }

    fun getSlope(firstNode : Node, secondNode : Node) : Float {
        val rise = secondNode.attributes.nodeElevation.getHeight() - firstNode.attributes.nodeElevation.getHeight()
        val run = firstNode.position.dst(secondNode.position)

        return rise / run * 100
    }

    companion object {

        //build all walls, fully lit
        fun NodeRoomMesh.buildWalls() {

            val newCurrentWall = mutableMapOf<Point, Point>()
            val newCurrentWallFade = mutableMapOf<Point, Point>()

            this.nodeRooms.forEach { nodeRoom ->

                nodeRoom.buildWalls()

                nodeRoom.currentWallFade.forEach { wallPoint ->
                    if (!newCurrentWallFade.keys.contains(wallPoint.key)) newCurrentWallFade[wallPoint.key] = wallPoint.value
                }

                nodeRoom.currentWall.forEach { wallPoint ->
                    if (!newCurrentWall.keys.contains(wallPoint.key)) newCurrentWall[wallPoint.key] = wallPoint.value
                }
            }

            newCurrentWall.entries.forEach { this.currentWall[it.key] = it.value }
            newCurrentWallFade.entries.forEach { this.currentWallFade[it.key] = it.value }
        }

        //line of sight
        fun NodeRoomMesh.buildWallsLos(refPosition : Point, refAngle : Angle, radius : Float = 0f) {

            val newCurrentWall = mutableMapOf<Point, Point>()
            val newCurrentWallFade = mutableMapOf<Point, Point>()

            val minBorderRegion = mutableListOf<Point>()
            val maxBorderRegion = mutableListOf<Point>()
            val fadeBorderRegion = mutableListOf<Point>()

            getAllRooms().getLineList().forEach { line ->

                val yMin = refPosition.y.toInt() - radius.toInt()
                val yMax = refPosition.y.toInt() + radius.toInt()

                (yMin..yMax).forEach { yIter ->
                    var xIter1 = refPosition.x.toInt()
                    var xIter2 = refPosition.x.toInt() + 1

                    while ( Math.pow(
                            (xIter1 - refPosition.x).toDouble(),
                            2.0
                        ) + Math.pow((yIter - refPosition.y).toDouble(), 2.0) <= Math.pow(radius.toDouble(), 2.0)
                    ) {
                        val checkPoint = Point(xIter1.toFloat(), yIter.toFloat())
                        if ( checkPoint.isInBorder(line, (NextDistancePx * 0.5).toInt()) ) fadeBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (NextDistancePx * 0.3).toInt()) ) maxBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (NextDistancePx * 0.2).toInt()) ) minBorderRegion.add(checkPoint)
                        xIter1--
                    }

                    while ( Math.pow(
                            (xIter2 - refPosition.x).toDouble(),
                            2.0
                        ) + Math.pow((yIter - refPosition.y).toDouble(), 2.0) <= Math.pow(radius.toDouble(), 2.0)
                    ) {
                        val checkPoint = Point(xIter2.toFloat(), yIter.toFloat())
                        if ( checkPoint.isInBorder(line, (NextDistancePx * 0.5).toInt()) ) fadeBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (NextDistancePx * 0.3).toInt()) ) maxBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (NextDistancePx * 0.2).toInt()) ) minBorderRegion.add(checkPoint)
                        xIter2++
                    }
                }
            }
            val aMin = 0
            val aMax = 359

            currentWall.entries.forEach { this.pastWall[it.key] = it.value }
            currentWallFade.entries.forEach { this.pastWallFade[it.key] = it.value }

            currentWall.entries.clear()
            currentWallFade.entries.clear()

            val processedPoints = mutableSetOf<Point>()

            (aMin..aMax).forEach { aIter ->
                //allow at most two pixels of fade per ray
                var fadeCounter = 1
                var rayLengthIter = 1f

                val checkRadius = if ( Math.abs(aIter - refAngle) < 30f || Math.abs(aIter - refAngle) > 330f ) radius else radius * .5f

//                println("aIter: $aIter")

                while (rayLengthIter <= checkRadius && fadeCounter > 0) {
                    val checkPoint = refPosition.getPositionByDistanceAndAngle(rayLengthIter, aIter.toFloat()).trunc()

                    if (!processedPoints.contains(checkPoint)) {
                        processedPoints.add(checkPoint)

//                        println("checkPoint: $checkPoint")

                        if ( fadeBorderRegion.contains(checkPoint) && !maxBorderRegion.contains(checkPoint) && !minBorderRegion.contains(checkPoint) ) {
                            if (pastWallFade.contains( checkPoint ) ) { currentWallFade[checkPoint] = pastWallFade[checkPoint]!! ; pastWallFade.remove(checkPoint) }
                            else newCurrentWallFade[checkPoint] = checkPoint + Point(Probability(mean = 0.5f, range = 0.3f).getValue(), Probability(mean = 0.5f, range = 0.3f).getValue())

                            fadeCounter--
                        }
                        if ( maxBorderRegion.contains(checkPoint) && !minBorderRegion.contains(checkPoint) )
                            if (pastWall.contains( checkPoint ) ) { currentWall[checkPoint] = pastWall[checkPoint]!! ; pastWall.remove(checkPoint) }
                            else newCurrentWall[checkPoint] = checkPoint + Point(Probability(mean = 1f, range = 0.5f).getValue(), Probability(mean = 1f, range = 0.5f).getValue())
                    }

                    rayLengthIter++
                }
            }

            newCurrentWall.entries.forEach { this.currentWall[it.key] = it.value }
            newCurrentWallFade.entries.forEach { this.currentWallFade[it.key] = it.value }
        }

        //line of sight
        fun NodeRoomMesh.buildFloorsLos(refNode : Node, forwardNode : Node, refAngle : Angle, radius : Float = 0f) {

            val nodesToBuild = mutableListOf<Node>()

            currentFloor.entries.forEach { this.pastFloor[it.key] = it.value }
            currentFloor.entries.clear()

            if (!floorNodes.contains(refNode) ) nodesToBuild.add(refNode)
            if (!floorNodes.contains(forwardNode) ) nodesToBuild.add(forwardNode)

            nodesToBuild.forEach { node ->

                //center circle first
                val radius = 6

                //https://stackoverflow.com/questions/40779343/java-loop-through-all-pixels-in-a-2d-circle-with-center-x-y-and-radius
                val yMin = node.position.y.toInt() - radius
                val yMax = node.position.y.toInt() + radius

                (yMin..yMax).forEach { yIter ->
                    var xIter1 = node.position.x.toInt()
                    var xIter2 = node.position.x.toInt() + 1

                    while ( Math.pow(
                            (xIter1 - node.position.x).toDouble(),
                            2.0
                        ) + Math.pow((yIter - node.position.y).toDouble(), 2.0) <= Math.pow(radius.toDouble(), 2.0)
                    ) {
                        val checkPoint = Point(xIter1.toFloat(), yIter.toFloat())

                        val isPoint = ProbabilitySelect(
                            mapOf(
                                true to Probability(node.attributes.nodeObstacle.getChallenge(), 0),
                                false to Probability(100 - node.attributes.nodeObstacle.getChallenge(), 0)
                            )
                        ).getSelectedProbability()!!

                        if (isPoint) {
                            if (!currentWall.keys.contains(checkPoint) && !currentWallFade.keys.contains(checkPoint))
                                pastFloor[checkPoint] = checkPoint + Point(Probability(mean = 0f, range = 0.75f).getValue(), Probability(mean = 0f, range = 0.75f).getValue())
                        }

                        xIter1--
                    }
                    while ( Math.pow(
                            (xIter2 - node.position.x).toDouble(),
                            2.0
                        ) + Math.pow((yIter - node.position.y).toDouble(), 2.0) <= Math.pow(radius.toDouble(), 2.0)
                    ) {
                        val checkPoint = Point(xIter2.toFloat(), yIter.toFloat())

                        val isPoint = ProbabilitySelect(
                            mapOf(
                                true to Probability(node.attributes.nodeObstacle.getChallenge(), 0),
                                false to Probability(100 - node.attributes.nodeObstacle.getChallenge(), 0)
                            )
                        ).getSelectedProbability()!!

                        if (isPoint) {
                            if (!currentWall.keys.contains(checkPoint) && !currentWallFade.keys.contains(checkPoint))
                                pastFloor[checkPoint] = checkPoint + Point(Probability(mean = 0f, range = 0.75f).getValue(), Probability(mean = 0f, range = 0.75f).getValue())
                        }

                        xIter2++
                    }
                }

                val childNodes = node.getNodeChildren(nodesMap.keys.toMutableList(), nodeLinks)
                val borderWidth = 4
                val nodeChallenge = node.attributes.nodeObstacle.getChallenge()

                childNodes.forEach { childNode ->
                    val beginCooridorPos = node.position.getPositionByDistanceAndAngle(borderWidth.toFloat() * 2, node.angleBetween(childNode))
                    val dstHalfCooridor = beginCooridorPos.dst(childNode.position) / 2 - borderWidth * 2
                    val halfCooridorPos = beginCooridorPos.getPositionByDistanceAndAngle(dstHalfCooridor, node.angleBetween(childNode))
                    val childNodeChallenge = childNode.attributes.nodeObstacle.getChallenge()

                    Line.pointsInBorder(Line(beginCooridorPos, halfCooridorPos), borderWidth).forEach { checkPoint ->
                        val dstGradientFromBegin = checkPoint.dst(beginCooridorPos) / dstHalfCooridor
                        val dstGradientFromHalf = checkPoint.dst(halfCooridorPos) / dstHalfCooridor
                        val avgChallenge = ( nodeChallenge + childNodeChallenge ) / 2

                        val dstChallenge = if (nodeChallenge != childNodeChallenge) nodeChallenge * dstGradientFromHalf + avgChallenge * dstGradientFromBegin else nodeChallenge.toFloat()

                        val isPoint = ProbabilitySelect(
                            mapOf(
                                true to Probability(dstChallenge, 0),
                                false to Probability(100 - dstChallenge, 0)
                            )
                        ).getSelectedProbability()!!

                        if (isPoint) {
                            if (!currentWall.keys.contains(checkPoint) && !currentWallFade.keys.contains(checkPoint))
                                pastFloor[checkPoint] = checkPoint + Point(Probability(mean = 0f, range = 0.75f).getValue(), Probability(mean = 0f, range = 0.75f).getValue())
                        }
                    }
                }
                //add to pastFloor in prep for LOS below
                floorNodes.add(node)
            }

            val aMin = 0
            val aMax = 359

            (aMin..aMax).forEach { aIter ->
                var rayLengthIter = 1f

                val checkRadius = if ( Math.abs(aIter - refAngle) < 30f || Math.abs(aIter - refAngle) > 330f ) radius else radius * .5f

//                println("aIter: $aIter")

                while (rayLengthIter <= checkRadius) {
                    val checkPoint = refNode.position.getPositionByDistanceAndAngle(rayLengthIter, aIter.toFloat()).trunc()

                    if (pastFloor.contains( checkPoint ) ) { currentFloor[checkPoint] = pastFloor[checkPoint]!! ; pastFloor.remove(checkPoint) }

                    rayLengthIter++
                }
            }
            println ("currentFloor size: ${currentFloor.size}")
            println ("pastFloor size: ${pastFloor.size}")
        }
    }
}