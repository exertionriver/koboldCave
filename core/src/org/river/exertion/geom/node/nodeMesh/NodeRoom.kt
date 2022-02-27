package org.river.exertion.geom.node.nodeMesh

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.*
import org.river.exertion.geom.leaf.ILeaf.Companion.nodeMesh
import org.river.exertion.geom.leaf.Leaf
import org.river.exertion.geom.Line.Companion.angleBetween
import org.river.exertion.geom.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.geom.lattice.ArrayLattice
import org.river.exertion.geom.lattice.ILattice.Companion.nodeMesh
import org.river.exertion.geom.lattice.RoundedLattice
import org.river.exertion.geom.leaf.Lace
import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.Node.Companion.angleBetween
import org.river.exertion.geom.node.nodeMesh.INodeMesh.Companion.processMesh
import org.river.exertion.geom.node.Node.Companion.averagePositionWithinNodes
import org.river.exertion.geom.node.Node.Companion.getNode
import org.river.exertion.geom.node.Node.Companion.nearestNodesOrderedAsc
import org.river.exertion.geom.node.NodeAttributes
import org.river.exertion.geom.node.NodeLink
import org.river.exertion.geom.node.NodeLink.Companion.removeOrphanLinks
import org.river.exertion.geom.node.nodeMesh.INodeMesh.Companion.setBordering
import java.util.*
import kotlin.math.min
import kotlin.random.Random

