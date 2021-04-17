package node

import Probability
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.VectorPath.Companion.intersects
import leaf.ILeaf
import leaf.Line.intersects
import node.INodeMesh.Companion.addMesh
import node.Node.Companion.addNode
import node.Node.Companion.angleBetween
import node.Node.Companion.getNode
import node.Node.Companion.nearestNodesOrderedAsc
import node.NodeLink.Companion.getNodeLinks
import kotlin.math.abs
import kotlin.math.atan
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeLink(val firstNodeUuid : UUID, val secondNodeUuid : UUID) {

    constructor(copyNodeLink : NodeLink
                , updFirstNodeUuid: UUID = copyNodeLink.firstNodeUuid
                , updSecondNodeUuid: UUID = copyNodeLink.secondNodeUuid) : this (
        firstNodeUuid = updFirstNodeUuid
        , secondNodeUuid = updSecondNodeUuid
    )

    fun getDistance(nodes : MutableList<Node>) : Double? {
        val firstNode = nodes.getNode(firstNodeUuid)
        val secondNode = nodes.getNode(secondNodeUuid)

        return if (firstNode != null && secondNode != null) Point.distance(firstNode.position, secondNode.position) else null
    }

    override fun toString() = "${NodeLink::class.simpleName}($firstNodeUuid, $secondNodeUuid)"

    companion object {
        val consolidateNodeDistance = ILeaf.NextDistancePx / 4
        val linkNodeDistance = ILeaf.NextDistancePx
        val stackedNodeDistance = 0.1 // px

        fun NodeLink.getNodeChildUuid(uuid: UUID) : UUID? = if (this.firstNodeUuid == uuid) secondNodeUuid else if (this.secondNodeUuid == uuid) firstNodeUuid else null

        fun NodeLink.getNodeChildAngle(nodes : MutableList<Node>, uuid: UUID) : Angle? {
            val firstNode = nodes.getNode(this.firstNodeUuid)
            val secondNode = nodes.getNode(this.secondNodeUuid)

            if ( (firstNode == null) || (secondNode == null) ) return null

            val returnAngle = if (this.firstNodeUuid == uuid) firstNode.angleBetween(secondNode)
            else secondNode.angleBetween(firstNode)

//            println("getting angle $returnAngle between $firstNode and $secondNode")

            return returnAngle.normalized
        }

        fun MutableList<NodeLink>.removeNode(nodes : MutableList<Node>, uuid : UUID) {
            this.getNodeLinks(uuid).let { this.removeAll(it) }
            nodes.remove( nodes.getNode(uuid) )
        }

        fun MutableList<NodeLink>.getNodeLineList(nodes : MutableList<Node>) : List<Pair<Point, Point>?> {

            val returnNodeLineList : MutableList<Pair<Point, Point>> = mutableListOf()

            this.forEach { nodeLink -> returnNodeLineList.add(Pair(nodes.getNode(nodeLink.firstNodeUuid)!!.position, nodes.getNode(nodeLink.secondNodeUuid)!!.position) ) }

            return returnNodeLineList
        }

        //link order does not matter with getNodeLink
        fun MutableList<NodeLink>.getNodeLink(firstUuid: UUID, secondUuid: UUID) : NodeLink? =
            this.firstOrNull { nodeLink -> nodeLink.firstNodeUuid == firstUuid && nodeLink.secondNodeUuid == secondUuid }
             ?: this.firstOrNull { nodeLink -> nodeLink.firstNodeUuid == secondUuid && nodeLink.secondNodeUuid == firstUuid }

        fun MutableList<NodeLink>.areNodesLinked(firstUuid: UUID, secondUuid: UUID) : Boolean = getNodeLink(firstUuid, secondUuid) != null

        fun MutableList<NodeLink>.addNodeLink(nodes: MutableList<Node>, firstUuid : UUID, secondUuid: UUID) : Boolean = if ( !areNodesLinked(firstUuid, secondUuid) && nodes.getNode(firstUuid) != null && nodes.getNode(secondUuid) != null ) this.add( NodeLink(firstUuid, secondUuid) ) else false

        fun MutableList<NodeLink>.addNodeLinks(nodeLinksToAdd : MutableList<NodeLink>) : Unit = nodeLinksToAdd.forEach { nodeLinkToAdd -> if ( !areNodesLinked(nodeLinkToAdd.firstNodeUuid, nodeLinkToAdd.secondNodeUuid) ) this.add( nodeLinkToAdd ) }

        fun MutableList<NodeLink>.removeNodeLink(firstUuid : UUID, secondUuid: UUID) { this.remove(this.getNodeLink(firstUuid, secondUuid)) }

        fun MutableList<NodeLink>.removeNodeLink(nodeLink : NodeLink) { this.removeNodeLink(nodeLink.firstNodeUuid, nodeLink.secondNodeUuid) }

        fun MutableList<NodeLink>.getNodeLinks(uuid: UUID): MutableList<NodeLink> = this.filter { nodeLink -> nodeLink.firstNodeUuid == uuid || nodeLink.secondNodeUuid == uuid }.toMutableList()

        fun MutableList<NodeLink>.getNodeLinks(uuids: List<UUID>) : MutableList<NodeLink> = this.filter { nodeLink -> uuids.contains(nodeLink.firstNodeUuid) || uuids.contains(nodeLink.secondNodeUuid) }.toMutableList()

        fun MutableList<NodeLink>.getNodeChildrenUuids(uuid: UUID, parentToExcludeUuid : UUID = uuid) : MutableList<UUID> = this.getNodeLinks(uuid).filter { nodeLink -> nodeLink.getNodeChildUuid(uuid)!! != parentToExcludeUuid }.map{ filteredLink -> filteredLink.getNodeChildUuid(uuid) }.filterNotNull().distinct().toMutableList()

        fun MutableList<NodeLink>.getNodeChildrenNodeAngles(nodes: MutableList<Node>, uuid: UUID) : MutableList<Pair<Node, Angle>> {

            val returnList : MutableList<Pair<Node, Angle>> = mutableListOf()

            this.getNodeLinks(uuid).forEach { childLink -> returnList.add(Pair(nodes.getNode(childLink.getNodeChildUuid(uuid)!!) ?: Node(), childLink.getNodeChildAngle(nodes, uuid) ?: Angle.fromDegrees(0) ) ) }

            return returnList
        }

        fun MutableList<NodeLink>.getNodeChildrenAngles(nodes: MutableList<Node>, uuid: UUID) : MutableList<Angle> {

            return this.getNodeLinks( uuid ).map{ childLink -> childLink.getNodeChildAngle(nodes, uuid) ?: Angle.fromDegrees(0) }.toMutableList()

        }

        //noise goes from 0 to 100
        fun Pair<Node?, Node?>.buildNodeLinkLine(noise : Int = 0, nodeDescription : String) : INodeMesh {

            if ( (this.first == null) || (this.second == null) ) return NodeMesh()

            if (this.first!!.position == this.second!!.position) return NodeMesh()

            val nodeLineList = mutableListOf<Node>()
            val nodeLineLinkList = mutableListOf<NodeLink>()

            val startNode = this.first!!
            val endNode = this.second!!
            val linkDistance = consolidateNodeDistance + 1

            val cappedNoise = if (noise < 0) 0 else if (noise > 100) 100 else noise
            //val angle = Angle.between(startNode.position, endNode.position)

//            println ("nodeLine start: $startNode step $linkDistance")

            nodeLineList.addNode( startNode, nodeDescription )
            var previousNode = startNode
            var currentNode = startNode

            var currentPosition : Point = currentNode.position
            var currentNoisePosition : Point
            var currentPositionOffsetDistance : Double

            when {
                (endNode.position.x >= startNode.position.x) && (endNode.position.y < startNode.position.y) -> {
                    val angle = Angle.fromRadians(atan((startNode.position.y - endNode.position.y) / (endNode.position.x - startNode.position.x)))

                    //current position in middle of line
                    currentPosition = Point(currentPosition.x + linkDistance * cos(angle), currentPosition.y - linkDistance * sin(angle))

                    //get offset distance due to noise
                    currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                    //find noise position at right angle of line angle
                    currentNoisePosition = Point ( currentPosition.x - currentPositionOffsetDistance * cos(Angle.fromDegrees(90) - angle)
                        , currentPosition.y - currentPositionOffsetDistance * sin(Angle.fromDegrees(90) - angle))

                    currentNode = Node(position = currentNoisePosition)
//                    println("1) node at ${currentNode}, angle ${angle.degrees}")

                    while ( (endNode.position.x >= currentPosition.x) && (endNode.position.y < currentPosition.y) ) {
                        nodeLineList.addNode(currentNode, nodeDescription )
                        nodeLineLinkList.addNodeLink(nodeLineList, previousNode.uuid, currentNode.uuid)
                        previousNode = currentNode

                        //current position in middle of line
                        currentPosition = Point(currentPosition.x + linkDistance * cos(angle), currentPosition.y - linkDistance * sin(angle))

                        //get offset distance due to noise
                        currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                        //find cappedNoise position at right angle of line angle
                        currentNoisePosition = Point ( currentPosition.x - currentPositionOffsetDistance * cos(Angle.fromDegrees(90) - angle)
                            , currentPosition.y - currentPositionOffsetDistance * sin(Angle.fromDegrees(90) - angle))

                        currentNode = Node(position = currentNoisePosition)
//                       println("1) node at ${currentNode}, angle ${angle.degrees}")
                    }
                }
                (endNode.position.x < startNode.position.x) && (endNode.position.y < startNode.position.y) -> {
                    val angle = Angle.fromRadians(atan((startNode.position.y - endNode.position.y) / (startNode.position.x - endNode.position.x)))

                    //current position in middle of line
                    currentPosition = Point(currentPosition.x - linkDistance * cos(angle), currentPosition.y - linkDistance * sin(angle))

                    //get offset distance due to noise
                    currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                    //find noise position at right angle of line angle
                    currentNoisePosition = Point ( currentPosition.x - currentPositionOffsetDistance * cos(Angle.fromDegrees(90) - angle)
                        , currentPosition.y + currentPositionOffsetDistance * sin(Angle.fromDegrees(90) - angle))

                    currentNode = Node( position = currentNoisePosition )
 //                   println("2) node at ${currentNode}, angle ${angle.degrees}")

                    while ( (endNode.position.x < currentPosition.x) && (endNode.position.y < currentPosition.y) ) {
                        nodeLineList.addNode(currentNode, nodeDescription)
                        nodeLineLinkList.addNodeLink(nodeLineList, previousNode.uuid, currentNode.uuid)
                        previousNode = currentNode

                        //current position in middle of line
                        currentPosition = Point(currentPosition.x - linkDistance * cos(angle), currentPosition.y - linkDistance * sin(angle))

                        //get offset distance due to noise
                        currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                        //find noise position at right angle of line angle
                        currentNoisePosition = Point ( currentPosition.x - currentPositionOffsetDistance * cos(Angle.fromDegrees(90) - angle)
                            , currentPosition.y + currentPositionOffsetDistance * sin(Angle.fromDegrees(90) - angle))

                        currentNode = Node( position = currentNoisePosition )
                    //                       println("2) node at ${currentNode}, angle ${angle.degrees}")
                    }
                }
                (endNode.position.x < startNode.position.x) && (endNode.position.y >= startNode.position.y) -> {
                    val angle = Angle.fromRadians(atan((endNode.position.y - startNode.position.y) / (startNode.position.x - endNode.position.x)))

                    //current position in middle of line
                    currentPosition = Point(currentPosition.x - linkDistance * cos(angle), currentPosition.y + linkDistance * sin(angle))

                    //get offset distance due to noise
                    currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                    //find noise position at right angle of line angle
                    currentNoisePosition = Point ( currentPosition.x + currentPositionOffsetDistance * cos(Angle.fromDegrees(90) - angle)
                        , currentPosition.y + currentPositionOffsetDistance * sin(Angle.fromDegrees(90) - angle))

                    currentNode = Node( position = currentNoisePosition )
                    //                  println("3) node at ${currentNode}, angle ${angle.degrees}")

                    while ( (endNode.position.x < currentPosition.x) && (endNode.position.y >= currentPosition.y) ) {
                        nodeLineList.addNode(currentNode, nodeDescription)
                        nodeLineLinkList.addNodeLink(nodeLineList, previousNode.uuid, currentNode.uuid)
                        previousNode = currentNode

                        //current position in middle of line
                        currentPosition = Point(currentPosition.x - linkDistance * cos(angle), currentPosition.y + linkDistance * sin(angle))

                        //get offset distance due to noise
                        currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                        //find noise position at right angle of line angle
                        currentNoisePosition = Point ( currentPosition.x + currentPositionOffsetDistance * cos(Angle.fromDegrees(90) - angle)
                            , currentPosition.y + currentPositionOffsetDistance * sin(Angle.fromDegrees(90) - angle))

                        currentNode = Node(position = currentNoisePosition )
    //                    println("3) node at ${currentNode}, angle ${angle.degrees}")
                    }
                }
                (endNode.position.x >= startNode.position.x) && (endNode.position.y >= startNode.position.y) -> {
                    val angle = Angle.fromRadians(atan((endNode.position.y - startNode.position.y) / (endNode.position.x - startNode.position.x)))

                    //current position in middle of line
                    currentPosition = Point(currentPosition.x + linkDistance * cos(angle), currentPosition.y + linkDistance * sin(angle))

                    //get offset distance due to noise
                    currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                    //find noise position at right angle of line angle
                    currentNoisePosition = Point ( currentPosition.x + currentPositionOffsetDistance * cos(Angle.fromDegrees(90) - angle)
                        , currentPosition.y - currentPositionOffsetDistance * sin(Angle.fromDegrees(90) - angle))

                    currentNode = Node(position = currentNoisePosition)
                    //                  println("4) node at ${currentNode}, angle ${angle.degrees}")

                    while ( (endNode.position.x >= currentPosition.x) && (endNode.position.y >= currentPosition.y) ) {
                        nodeLineList.addNode(currentNode, nodeDescription)
                        nodeLineLinkList.addNodeLink(nodeLineList, previousNode.uuid, currentNode.uuid)
                        previousNode = currentNode

                        //current position in middle of line
                        currentPosition = Point(currentPosition.x + linkDistance * cos(angle), currentPosition.y + linkDistance * sin(angle))

                        //get offset distance due to noise
                        currentPositionOffsetDistance = Probability(0, (cappedNoise.toDouble() / 100 * linkDistance).toInt() ).getValue()

                        //find noise position at right angle of line angle
                        currentNoisePosition = Point ( currentPosition.x + currentPositionOffsetDistance * cos(Angle.fromDegrees(90) - angle)
                            , currentPosition.y - currentPositionOffsetDistance * sin(Angle.fromDegrees(90) - angle))

                        currentNode = Node(position = currentNoisePosition)
     //                   println("4) node at ${currentNode}, angle ${angle.degrees}")
                    }
                }
            }

            nodeLineList.addNode(endNode, nodeDescription)
            nodeLineLinkList.addNodeLink(nodeLineList, previousNode.uuid, endNode.uuid)

 //           println ("nodeLine end: $endNode, ${nodeLineList.size} nodes")
 //           nodeLineLinkList.forEach{ println("nodeLine link: $it") }

            return NodeMesh(description = "path${Random.nextInt(256)}", nodes = nodeLineList, nodeLinks = nodeLineLinkList)
        }

        fun MutableList<NodeLink>.buildNodeLinkLines(nodes: MutableList<Node>, noise : Int = 0, nodeDescription : String) : INodeMesh {

            val returnNodeMesh = NodeMesh()

            this.forEach { link ->
//                println("before nodes: ${link.firstNodeUuid}, ${link.secondNodeUuid}")
                returnNodeMesh.addMesh(Pair(nodes.getNode(link.firstNodeUuid), nodes.getNode(link.secondNodeUuid)).buildNodeLinkLine(noise, nodeDescription) )
                returnNodeMesh.nodeLinks.removeNodeLink(link)
//                returnNodeMesh.nodeLinks.forEach { println("after nodeLinks: $it")}
            }

            return returnNodeMesh
        }

        fun MutableList<NodeLink>.getRandomNextNodeAngle(nodes : MutableList<Node>, refNode : Node) : Angle {
            val childrenAngles = getNodeChildrenAngles(nodes, refNode.uuid)

            val randomAngle = childrenAngles[Random.nextInt(childrenAngles.size)]

//            println("randomAngle : $randomAngle")

            return randomAngle
        }

        fun MutableList<NodeLink>.getNextNodeAngle(nodes : MutableList<Node>, refNode : Node, refAngle : Angle) : Pair<Node, Angle> {

//            println ("refNode, refAngle: $refNode, $refAngle")

            val childrenNodeAngles = getNodeChildrenNodeAngles(nodes, refNode.uuid)

//            childrenNodeAngles.forEach { println ("childrenNodeAngles ${it.first}, ${it.second}")}

            val combinedAngleNodes : MutableMap<Angle, Node> = mutableMapOf()

            childrenNodeAngles.forEach { combinedAngleNodes[it.second] = it.first }

            childrenNodeAngles.forEach { combinedAngleNodes[it.second + Angle.fromDegrees(360)] = it.first }

            childrenNodeAngles.forEach { combinedAngleNodes[it.second - Angle.fromDegrees(360)] = it.first }

//            combinedAngleNodes.forEach { println ("combinedAngles ${it.key}, ${it.value}")}

            val combinedAngles = combinedAngleNodes.keys.toList().filter { abs(it.degrees - refAngle.degrees) < 60 }.sortedBy { abs(it.degrees - refAngle.degrees) }

            if (combinedAngles.isNullOrEmpty()) return Pair(refNode, refAngle)

            val returnNode = combinedAngleNodes[combinedAngles[0]] ?: return Pair(refNode, refAngle)

            val returnAngle = childrenNodeAngles.firstOrNull { it.first == returnNode }?.second ?: return Pair(refNode, refAngle)

            val returnNodeAngle = Pair(returnNode, returnAngle)

//            println ("returnNodeAngle: $returnNodeAngle")

            return returnNodeAngle
        }

        fun MutableList<NodeLink>.getNextAngle(nodes : MutableList<Node>, refNode : Node, refAngle : Angle, rangeAngle : Angle) : Angle {

//            println ("refNode, refAngle, rangeAngle: $refNode, $refAngle, $rangeAngle")

            val childrenAngles = getNodeChildrenAngles(nodes, refNode.uuid)

            val childrenAnglesOver360 = childrenAngles.map { Angle.fromDegrees(360) + it }

            val childrenAnglesUnder0 = childrenAngles.map { Angle.fromDegrees(360) - it }

            val combinedAngles = (childrenAngles + childrenAnglesOver360 + childrenAnglesUnder0).sortedBy { it.degrees }

            val nextAngle = when {
                (rangeAngle.degrees > 0) -> {
                    val nextAngles = combinedAngles.filter { it > refAngle && it <= refAngle + rangeAngle }
                    if (!nextAngles.isNullOrEmpty()) nextAngles[0] else refAngle + rangeAngle
                }
                (rangeAngle.degrees < 0) -> {
                    val nextAngles = combinedAngles.filter { it < refAngle && it >= refAngle + rangeAngle }
                    if (!nextAngles.isNullOrEmpty()) nextAngles[0] else refAngle + rangeAngle
                }
                else -> refAngle + rangeAngle
            }

//            println ("returnNextAngle: ${nextAngle.normalized}")

            return nextAngle.normalized
        }

        val linkAngleMinDegree = 30

    //consolidates nodeLinks if degree difference of minor links is < linkAngleMinDegrees
     fun MutableList<NodeLink>.consolidateNodeLinkNodes(nodes : MutableList<Node>, nodeUuid : UUID) : MutableList<Node> {
    //        println("pre-consolidation nodeLinks: $this")

        val childNodeAngles = this.getNodeChildrenNodeAngles(nodes, nodeUuid)
        var bestAngle = 0
        var bestAngleDiff = childNodeAngles.size * linkAngleMinDegree

        (0 until linkAngleMinDegree).forEach { angleIdx ->
            var thisAngleDiff = 0

            childNodeAngles.forEach {
                //    println ("angle modded: ${(it.second.degrees % angleMinDegrees).toInt()} ")
                thisAngleDiff += abs(angleIdx - (it.second.degrees % linkAngleMinDegree).toInt() )
            }

//            println ("thisAngleDiff @ $angleIdx: $thisAngleDiff")

            if (thisAngleDiff <= bestAngleDiff) {
                bestAngleDiff = thisAngleDiff
                bestAngle = angleIdx
            }

//            println ("bestAngleDiff @ $bestAngle: $bestAngleDiff")
        }

        val checkNodeAngles = mutableMapOf<Angle, Node>()

        childNodeAngles.forEach { checkNodeAngles[it.second] = it.first }

        childNodeAngles.forEach { checkNodeAngles[it.second + Angle.fromDegrees(360)] = it.first }

        childNodeAngles.forEach { checkNodeAngles[it.second - Angle.fromDegrees(360)] = it.first }

        val keepNodeAngles = mutableMapOf<Node, Angle>()

        checkNodeAngles.keys.sortedBy {
            if (it.degrees.toInt() >= 0)
                abs (( it.degrees % linkAngleMinDegree) - bestAngle).toInt()
            else abs ( linkAngleMinDegree - (it.degrees % -linkAngleMinDegree) - bestAngle ).toInt()
        }.forEach { checkNodeAngle ->

            var keepNodeAngle = false

            if (!keepNodeAngles.containsKey(checkNodeAngles[checkNodeAngle])) {

                val checkMod = if (checkNodeAngle.degrees.toInt() >= 0)
                    abs((checkNodeAngle.degrees % linkAngleMinDegree) - bestAngle).toInt()
                else abs(linkAngleMinDegree - (checkNodeAngle.degrees % -linkAngleMinDegree) - bestAngle).toInt()

//                println("node:${checkNodeAngles[checkNodeAngle]} angle:${checkNodeAngle.degrees} mod:${checkMod}")

                if (keepNodeAngles.isNotEmpty()) {
                    if (checkNodeAngle.degrees.toInt() in 0..360) keepNodeAngle = true

                    keepNodeAngles.forEach { keepNode ->
                        if ( abs(checkNodeAngle.degrees - keepNode.value.degrees) < linkAngleMinDegree) {
//                            println("angle diff : ${abs(checkNodeAngle.degrees - keepNode.value.degrees)}")
                            keepNodeAngle = false
                        }
                    }
                } else if (checkNodeAngle.degrees.toInt() in 0..360) keepNodeAngle = true

                if (keepNodeAngle) {
//                    println("adding ${checkNodeAngles[checkNodeAngle]} = $checkNodeAngle")
                    keepNodeAngles[checkNodeAngles[checkNodeAngle]!!] = checkNodeAngle
                }
            }
        }
//        keepNodeAngles.forEach { println ("keepNodesAngles: ${it.key} : ${it.value}")}

         val removeNodeAngles = mutableMapOf<Node, Angle>()

         childNodeAngles.forEach { if (!keepNodeAngles.containsKey(it.first)) removeNodeAngles[it.first] = it.second }

//         removeNodeAngles.forEach { println ("removeNodesAngles: ${it.key} : ${it.value}")}

         return removeNodeAngles.map { it.key }.toMutableList()
       }

        fun MutableList<NodeLink>.consolidateNodeLinks(nodes : MutableList<Node>) : MutableList<NodeLink> {
//        println("checking for nodelinks to consolidate...")
           val returnNodeLinks = this

            nodes.sortedBy { it.uuid.toString() }.forEach { node ->
                this.consolidateNodeLinkNodes(nodes, node.uuid).forEach { returnNode ->
//                    println ("removing link(${node.uuid}, ${returnNode.uuid})")
                    returnNodeLinks.removeNodeLink(node.uuid, returnNode.uuid)
                }
            }

            return returnNodeLinks
        }

        fun MutableList<NodeLink>.pruneNodeLinks(nodes : MutableList<Node>) : MutableList<NodeLink> {

            val returnNodeLinks = this

            nodes.sortedBy { it.uuid.toString() }.forEach { refNode ->
                val nearestNodes = nodes.nearestNodesOrderedAsc(refNode)

                //cycle through quarter of nearest nodelinks to check for intersect
                if (!nearestNodes.isNullOrEmpty()) {
                    val quarterNearestNodesSize = nearestNodes.size / 4

                    val refNodeLinks = this.getNodeLinks(refNode.uuid)

                    (0..quarterNearestNodesSize).forEach { closeNodeAscIdx ->
                        val closeNodeLinks = this.getNodeLinks(nearestNodes[closeNodeAscIdx].uuid)

                        refNodeLinks.forEach { refNodeLink ->
                            closeNodeLinks.forEach { closeNodeLink ->
                                if ( Pair(nodes.getNode(refNodeLink.firstNodeUuid)!!.position, nodes.getNode (refNodeLink.secondNodeUuid)!!.position).intersects(
                                        Pair(nodes.getNode(closeNodeLink.firstNodeUuid)!!.position, nodes.getNode (closeNodeLink.secondNodeUuid)!!.position)
                                    )
                                ) if ( refNodeLink.firstNodeUuid.toString() < closeNodeLink.firstNodeUuid.toString() )
                                    returnNodeLinks.removeNodeLink(closeNodeLink)
                                else
                                    returnNodeLinks.removeNodeLink(refNodeLink)
                            }
                        }
                    }
                }
            }

            return returnNodeLinks
        }

    }

}