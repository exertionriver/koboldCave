package org.river.exertion.koboldCave.node.nodeMesh

import org.river.exertion.koboldCave.Probability
import org.river.exertion.koboldCave.ProbabilitySelect
import org.river.exertion.*
import org.river.exertion.koboldCave.leaf.ILeaf
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.nodeMesh
import org.river.exertion.koboldCave.leaf.Leaf
import org.river.exertion.koboldCave.Line.Companion.angleBetween
import org.river.exertion.koboldCave.Line.Companion.borderLines
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.Line.Companion.isInBorder
import org.river.exertion.koboldCave.Line.Companion.points
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.nodeMesh.INodeMesh.Companion.processMesh
import org.river.exertion.koboldCave.node.Node.Companion.averagePositionWithinNodes
import org.river.exertion.koboldCave.node.Node.Companion.nearestNodesOrderedAsc
import org.river.exertion.koboldCave.node.NodeAttributes
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.addNodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.removeOrphanLinks
import org.river.exertion.koboldCave.node.nodeMesh.INodeMesh.Companion.setBordering
import org.river.exertion.koboldCave.node.nodeRoomMesh.INodeRoomMesh
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh
import java.lang.Math.abs
import java.lang.Math.pow
import java.util.*
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeRoom(override val uuid: UUID = UUID.randomUUID(), override var description: String = "nodeRoom${Random.nextInt(256)}"
               , override var nodes : MutableList<Node> = mutableListOf(), override var nodeLinks : MutableList<NodeLink> = mutableListOf()
               , var centroid : Node = Node(NodeAttributes.NodeType.CENTROID), var attributes : NodeRoomAttributes = NodeRoomAttributes()
               , var currentWall : MutableMap<Point, Point> = mutableMapOf(), var pastWall : MutableMap<Point, Point> = mutableMapOf()
               , var currentWallFade : MutableMap<Point, Point> = mutableMapOf(), var pastWallFade : MutableMap<Point, Point> = mutableMapOf() ) :
    INodeMesh {

    val activatedExitNodes = mutableListOf<Node>()

    //build constructor
    constructor(centerPoint: Point, height: Int, circleNoise : Int = 50, angleNoise : Int = 50, heightNoise : Int = 50, borderRooms : NodeRoom = NodeRoom(),
                initCentroid : Node? = null ) : this (
    ) {
        val workNodeRoom = centerPoint.buildNodeRoom(height, circleNoise, angleNoise, heightNoise, borderRooms, initCentroid)

        this.description = workNodeRoom.description
        this.nodes = mutableListOf<Node>().apply { addAll(workNodeRoom.nodes) }
        this.nodeLinks = mutableListOf<NodeLink>().apply { addAll(workNodeRoom.nodeLinks) }
        this.centroid = workNodeRoom.centroid
        this.setExitNodes()

        this.attributes.circleNoise = circleNoise
        this.attributes.angleNoise = angleNoise
        this.attributes.heightNoise = heightNoise

//        println("new NodeRoomMesh: ${this.nodes.size}, ${workNodeRoom.centroid}, ${this.centroid}")

    }

    //NodeMesh constructor
    constructor(copyNodeMesh : NodeMesh
                , updUuid: UUID = copyNodeMesh.uuid
                , updDescription: String = copyNodeMesh.description) : this (
        uuid = updUuid
        , description = updDescription
    ) {
        nodes = mutableListOf<Node>().apply { addAll(copyNodeMesh.nodes) }
        nodeLinks = mutableListOf<NodeLink>().apply { addAll(copyNodeMesh.nodeLinks) }
    }

    //copy constructor
    constructor(copyNodeRoom : NodeRoom
                , updUuid: UUID = copyNodeRoom.uuid
                , updDescription: String = copyNodeRoom.description) : this (
        uuid = updUuid
        , description = updDescription
    ) {
        nodes = mutableListOf<Node>().apply { addAll(copyNodeRoom.nodes) }
        nodeLinks = mutableListOf<NodeLink>().apply { addAll(copyNodeRoom.nodeLinks) }
    }

    operator fun plus(secondMesh : NodeRoom) : NodeRoom {
        val workNodeMesh = this

        val workNodes = mutableListOf<Node>().apply { addAll(workNodeMesh.nodes); addAll(secondMesh.nodes) }
        val workNodeLinks = mutableListOf<NodeLink>().apply { addAll(workNodeMesh.nodeLinks); addAll(secondMesh.nodeLinks) }

        return NodeRoom(description ="${workNodeMesh.description} + ${secondMesh.description}", nodes = workNodes, nodeLinks = workNodeLinks).apply { consolidateStackedNodes() }
    }

    operator fun minus(secondMesh : NodeRoom) : NodeRoom {
        val workNodeMesh = this

        val workNodes = mutableListOf<Node>().apply { addAll(workNodeMesh.nodes); removeAll(secondMesh.nodes) }
        val workNodeLinks = mutableListOf<NodeLink>().apply { addAll(workNodeMesh.nodeLinks); this.removeOrphanLinks(workNodes) }

        return NodeRoom(description ="${workNodeMesh.description} + ${secondMesh.description}", nodes = workNodes, nodeLinks = workNodeLinks)

    }

    fun getExitNodes() = nodes.filter { it.attributes.nodeType == NodeAttributes.NodeType.EXIT }

    fun getInactivatedExitNodes() = getExitNodes().filter { !activatedExitNodes.contains(it) }

    fun inactiveExitNodesInRange(currentNode : Node) = getInactivatedExitNodes().filter { it == currentNode || it.getNodeChildren(this.nodes, this.nodeLinks).contains(currentNode) }

    override fun toString() = "${NodeRoom::class.simpleName}(${uuid}) : $description, $nodes, $nodeLinks"

    companion object {

        fun Point.buildNodeRoom(height : Int, circleNoise : Int = 0, angleNoise : Int = 0, heightNoise : Int = 0, borderRooms : NodeRoom = NodeRoom(), centroid : Node?) : NodeRoom {

            var roomMesh = NodeRoom()
            roomMesh.centroid = centroid ?: Node(position = this)

            if (height < 1) return roomMesh

            val leafMap = mutableMapOf<Angle, Point>()
            val pointsOnCircle = height

            //using as a divisor
            val cappedCircleNoise = if (circleNoise > 100) 100 else if (circleNoise < 0) 0 else circleNoise
            val cappedAngleNoise = if (angleNoise > 100) 100 else if (angleNoise < 0) 0 else angleNoise

            val sliceOnCircle = 360F / height

            (1..pointsOnCircle).toList().forEach{ idx ->
                val pointOnCircle = ( sliceOnCircle * idx ).normalizeDeg()
                val pointNoiseOnCircle = Probability(sliceOnCircle, sliceOnCircle / 2 * cappedCircleNoise / 100).getValue().normalizeDeg()
                val noisyPointOnCircle = pointOnCircle + pointNoiseOnCircle

                //points back to the center of the circle
                val angleNoiseOnCircle = Probability(180f, 60f * cappedAngleNoise / 100).getValue().normalizeDeg()
                val noisyAngleOnCircle = (angleNoiseOnCircle + noisyPointOnCircle).normalizeDeg()

                leafMap[noisyAngleOnCircle] = ILeaf.getChildPosition(roomMesh.centroid.position, height * ILeaf.NextDistancePx / 2, noisyPointOnCircle)
            }

            val cappedHeightNoise = if (heightNoise > 100) 100 else if (heightNoise < 0) 0 else heightNoise

            leafMap.forEach {

                val minHeight = if (height > 1) height - 1 else 1
                val maxHeight = height + 1

                val noisyHeight = ProbabilitySelect(
                    mapOf(
                        "$minHeight" to Probability(cappedHeightNoise / 4, 0),
                        "$height" to Probability(100 - (cappedHeightNoise / 2), 0),
                        "$maxHeight" to Probability(cappedHeightNoise / 4, 0)
                    )
                ).getSelectedProbability()!!.toInt()

                roomMesh += NodeRoom(Leaf(topHeight = noisyHeight, topAngle = it.key, position = it.value).nodeMesh().setBordering(borderRooms, NextDistancePx * .5) as NodeMesh)

            }

            roomMesh.processMesh()

            //reset centroid after adding and processing
            roomMesh.centroid = Node(position = roomMesh.nodes.averagePositionWithinNodes() )

            return roomMesh
        }

        //todo: exclude angles that open into existing rooms
        fun NodeRoom.setExitNodes() {

            val returnNodes = mutableListOf<Node>()

            val numExits = Probability(2, 1).getValue().toInt()

            val orderedNodes = this.nodes.nearestNodesOrderedAsc(this.centroid)

            val maxExits = if (orderedNodes.size >= numExits) numExits else orderedNodes.size

            var exitsCount = 0

            var nodeIdx = orderedNodes.size - 1

//            println("maxExits: $maxExits; nodeIdx: $nodeIdx; exitsCount: $exitsCount")

            var finished = (nodeIdx < 0) || (exitsCount == maxExits)

            val anglesExiting = mutableListOf<Float>()

            while (!finished) {
                var angleFound = true

                val nodeAngle = this.centroid.position.angleBetween(orderedNodes[nodeIdx].position)

                anglesExiting.forEach { angle ->
                    val angleDiff = (nodeAngle - angle).normalizeDeg()

                    if ( ( angleDiff < 60 ) || (angleDiff > 300) ) angleFound = false

//                    println("checking $nodeAngle against $angle: $angleDiff ")
                }

                if (angleFound) {
                    orderedNodes[nodeIdx].attributes.nodeType = NodeAttributes.NodeType.EXIT

                    returnNodes.add(orderedNodes[nodeIdx])

                    anglesExiting.add(nodeAngle)

                    exitsCount++
                }

                nodeIdx--

//                println("nodeIdx: $nodeIdx; exitsCount: $exitsCount")

                finished = (nodeIdx < 0) || (exitsCount == maxExits)
            }

        }

        //build all walls, fully lit
        fun NodeRoom.buildWalls() {

            val newCurrentWall = mutableMapOf<Point, Point>()
            val newCurrentWallFade = mutableMapOf<Point, Point>()

            val minBorderRegion = mutableListOf<Point>()
            val maxBorderRegion = mutableListOf<Point>()
            val fadeBorderRegion = mutableListOf<Point>()

            this.getLineList().forEach { line ->

                val fadeBorderLines = line.borderLines((ILeaf.NextDistancePx * 0.5).toInt())

                val fadeMinX = fadeBorderLines.points().minOf { xVar -> xVar.x }.toInt()
                val fadeMaxX = fadeBorderLines.points().maxOf { xVar -> xVar.x }.toInt()
                val fadeMinY = fadeBorderLines.points().minOf { yVar -> yVar.y }.toInt()
                val fadeMaxY = fadeBorderLines.points().maxOf { yVar -> yVar.y }.toInt()

                (fadeMinX..fadeMaxX).forEach { xVar ->
                    (fadeMinY..fadeMaxY).forEach { yVar ->
                        val checkPoint = Point(xVar.toFloat(), yVar.toFloat())
                        if ( checkPoint.isInBorder(line, (ILeaf.NextDistancePx * 0.5).toInt()) ) fadeBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (ILeaf.NextDistancePx * 0.3).toInt()) ) maxBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (ILeaf.NextDistancePx * 0.2).toInt()) ) minBorderRegion.add(checkPoint)
                    }
                }
            }

            val minX = fadeBorderRegion.minOf { xVar -> xVar.x }.toInt()
            val maxX = fadeBorderRegion.maxOf { xVar -> xVar.x }.toInt()
            val minY = fadeBorderRegion.minOf { yVar -> yVar.y }.toInt()
            val maxY = fadeBorderRegion.maxOf { yVar -> yVar.y }.toInt()

            (minX..maxX).forEach { xVar ->
                (minY..maxY).forEach { yVar ->
                    val checkPoint = Point(xVar.toFloat(), yVar.toFloat())

                    if ( fadeBorderRegion.contains(checkPoint) && !maxBorderRegion.contains(checkPoint) && !minBorderRegion.contains(checkPoint) )
                        newCurrentWallFade[checkPoint] = checkPoint + Point(Probability(mean = 0.5f, range = 0.3f).getValue(), Probability(mean = 0.5f, range = 0.3f).getValue())

                    if ( maxBorderRegion.contains(checkPoint) && !minBorderRegion.contains(checkPoint) )
                        newCurrentWall[checkPoint] = checkPoint + Point(Probability(mean = 1f, range = 0.5f).getValue(), Probability(mean = 1f, range = 0.5f).getValue())
                }
            }

            newCurrentWall.entries.forEach { this.currentWall[it.key] = it.value }
            newCurrentWallFade.entries.forEach { this.currentWallFade[it.key] = it.value }
        }

        //radius of light
        fun NodeRoom.buildWalls(refPosition : Point, radius : Float = 0f) {

            val newCurrentWall = mutableMapOf<Point, Point>()
            val newCurrentWallFade = mutableMapOf<Point, Point>()

            val minBorderRegion = mutableListOf<Point>()
            val maxBorderRegion = mutableListOf<Point>()
            val fadeBorderRegion = mutableListOf<Point>()

            this.getLineList().forEach { line ->

                //https://stackoverflow.com/questions/40779343/java-loop-through-all-pixels-in-a-2d-circle-with-center-x-y-and-radius
                val yMin = refPosition.y.toInt() - radius.toInt()
                val yMax = refPosition.y.toInt() + radius.toInt()

                (yMin..yMax).forEach { yIter ->
                    var xIter1 = refPosition.x.toInt()
                    var xIter2 = refPosition.x.toInt() + 1

                    while ( pow((xIter1 - refPosition.x).toDouble(), 2.0) + pow((yIter - refPosition.y).toDouble(), 2.0) <= pow(radius.toDouble(), 2.0) ) {
                        val checkPoint = Point(xIter1.toFloat(), yIter.toFloat())
                        if ( checkPoint.isInBorder(line, (ILeaf.NextDistancePx * 0.5).toInt()) ) fadeBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (ILeaf.NextDistancePx * 0.3).toInt()) ) maxBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (ILeaf.NextDistancePx * 0.2).toInt()) ) minBorderRegion.add(checkPoint)
                        xIter1--
                    }

                    while ( pow((xIter2 - refPosition.x).toDouble(), 2.0) + pow((yIter - refPosition.y).toDouble(), 2.0) <= pow(radius.toDouble(), 2.0) ) {
                        val checkPoint = Point(xIter2.toFloat(), yIter.toFloat())
                        if ( checkPoint.isInBorder(line, (ILeaf.NextDistancePx * 0.5).toInt()) ) fadeBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (ILeaf.NextDistancePx * 0.3).toInt()) ) maxBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (ILeaf.NextDistancePx * 0.2).toInt()) ) minBorderRegion.add(checkPoint)
                        xIter2++
                    }
                }
            }

            val yMin = refPosition.y.toInt() - radius.toInt()
            val yMax = refPosition.y.toInt() + radius.toInt()

            currentWall.entries.forEach { this.pastWall[it.key] = it.value }
            currentWallFade.entries.forEach { this.pastWallFade[it.key] = it.value }

            currentWall.entries.clear()
            currentWallFade.entries.clear()

            (yMin..yMax).forEach { yIter ->
                var xIter1 = refPosition.x.toInt()
                var xIter2 = refPosition.x.toInt() + 1

                while ( pow((xIter1 - refPosition.x).toDouble(), 2.0) + pow((yIter - refPosition.y).toDouble(), 2.0) <= pow(radius.toDouble(), 2.0) ) {
                    val checkPoint = Point(xIter1.toFloat(), yIter.toFloat())
                    if ( fadeBorderRegion.contains(checkPoint) && !maxBorderRegion.contains(checkPoint) && !minBorderRegion.contains(checkPoint) )
                        if (pastWallFade.contains( checkPoint ) ) { currentWallFade[checkPoint] = pastWallFade[checkPoint]!! ; pastWallFade.remove(checkPoint) }
                        else newCurrentWallFade[checkPoint] = checkPoint + Point(Probability(mean = 0.5f, range = 0.3f).getValue(), Probability(mean = 0.5f, range = 0.3f).getValue())
                    if ( maxBorderRegion.contains(checkPoint) && !minBorderRegion.contains(checkPoint) )
                        if (pastWall.contains( checkPoint ) ) { currentWall[checkPoint] = pastWall[checkPoint]!! ; pastWall.remove(checkPoint) }
                        else newCurrentWall[checkPoint] = checkPoint + Point(Probability(mean = 1f, range = 0.5f).getValue(), Probability(mean = 1f, range = 0.5f).getValue())

                    xIter1--
                }

                while ( pow((xIter2 - refPosition.x).toDouble(), 2.0) + pow((yIter - refPosition.y).toDouble(), 2.0) <= pow(radius.toDouble(), 2.0) ) {
                    val checkPoint = Point(xIter2.toFloat(), yIter.toFloat())
                    if ( fadeBorderRegion.contains(checkPoint) && !maxBorderRegion.contains(checkPoint) && !minBorderRegion.contains(checkPoint) )
                        if (pastWallFade.contains( checkPoint ) ) { currentWallFade[checkPoint] = pastWallFade[checkPoint]!! ; pastWallFade.remove(checkPoint) }
                        else newCurrentWallFade[checkPoint] = checkPoint + Point(Probability(mean = 0.5f, range = 0.3f).getValue(), Probability(mean = 0.5f, range = 0.3f).getValue())
                    if ( maxBorderRegion.contains(checkPoint) && !minBorderRegion.contains(checkPoint) )
                        if (pastWall.contains( checkPoint ) ) { currentWall[checkPoint] = pastWall[checkPoint]!! ; pastWall.remove(checkPoint) }
                        else newCurrentWall[checkPoint] = checkPoint + Point(Probability(mean = 1f, range = 0.5f).getValue(), Probability(mean = 1f, range = 0.5f).getValue())

                    xIter2++
                }

                newCurrentWall.entries.forEach { this.currentWall[it.key] = it.value }
                newCurrentWallFade.entries.forEach { this.currentWallFade[it.key] = it.value }
            }
        }

        //line of sight
        fun NodeRoom.buildWallsLos(refPosition : Point, refAngle : Angle, radius : Float = 0f) {

            val newCurrentWall = mutableMapOf<Point, Point>()
            val newCurrentWallFade = mutableMapOf<Point, Point>()

            val minBorderRegion = mutableListOf<Point>()
            val maxBorderRegion = mutableListOf<Point>()
            val fadeBorderRegion = mutableListOf<Point>()

            this.getLineList().forEach { line ->

                val yMin = refPosition.y.toInt() - radius.toInt()
                val yMax = refPosition.y.toInt() + radius.toInt()

                (yMin..yMax).forEach { yIter ->
                    var xIter1 = refPosition.x.toInt()
                    var xIter2 = refPosition.x.toInt() + 1

                    while ( pow((xIter1 - refPosition.x).toDouble(), 2.0) + pow((yIter - refPosition.y).toDouble(), 2.0) <= pow(radius.toDouble(), 2.0) ) {
                        val checkPoint = Point(xIter1.toFloat(), yIter.toFloat())
                        if ( checkPoint.isInBorder(line, (NextDistancePx * 0.5).toInt()) ) fadeBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (NextDistancePx * 0.3).toInt()) ) maxBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (NextDistancePx * 0.2).toInt()) ) minBorderRegion.add(checkPoint)
                        xIter1--
                    }

                    while ( pow((xIter2 - refPosition.x).toDouble(), 2.0) + pow((yIter - refPosition.y).toDouble(), 2.0) <= pow(radius.toDouble(), 2.0) ) {
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

                val checkRadius = if ( abs(aIter - refAngle) < 30f || abs(aIter - refAngle) > 330f ) radius else radius * .5f

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
    }
}