class NodeRoom(override val uuid: UUID = UUID.randomUUID(), override var description: String = "nodeRoom${Random.nextInt(256)}"
               , override var nodes : MutableSet<Node> = mutableSetOf(), override var nodeLinks : MutableSet<NodeLink> = mutableSetOf()
               , var centroid : Node = Node(NodeAttributes.NodeType.CENTROID), var attributes : NodeRoomAttributes = NodeRoomAttributes() ) :
    INodeMesh {

    val activatedExitNodes = mutableSetOf<Node>()

    //build constructor
    constructor(attributes: NodeRoomAttributes, centerPoint: Point, borderRooms : NodeRoom = NodeRoom()
                , exitsAllowed : Int = maxGenerativeExits, initCentroid : Node? = null ) : this (
        attributes.geomType, attributes.geomStyle, centerPoint, attributes.geomHeight, attributes.circleNoise, attributes.angleNoise, attributes.heightNoise
        , attributes.geomNoise
        , borderRooms, exitsAllowed, initCentroid
    )

        //build constructor
    constructor(geomType: NodeRoomAttributes.GeomType = NodeRoomAttributes.GeomType.LEAF
                , geomStyle: NodeRoomAttributes.GeomStyle = NodeRoomAttributes.GeomStyle.CIRCLE_IN
                , centerPoint: Point, height: Int = 3, circleNoise : Int = 50, angleNoise : Int = 50, heightNoise : Int = 50, geomNoise : Int = 0
                , borderRooms : NodeRoom = NodeRoom()
                , exitsAllowed : Int = maxGenerativeExits, initCentroid : Node? = null ) : this (
    ) {

        val workNodeRoom = centerPoint.buildNodeRoom(geomType, geomStyle, height, circleNoise, angleNoise, heightNoise, geomNoise, borderRooms, initCentroid)

        this.description = workNodeRoom.description
        this.nodes = mutableSetOf<Node>().apply { addAll(workNodeRoom.nodes) }
        this.nodeLinks = mutableSetOf<NodeLink>().apply { addAll(workNodeRoom.nodeLinks) }
        this.centroid = workNodeRoom.centroid
        this.setExitNodes(exitsAllowed)
        this.nodes.forEach {
            it.attributes.nodeObstacle = NodeAttributes.getProbNodeObstacle()
            it.attributes.nodeElevation = NodeAttributes.getProbNodeElevation()
        }

        this.attributes.circleNoise = circleNoise
        this.attributes.angleNoise = angleNoise
        this.attributes.heightNoise = heightNoise
        this.attributes.geomNoise = geomNoise

        this.attributes.geomType = geomType
        this.attributes.geomStyle = geomStyle
        this.attributes.geomHeight = height

//        println("new NodeRoomMesh: ${this.nodes.size}, ${workNodeRoom.centroid}, ${this.centroid}")
    }


    //NodeMesh constructor
    constructor(copyNodeMesh : NodeMesh
                , updUuid: UUID = copyNodeMesh.uuid
                , updDescription: String = copyNodeMesh.description) : this (
        uuid = updUuid
        , description = updDescription
    ) {
        nodes = mutableSetOf<Node>().apply { addAll(copyNodeMesh.nodes) }
        nodeLinks = mutableSetOf<NodeLink>().apply { addAll(copyNodeMesh.nodeLinks) }
    }

    //copy constructor
    constructor(copyNodeRoom : NodeRoom
                , updUuid: UUID = copyNodeRoom.uuid
                , updDescription: String = copyNodeRoom.description) : this (
        uuid = updUuid
        , description = updDescription
    ) {
        nodes = mutableSetOf<Node>().apply { addAll(copyNodeRoom.nodes) }
        nodeLinks = mutableSetOf<NodeLink>().apply { addAll(copyNodeRoom.nodeLinks) }
    }

    operator fun plus(secondMesh : NodeRoom) : NodeRoom {
        val workNodeMesh = this

        val workNodes = mutableSetOf<Node>().apply { addAll(workNodeMesh.nodes); addAll(secondMesh.nodes) }
        val workNodeLinks = mutableSetOf<NodeLink>().apply { addAll(workNodeMesh.nodeLinks); addAll(secondMesh.nodeLinks) }

        return NodeRoom(description ="${workNodeMesh.description} + ${secondMesh.description}", nodes = workNodes, nodeLinks = workNodeLinks).apply { consolidateStackedNodes() }
    }

    operator fun minus(secondMesh : NodeRoom) : NodeRoom {
        val workNodeMesh = this

        val workNodes = mutableSetOf<Node>().apply { addAll(workNodeMesh.nodes); removeAll(secondMesh.nodes) }
        val workNodeLinks = mutableSetOf<NodeLink>().apply { addAll(workNodeMesh.nodeLinks); this.removeOrphanLinks(workNodes) }

        return NodeRoom(description ="${workNodeMesh.description} + ${secondMesh.description}", nodes = workNodes, nodeLinks = workNodeLinks)

    }

    fun getExitNodes() = nodes.filter { it.attributes.nodeType == NodeAttributes.NodeType.EXIT }

    fun getInactivatedExitNodes() = getExitNodes().filter { !activatedExitNodes.contains(it) }

    fun inactiveExitNodesInRange(currentNode : Node) = getInactivatedExitNodes().filter { it == currentNode || it.getNodeChildren(this.nodes, this.nodeLinks).contains(currentNode) }

    override fun toString() = "${NodeRoom::class.simpleName}(${uuid}) : $description, $nodes, $nodeLinks"

    companion object {

        val maxGenerativeExits = 4

        fun Point.buildNodeRoom(geomType: NodeRoomAttributes.GeomType, geomStyle: NodeRoomAttributes.GeomStyle
            , height : Int, circleNoise : Int = 0, angleNoise : Int = 0, heightNoise : Int = 0, geomNoise : Int = 0
            , borderRooms : NodeRoom = NodeRoom(), centroid : Node?) : NodeRoom {

            var roomMesh = NodeRoom()
            roomMesh.centroid = centroid ?: Node(position = this)

            if (height < 1) return roomMesh

            val geomMap = mutableMapOf<Angle, Point>()

            //using as a divisor
            val cappedCircleNoise = if (circleNoise > 100) 100 else if (circleNoise < 0) 0 else circleNoise
            val cappedAngleNoise = if (angleNoise > 100) 100 else if (angleNoise < 0) 0 else angleNoise

            when (geomStyle) {
                NodeRoomAttributes.GeomStyle.OPPOSITE -> {
                    val pointsOnCircle = 2
                    val angleVariance = 30f

                    (1..pointsOnCircle).toList().forEach{ idx ->
                        val pointOnCircle = ( 180f * idx ).normalizeDeg()
                        val pointNoiseOnCircle = Probability(angleVariance, angleVariance / 2 * cappedCircleNoise / 100).getValue().normalizeDeg()
                        val noisyPointOnCircle = pointOnCircle + pointNoiseOnCircle

                        //points back to the center of the circle
                        val angleNoiseOnCircle = Probability(180f, angleVariance * cappedAngleNoise / 100).getValue().normalizeDeg()
                        val noisyAngleOnCircle = (angleNoiseOnCircle + noisyPointOnCircle).normalizeDeg()

                        geomMap[noisyAngleOnCircle] = roomMesh.centroid.position.getPositionByDistanceAndAngle(height * NextDistancePx / 2, noisyPointOnCircle)
                    }
                }
                NodeRoomAttributes.GeomStyle.SINGLE -> {
                    val pointsOnCircle = 1
                    val angleVariance = 30f

                    (1..pointsOnCircle).toList().forEach{ idx ->
                        val pointOnCircle = ( 360f * idx ).normalizeDeg()
                        val pointNoiseOnCircle = Probability(angleVariance, angleVariance / 2 * cappedCircleNoise / 100).getValue().normalizeDeg()
                        val noisyPointOnCircle = pointOnCircle + pointNoiseOnCircle

                        //points back to the center of the circle
                        val angleNoiseOnCircle = Probability(180f, angleVariance * cappedAngleNoise / 100).getValue().normalizeDeg()
                        val noisyAngleOnCircle = (angleNoiseOnCircle + noisyPointOnCircle).normalizeDeg()

                        geomMap[noisyAngleOnCircle] = roomMesh.centroid.position.getPositionByDistanceAndAngle(height * NextDistancePx / 2, noisyPointOnCircle)
                    }
                }
                NodeRoomAttributes.GeomStyle.SEQUENCE -> {
                    val originAngleOnCircle = Probability(360f, 180f * cappedCircleNoise / 100).getValue().normalizeDeg()
                    val endingAngleOnCircle = (originAngleOnCircle - 180f).normalizeDeg()
                    val originPointOnCircle = roomMesh.centroid.position.getPositionByDistanceAndAngle(height * NextDistancePx / 2, originAngleOnCircle)
                    val endingPointOnCircle = roomMesh.centroid.position.getPositionByDistanceAndAngle(height * NextDistancePx / 2, endingAngleOnCircle)

                    println("originAngle:${originAngleOnCircle}, endingAngle:${endingAngleOnCircle}, originPoint:${originPointOnCircle}, endingPoint:${endingPointOnCircle}, distance: ${originPointOnCircle.dst(endingPointOnCircle)}")

                    val sequenceLine = NodeLine(firstNode = Node(position = originPointOnCircle), lastNode = Node(position=endingPointOnCircle), lineNoise = cappedAngleNoise)
                    val angleVariance = 30f

//                    println("nodeLine size:${sequenceLine.nodes.size}, order size:${sequenceLine.nodeOrder.size}")

                    sequenceLine.nodeOrder.forEachIndexed { idx, lineNodeUUID ->
//                        println("idx:$idx, lineNodeUUID:$lineNodeUUID")
                        //get anglebetween this and next node
                        if (idx < sequenceLine.nodes.size - 1) {
                            val currNode = sequenceLine.nodes.getNode(lineNodeUUID)!!
                            val nextNode = sequenceLine.nodes.getNode(sequenceLine.nodeOrder[idx + 1])!!

                            val angleBetween = currNode.angleBetween(nextNode)
                            geomMap[angleBetween] = currNode.position
                        } else {
                            var angleNoiseFinalNode = Probability(endingAngleOnCircle, angleVariance * cappedAngleNoise / 100).getValue().normalizeDeg()

                            while (geomMap[angleNoiseFinalNode] != null) angleNoiseFinalNode++
                            geomMap[angleNoiseFinalNode] = sequenceLine.nodes.getNode(lineNodeUUID)!!.position
                        }
                    }

                    geomMap.entries.forEach {
                        println("${it.key}: ${it.value}")
                    }

                }
                NodeRoomAttributes.GeomStyle.CIRCLE_OUT -> {
                    val pointsOnCircle = height
                    val sliceOnCircle = 360F / height

                    (1..pointsOnCircle).toList().forEach{ idx ->
                        val pointOnCircle = ( sliceOnCircle * idx ).normalizeDeg()
                        val pointNoiseOnCircle = Probability(sliceOnCircle, sliceOnCircle / 2 * cappedCircleNoise / 100).getValue().normalizeDeg()
                        val noisyPointOnCircle = pointOnCircle + pointNoiseOnCircle

                        //points back to the center of the circle
                        val angleNoiseOnCircle = Probability(180f, 60f * cappedAngleNoise / 100).getValue().normalizeDeg()
                        val noisyAngleOnCircle = (angleNoiseOnCircle + noisyPointOnCircle).normalizeDeg()

                        geomMap[noisyAngleOnCircle] = roomMesh.centroid.position
                    }
                }
                else -> { //CIRCLE_IN / NONE
                    val pointsOnCircle = height
                    val sliceOnCircle = 360F / height

                    (1..pointsOnCircle).toList().forEach{ idx ->
                        val pointOnCircle = ( sliceOnCircle * idx ).normalizeDeg()
                        val pointNoiseOnCircle = Probability(sliceOnCircle, sliceOnCircle / 2 * cappedCircleNoise / 100).getValue().normalizeDeg()
                        val noisyPointOnCircle = pointOnCircle + pointNoiseOnCircle

                        //points back to the center of the circle
                        val angleNoiseOnCircle = Probability(180f, 60f * cappedAngleNoise / 100).getValue().normalizeDeg()
                        val noisyAngleOnCircle = (angleNoiseOnCircle + noisyPointOnCircle).normalizeDeg()

                        geomMap[noisyAngleOnCircle] = roomMesh.centroid.position.getPositionByDistanceAndAngle(height * NextDistancePx / 2, noisyPointOnCircle)
                    }
                }
            }

            val cappedHeightNoise = if (heightNoise > 100) 100 else if (heightNoise < 0) 0 else heightNoise
            val cappedGeomNoise = if (geomNoise > 100) 100 else if (geomNoise < 0) 0 else geomNoise

            geomMap.forEach {

                val minHeight = if (height > 1) height - 1 else 1
                val maxHeight = height + 1

                val noisyHeight = ProbabilitySelect(
                    mapOf(
                        "$minHeight" to Probability(cappedHeightNoise / 4, 0),
                        "$height" to Probability(100 - (cappedHeightNoise / 2), 0),
                        "$maxHeight" to Probability(cappedHeightNoise / 4, 0)
                    )
                ).getSelectedProbability()!!.toInt()

                //geomNoise
                val minGeomProbability = 100 / NodeRoomAttributes.GeomType.values().size
                val geomProbability = minGeomProbability + (100 - minGeomProbability) * (100 - cappedGeomNoise) / 100
                val otherGeomProbability = (100 - geomProbability) / (NodeRoomAttributes.GeomType.values().size - 1)

//                Gdx.app.log("geomNoise", "noise:$geomNoise, prob:$geomProbability, other:$otherGeomProbability")

                val noisyGeomMap = mutableMapOf(geomType to Probability(geomProbability, 0))
                NodeRoomAttributes.GeomType.values().forEach { checkGeomType ->
                    if (checkGeomType != geomType) noisyGeomMap[checkGeomType] = Probability(otherGeomProbability, 0)
                }
                val noisyGeom = ProbabilitySelect(noisyGeomMap.toMap()).getSelectedProbability()!!

                roomMesh += when (noisyGeom) {
                    NodeRoomAttributes.GeomType.LACE -> NodeRoom(Lace(topHeight = noisyHeight, topAngle = it.key, position = it.value).nodeMesh().setBordering(borderRooms, NextDistancePx * .5, Node(position = this)) as NodeMesh)
                    NodeRoomAttributes.GeomType.ROUNDED_LATTICE -> NodeRoom(RoundedLattice(topHeight = noisyHeight, topAngle = it.key, position = it.value).nodeMesh().setBordering(borderRooms, NextDistancePx * .5, Node(position = this)) as NodeMesh)
                    NodeRoomAttributes.GeomType.ARRAY_LATTICE -> NodeRoom(ArrayLattice(topHeight = noisyHeight, topAngle = it.key, position = it.value).nodeMesh().setBordering(borderRooms, NextDistancePx * .5, Node(position = this)) as NodeMesh)
                    //else LEAF / NONE
                    else -> NodeRoom(Leaf(topHeight = noisyHeight, topAngle = it.key, position = it.value).nodeMesh().setBordering(borderRooms, NextDistancePx * .5, Node(position = this)) as NodeMesh)
                }
            }

            roomMesh.processMesh()

            //remove links that are too long as a hack to prevent for long cross-link room creation
//            val removeLinks = mutableListOf<NodeLink>()
//            roomMesh.nodeLinks.forEach { if (it.getDistance(roomMesh.nodes)!! > NextDistancePx * 1.5) removeLinks.add(it) }
//            roomMesh.nodeLinks.removeAll(removeLinks)

            //reset centroid after adding and processing
            roomMesh.centroid = Node(position = roomMesh.nodes.averagePositionWithinNodes() )

            return roomMesh
        }

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
    }
}