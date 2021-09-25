package org.river.exertion.koboldCave.node.nodeMesh

import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import org.river.exertion.*
import org.river.exertion.koboldCave.Line
import org.river.exertion.koboldCave.leaf.ILeaf
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.nodeMesh
import org.river.exertion.koboldCave.leaf.Leaf
import org.river.exertion.koboldCave.Line.Companion.angleBetween
import org.river.exertion.koboldCave.Line.Companion.borderLines
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.Line.Companion.isInBorder
import org.river.exertion.koboldCave.Line.Companion.points
import org.river.exertion.koboldCave.Line.Companion.pointsInBorder
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.nodeMesh.INodeMesh.Companion.processMesh
import org.river.exertion.koboldCave.node.Node.Companion.averagePositionWithinNodes
import org.river.exertion.koboldCave.node.Node.Companion.nearestNodesOrderedAsc
import org.river.exertion.koboldCave.node.NodeAttributes
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.removeOrphanLinks
import org.river.exertion.koboldCave.node.nodeMesh.INodeMesh.Companion.setBordering
import java.lang.Math.abs
import java.lang.Math.pow
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

class NodeRoom(override val uuid: UUID = UUID.randomUUID(), override var description: String = "nodeRoom${Random.nextInt(256)}"
               , override var nodes : MutableList<Node> = mutableListOf(), override var nodeLinks : MutableList<NodeLink> = mutableListOf()
               , var centroid : Node = Node(NodeAttributes.NodeType.CENTROID), var attributes : NodeRoomAttributes = NodeRoomAttributes() ) :
    INodeMesh {

    val activatedExitNodes = mutableListOf<Node>()
    var currentWall : MutableMap<Point, Point> = mutableMapOf()
    var pastWall : MutableMap<Point, Point> = mutableMapOf()
    var currentWallFade : MutableMap<Point, Point> = mutableMapOf()
    var pastWallFade : MutableMap<Point, Point> = mutableMapOf()

    val floorNodes = mutableListOf<Node>()
    var currentFloor : MutableMap<Point, Point> = mutableMapOf()
    var pastFloor : MutableMap<Point, Point> = mutableMapOf()
    var currentStairs : MutableMap<Point, Angle> = mutableMapOf()
    var pastStairs : MutableMap<Point, Angle> = mutableMapOf()

    //build constructor
    constructor(centerPoint: Point, height: Int, circleNoise : Int = 50, angleNoise : Int = 50, heightNoise : Int = 50, borderRooms : NodeRoom = NodeRoom(),
                exitsAllowed : Int = maxGenerativeExits, initCentroid : Node? = null ) : this (
    ) {
        val workNodeRoom = centerPoint.buildNodeRoom(height, circleNoise, angleNoise, heightNoise, borderRooms, initCentroid)

        this.description = workNodeRoom.description
        this.nodes = mutableListOf<Node>().apply { addAll(workNodeRoom.nodes) }
        this.nodeLinks = mutableListOf<NodeLink>().apply { addAll(workNodeRoom.nodeLinks) }
        this.centroid = workNodeRoom.centroid
        this.setExitNodes(exitsAllowed)
        this.nodes.forEach {
            it.attributes.nodeObstacle = NodeAttributes.getProbNodeObstacle()
            it.attributes.nodeElevation = NodeAttributes.getProbNodeElevation()
        }

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

        val maxGenerativeExits = 4

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
        fun NodeRoom.setExitNodes(exitsAllowed : Int) {

            val returnNodes = mutableListOf<Node>()

            val numExitsTarget = Probability(maxGenerativeExits / 2, maxGenerativeExits / 4).getValue().toInt()

            val numExits = min(exitsAllowed, numExitsTarget)

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

//            println("buildWalls nodes in room: $nodes")

            this.getLineList().forEach { line ->

                val fadeBorderLines = line.borderLines( (NextDistancePx * 0.5).roundToInt())

                val fadeMinX = fadeBorderLines.points().minOf { xVar -> xVar.x }.roundToInt()
                val fadeMaxX = fadeBorderLines.points().maxOf { xVar -> xVar.x }.roundToInt()
                val fadeMinY = fadeBorderLines.points().minOf { yVar -> yVar.y }.roundToInt()
                val fadeMaxY = fadeBorderLines.points().maxOf { yVar -> yVar.y }.roundToInt()

                (fadeMinX..fadeMaxX).forEach { xVar ->
                    (fadeMinY..fadeMaxY).forEach { yVar ->
                        val checkPoint = Point(xVar.toFloat(), yVar.toFloat())
                        if ( checkPoint.isInBorder(line, (NextDistancePx * 0.5).roundToInt()) ) fadeBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (NextDistancePx * 0.3).roundToInt()) ) maxBorderRegion.add(checkPoint)
                        if ( checkPoint.isInBorder(line, (NextDistancePx * 0.2).roundToInt()) ) minBorderRegion.add(checkPoint)
                    }
                }
            }

            val minX = fadeBorderRegion.minOf { xVar -> xVar.x }.roundToInt()
            val maxX = fadeBorderRegion.maxOf { xVar -> xVar.x }.roundToInt()
            val minY = fadeBorderRegion.minOf { yVar -> yVar.y }.roundToInt()
            val maxY = fadeBorderRegion.maxOf { yVar -> yVar.y }.roundToInt()

            (minX..maxX).forEach { xVar ->
                (minY..maxY).forEach { yVar ->
                    val checkPoint = Point(xVar.toFloat(), yVar.toFloat())

                    if ( fadeBorderRegion.contains(checkPoint) && !maxBorderRegion.contains(checkPoint) && !minBorderRegion.contains(checkPoint) )
                        newCurrentWallFade[checkPoint] = checkPoint + Point(Probability(mean = 0f, range = 0.3f).getValue(), Probability(mean = 0f, range = 0.3f).getValue())
//                       newCurrentWallFade[checkPoint] = checkPoint

                    if ( maxBorderRegion.contains(checkPoint) && !minBorderRegion.contains(checkPoint) )
                        newCurrentWall[checkPoint] = checkPoint + Point(Probability(mean = 0f, range = 0.5f).getValue(), Probability(mean = 0f, range = 0.5f).getValue())
//                        newCurrentWall[checkPoint] = checkPoint
                }
            }

            newCurrentWall.entries.forEach { this.currentWall[it.key] = it.value }
            newCurrentWallFade.entries.forEach { this.currentWallFade[it.key] = it.value }
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
                            else newCurrentWallFade[checkPoint] = checkPoint + Point(Probability(mean = 0f, range = 0.3f).getValue(), Probability(mean = 0f, range = 0.3f).getValue())

                            fadeCounter--
                        }
                        if ( maxBorderRegion.contains(checkPoint) && !minBorderRegion.contains(checkPoint) )
                            if (pastWall.contains( checkPoint ) ) { currentWall[checkPoint] = pastWall[checkPoint]!! ; pastWall.remove(checkPoint) }
                            else newCurrentWall[checkPoint] = checkPoint + Point(Probability(mean = 0f, range = 0.5f).getValue(), Probability(mean = 0f, range = 0.5f).getValue())
                    }

                    rayLengthIter++
                }
            }

            newCurrentWall.entries.forEach { this.currentWall[it.key] = it.value }
            newCurrentWallFade.entries.forEach { this.currentWallFade[it.key] = it.value }
        }

        //build all floors, fully lit
        fun NodeRoom.buildFloors() {

            currentFloor.entries.forEach { this.pastFloor[it.key] = it.value }
            currentFloor.entries.clear()

//            println("buildFloors nodes in room: $nodes")

            nodes.forEach { node ->

                val currentNodePoints = mutableListOf<Point>()

                //center circle first
                val radius = 6

                //https://stackoverflow.com/questions/40779343/java-loop-through-all-pixels-in-a-2d-circle-with-center-x-y-and-radius
                val yMin = node.position.y.toInt() - radius
                val yMax = node.position.y.toInt() + radius

                (yMin..yMax).forEach { yIter ->
                    var xIter1 = node.position.x.toInt()
                    var xIter2 = node.position.x.toInt() + 1

                    while ( pow((xIter1 - node.position.x).toDouble(), 2.0) + pow((yIter - node.position.y).toDouble(), 2.0) <= pow(radius.toDouble(), 2.0) ) {
                        val checkPoint = Point(xIter1.toFloat(), yIter.toFloat())

                        val isPoint = ProbabilitySelect(
                            mapOf(
                                true to Probability(node.attributes.nodeObstacle.getChallenge(), 0),
                                false to Probability(100 - node.attributes.nodeObstacle.getChallenge(), 0)
                            )
                        ).getSelectedProbability()!!

                        if (isPoint) {
                            if (!currentWall.keys.contains(checkPoint) && !currentWallFade.keys.contains(checkPoint))
                                currentFloor[checkPoint] = checkPoint + Point(Probability(mean = 0f, range = .75f).getValue(), Probability(mean = 0f, range = .75f).getValue())
//                            currentFloor[checkPoint] = checkPoint
                        }

                        xIter1--
                    }
                    while ( pow((xIter2 - node.position.x).toDouble(), 2.0) + pow((yIter - node.position.y).toDouble(), 2.0) <= pow(radius.toDouble(), 2.0) ) {
                        val checkPoint = Point(xIter2.toFloat(), yIter.toFloat())

                        val isPoint = ProbabilitySelect(
                            mapOf(
                                true to Probability(node.attributes.nodeObstacle.getChallenge(), 0),
                                false to Probability(100 - node.attributes.nodeObstacle.getChallenge(), 0)
                            )
                        ).getSelectedProbability()!!

                        if (isPoint) {
                            if (!currentWall.keys.contains(checkPoint) && !currentWallFade.keys.contains(checkPoint))
                                currentFloor[checkPoint] = checkPoint + Point(Probability(mean = 0f, range = .75f).getValue(), Probability(mean = 0f, range = .75f).getValue())
//                                currentFloor[checkPoint] = checkPoint
                        }

                        xIter2++
                    }
                }

                val childNodes = node.getNodeChildren(nodes, nodeLinks)
                val borderWidth = 4
                val nodeChallenge = node.attributes.nodeObstacle.getChallenge()

                childNodes.forEach { childNode ->

                    val beginCorridorPos = node.position.getPositionByDistanceAndAngle(borderWidth.toFloat() * 2, node.angleBetween(childNode))
                    val dstHalfCorridor = beginCorridorPos.dst(childNode.position) / 2 - borderWidth * 2
                    val halfCorridorPos = beginCorridorPos.getPositionByDistanceAndAngle(dstHalfCorridor, node.angleBetween(childNode))

                    //build obstacles
                    val childNodeChallenge = childNode.attributes.nodeObstacle.getChallenge()

                    pointsInBorder(Line(beginCorridorPos, halfCorridorPos), borderWidth).forEach { checkPoint ->
                        val dstGradientFromBegin = checkPoint.dst(beginCorridorPos) / dstHalfCorridor
                        val dstGradientFromHalf = checkPoint.dst(halfCorridorPos) / dstHalfCorridor
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
                                currentFloor[checkPoint] = checkPoint + Point(Probability(mean = 0f, range = .75f).getValue(), Probability(mean = 0f, range = .75f).getValue())
                        }
                    }

                    //build elevations
                    val nodeHeight = node.attributes.nodeElevation.getHeight()
                    val childNodeHeight = childNode.attributes.nodeElevation.getHeight()
                    val dstHalfCorridorBorder = (dstHalfCorridor + borderWidth).roundToInt()

                    if (nodeHeight != childNodeHeight) {
                        val halfHeightDiff = abs(nodeHeight - childNodeHeight) / 2 // difference for half corridor
                        val stairStep = ((dstHalfCorridorBorder / halfHeightDiff) ).roundToInt() //stair step at half height unit
                        val halfStep = stairStep / 2

                        val loopStep = if (stairStep > 0) stairStep else 1

//                        println("node: $node, childNode: $childNode, dstHalfCorridorBorder: $dstHalfCorridorBorder, halfHeightDiff: $halfHeightDiff, stairStep: $stairStep, halfStep: $halfStep")

                        (halfStep..dstHalfCorridorBorder step loopStep).forEach { dstOffset ->
                            val angle = if (nodeHeight > childNodeHeight) node.angleBetween(childNode) else (node.angleBetween(childNode) + 180f).normalizeDeg()

                            val varDstOffset = Probability(dstOffset.toFloat(), 3f).getValue()
//                            val varDstOffsetOffset = Probability(varDstOffset, 2f).getValue()

                            val varDstPos = beginCorridorPos.getPositionByDistanceAndAngle(varDstOffset, node.angleBetween(childNode))
//                            val varDstPosOffset = beginCorridorPos.getPositionByDistanceAndAngle(varDstOffsetOffset, node.angleBetween(childNode))

                            if (!currentStairs.keys.contains(varDstPos) ) {
//                                currentStairs[varDstPos] = Probability(angle + 30f, 15f).getValue()
                                currentStairs[varDstPos] = Probability(angle, 30f).getValue()
                            }
//                            if (!currentStairs.keys.contains(varDstPosOffset) ) {
//                                currentStairs[varDstPosOffset] = Probability(angle - 30f, 15f).getValue()
//                            }
                        }
                    }
                }
                floorNodes.add(node)
            }
        }
    }
}