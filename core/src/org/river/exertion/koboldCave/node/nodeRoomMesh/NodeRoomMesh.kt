package org.river.exertion.koboldCave.node.nodeRoomMesh

import org.river.exertion.*
import org.river.exertion.koboldCave.Line
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.Line.Companion.pointsInBorder
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.Node.Companion.getNode
import org.river.exertion.koboldCave.node.Node.Companion.nearestNodesOrderedAsc
import org.river.exertion.koboldCave.node.NodeAttributes
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.addNodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.getLineList
import org.river.exertion.koboldCave.node.NodeLink.Companion.removeNodeLink
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoomLink
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

class NodeRoomMesh(override val uuid: UUID = UUID.randomUUID(), override val description: String = "nodeRoomMesh${Random.nextInt(256)}"
                   , override var nodeRooms : MutableList<NodeRoom> = mutableListOf(), override var nodeRoomLinks : MutableList<NodeRoomLink> = mutableListOf()
                   , var nodesMap : MutableMap<Node, UUID> = mutableMapOf(), var nodeLinks : MutableList<NodeLink> = mutableListOf() ) :
    INodeRoomMesh {

    val maxRoomExits = 18
    var currentRoomExits = 0

    val activatedExitNodes = mutableListOf<Node>()
    val exitNodes = mutableListOf<Node>()

    var builtPath : MutableMap<Point, Point> = mutableMapOf()
    var currentPath : MutableMap<Point, Point> = mutableMapOf()
    var pastPath : MutableMap<Point, Point> = mutableMapOf()
    var obstaclePath : MutableMap<Point, Float> = mutableMapOf()
    var elevationPath : MutableMap<Point, Float> = mutableMapOf()

    var builtWall : MutableMap<Point, Point> = mutableMapOf()
    var currentWall : MutableMap<Point, Point> = mutableMapOf()
    var pastWall : MutableMap<Point, Point> = mutableMapOf()

    var builtWallFade : MutableMap<Point, Point> = mutableMapOf()
    var currentWallFade : MutableMap<Point, Point> = mutableMapOf()
    var pastWallFade : MutableMap<Point, Point> = mutableMapOf()

    val renderedNodeLinks = mutableSetOf<NodeLink>()

    val renderedNodes = mutableSetOf<Node>()
    var currentFloor : MutableMap<Point, Point> = mutableMapOf()
    var pastFloor : MutableMap<Point, Point> = mutableMapOf()
    var currentStairs : MutableMap<Point, Angle> = mutableMapOf()
    var pastStairs : MutableMap<Point, Angle> = mutableMapOf()

    //build constructor
    constructor(nodeRoom : NodeRoom) : this (
    ) {
        this.nodeRooms.add(nodeRoom)

        nodeRoom.nodes.forEach { nodesMap[it] = nodeRoom.uuid }
        nodeRoom.nodeLinks.forEach { nodeLinks.add ( it ) }
        nodeRoom.getExitNodes().forEach { exitNodes.add ( it ) }
        currentRoomExits += nodeRoom.getExitNodes().size

//        println("new NodeRoomMesh: ${this.nodeRooms.size}, ${this.nodeRoomLinks.size}")
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
//        println("new NodeRoom created! node count: ${newNodeRoom.nodes.size}")

        if (newNodeRoom.nodes.size > 1) {

            this.nodeRooms.add(newNodeRoom)
            val newNodeRoomIdx = nodeRooms.size - 1

            newNodeRoom.nodes.forEach { nodesMap[it] = newNodeRoom.uuid }
            newNodeRoom.nodeLinks.forEach { nodeLinks.add ( it ) }
            newNodeRoom.getExitNodes().forEach { exitNodes.add ( it ) }
            currentRoomExits += newNodeRoom.getExitNodes().size

 //           println("new NodeRoomMesh: ${this.nodeRooms.size}, ${this.nodeRoomLinks.size}")

            //next, bridge between the two NodeRooms with NodeLink
            val nearestNewRoomNode = newNodeRoom.nodes.nearestNodesOrderedAsc(roomExitNode)[0]

            this.nodeLinks.addNodeLink(this.nodesMap.keys.toMutableList(), roomExitNode.uuid, nearestNewRoomNode.uuid)
            this.nodeRoomLinks.add(NodeRoomLink(this.uuid, newNodeRoom.uuid))
            this.nodeRooms[nodeRoomIdx].nodeLinks.addNodeLink(this.nodesMap.keys.toMutableList(), roomExitNode.uuid, nearestNewRoomNode.uuid )
            this.nodeRooms[newNodeRoomIdx].nodeLinks.addNodeLink(this.nodesMap.keys.toMutableList(), roomExitNode.uuid, nearestNewRoomNode.uuid )

 //           println("new NodeRoom linked!")

            nearestNewRoomNode.attributes.nodeType = NodeAttributes.NodeType.EXIT
            this.nodesMap[nearestNewRoomNode] = newNodeRoom.uuid
            this.activatedExitNodes.add(nearestNewRoomNode)

            //re-build adjacent node walls
            renderedNodeLinks.toMutableList().removeNodeLink(this.nodeRooms[nodeRoomIdx].uuid, roomExitNode.uuid)
            renderedNodeLinks.toMutableList().removeNodeLink(roomExitNode.uuid, nearestNewRoomNode.uuid)

//            println ("activating node ${roomExitNode.uuid} from ${this.nodeRooms[nodeRoomIdx].uuid}" )
//            println ("removing rendered link between ${this.nodeRooms[nodeRoomIdx].uuid} and ${roomExitNode.uuid}")
//            println ("removing rendered link between ${roomExitNode.uuid} and ${nearestNewRoomNode.uuid}")
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
        //build walking path, fully lit
        fun NodeRoomMesh.buildAndRenderSimplePath() {

            this.nodeLinks.getLineList(this.nodesMap.keys.toMutableList()).forEach { line ->

                line.pointsInBorder(1).forEach { pathPoint -> this.currentWall[pathPoint] = pathPoint }
            }
        }

        fun Pair<Node, Node>.getPointChallenge(refPoint : Point) : Float {
            val totalDst = this.first.position.dst(this.second.position)
            val dstFromFirst = refPoint.dst(this.first.position)
            val dstFromSecond = refPoint.dst(this.second.position)
            val invNormDstFromFirst = 1 - (dstFromFirst / totalDst)
            val invNormDstFromSecond = 1 - (dstFromSecond / totalDst)

            return this.first.attributes.nodeObstacle.getChallenge() * invNormDstFromFirst +
                    this.second.attributes.nodeObstacle.getChallenge() * invNormDstFromSecond
        }

        fun Pair<Node, Node>.getPointElevation(refPoint : Point) : Float {
            val totalDst = this.first.position.dst(this.second.position)
            val dstFromFirst = refPoint.dst(this.first.position)
            val dstFromSecond = refPoint.dst(this.second.position)
            val invNormDstFromFirst = 1 - (dstFromFirst / totalDst)
            val invNormDstFromSecond = 1 - (dstFromSecond / totalDst)

            return this.first.attributes.nodeElevation.getHeight() * invNormDstFromFirst +
                    this.second.attributes.nodeElevation.getHeight() * invNormDstFromSecond
        }

        //build walls for nodeLinks that have not been rendered in nodeMesh
        fun NodeRoomMesh.buildWallsAndPath() {

            this.nodeLinks.filter{ !renderedNodeLinks.contains(it) }.forEach { link ->

                val firstNode = this.nodesMap.keys.toMutableList().getNode(link.firstNodeUuid)!!
                val secondNode = this.nodesMap.keys.toMutableList().getNode(link.secondNodeUuid)!!

//                println ("processing render link between ${firstNode.uuid} and ${secondNode.uuid}")
                val line = Line(firstNode.position, secondNode.position)

                line.pointsInBorder((NextDistancePx * 0.2).toInt()).forEach { pathPoint ->

                    val pointChallengeRange = Pair(firstNode, secondNode).getPointChallenge(pathPoint) / 100f
                    val elevation = Pair(firstNode, secondNode).getPointElevation(pathPoint)

                    this.builtPath[pathPoint] = pathPoint +
                            Point(Probability(mean = 0f, range = pointChallengeRange).getValue(), Probability(mean = 0f, range = pointChallengeRange).getValue())
                    this.obstaclePath[pathPoint] = Probability(mean = 0.25f, range = pathPoint.dst(this.builtPath[pathPoint])).getValue()
                    this.elevationPath[pathPoint] = elevation
                }

                line.pointsInBorder((NextDistancePx * 0.3).toInt()).forEach { wallPoint -> this.builtWall[wallPoint] = wallPoint +
                        Point(Probability(mean = 1f, range = 0.5f).getValue(), Probability(mean = 1f, range = 0.5f).getValue()) }

                this.builtWall.keys.removeAll(builtPath.keys)
                this.pastWall.keys.removeAll(builtPath.keys)
                this.currentWall.keys.removeAll(builtPath.keys)

                line.pointsInBorder((NextDistancePx * 0.5).toInt()).forEach { wallFadePoint -> this.builtWallFade[wallFadePoint] = wallFadePoint +
                        Point(Probability(mean = 0.5f, range = 0.3f).getValue(), Probability(mean = 0.5f, range = 0.3f).getValue()) }

                this.builtWallFade.keys.removeAll(builtPath.keys)
                this.builtWallFade.keys.removeAll(builtWall.keys)
                this.pastWallFade.keys.removeAll(builtPath.keys)
                this.pastWallFade.keys.removeAll(builtWall.keys)
                this.currentWallFade.keys.removeAll(builtPath.keys)
                this.currentWallFade.keys.removeAll(builtWall.keys)

                renderedNodeLinks.add(link)
            }
        }

        //render all built walls
        fun NodeRoomMesh.renderWalls() {

            if (this.builtPath.size > this.currentPath.size) this.builtPath.forEach { this.currentPath[it.key] = it.value }
            if (this.builtWall.size > this.currentWall.size) this.builtWall.forEach { this.currentWall[it.key] = it.value }
            if (this.builtWallFade.size > this.currentWallFade.size) this.builtWallFade.forEach { this.currentWallFade[it.key] = it.value }
        }

        //line of sight
        fun NodeRoomMesh.renderWallsAndPathLos(refPosition : Point, refAngle : Angle, radius : Float = NextDistancePx * .5f) {

            val aMin = 0
            val aMax = 359

            currentPath.entries.forEach { this.pastPath[it.key] = it.value }
            currentWall.entries.forEach { this.pastWall[it.key] = it.value }
            currentWallFade.entries.forEach { this.pastWallFade[it.key] = it.value }

            currentPath.entries.clear()
            currentWall.entries.clear()
            currentWallFade.entries.clear()

            val processedPoints = mutableSetOf<Point>()

            (aMin..aMax step 1).forEach { aIter ->
                //allow at most three pixels of fade per ray
                var fadeCounter = 1
                var rayLengthIter = 1f

                val checkRadius = if ( Math.abs(aIter - refAngle) < 30f || Math.abs(aIter - refAngle) > 330f ) radius else radius *.5f

//                println("aIter: $aIter, checkRadius: $checkRadius")

                while ( rayLengthIter <= checkRadius && fadeCounter > 0 ) {
                    val checkPoint = refPosition.getPositionByDistanceAndAngle(rayLengthIter, aIter.toFloat()).trunc()

                    if (!processedPoints.contains(checkPoint)) {
                        processedPoints.add(checkPoint)

//                        println("checkPoint: $checkPoint")
                        if (builtPath.contains( checkPoint ) ) {
                            currentPath[checkPoint] = builtPath[checkPoint]!!
                            pastPath.remove(checkPoint)
                       }

                        if (builtWallFade.contains( checkPoint ) ) {
                            currentWallFade[checkPoint] = builtWallFade[checkPoint]!!
                            pastWallFade.remove(checkPoint)
                            fadeCounter--
                        }

                        if (builtWall.contains( checkPoint ) ) {
                            currentWall[checkPoint] = builtWall[checkPoint]!!
                            pastWall.remove(checkPoint)
                            fadeCounter--
                        }
                    }

                    rayLengthIter++
                }
//                println("final rayLengthIter: $rayLengthIter")
            }
        }
    }
}