package node

import Probability
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.*
import leaf.ILeaf
import node.INodeMesh.Companion.addMesh
import node.Node.Companion.addNode
import node.Node.Companion.angleBetween
import node.Node.Companion.getNode
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
        val consolidateNodeDistance = ILeaf.LeafDistancePx / 4
        val linkNodeDistance = ILeaf.LeafDistancePx

        fun NodeLink.getNodeChildUuid(uuid: UUID) : UUID? = if (this.firstNodeUuid == uuid) secondNodeUuid else if (this.secondNodeUuid == uuid) firstNodeUuid else null

        fun NodeLink.getNodeChildAngle(nodes : MutableList<Node>, uuid: UUID) : Angle? {
            val firstNode = nodes.getNode(this.firstNodeUuid)
            val secondNode = nodes.getNode(this.secondNodeUuid)

            if ( (firstNode == null) || (secondNode == null) ) return null

            val returnAngle = if (this.firstNodeUuid == uuid) firstNode.angleBetween(secondNode)
            else secondNode.angleBetween(firstNode)

            println("getting angle $returnAngle between $firstNode and $secondNode")

            return returnAngle
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

        fun MutableList<NodeLink>.removeNodeLink(firstUuid : UUID, secondUuid: UUID) { this.remove(NodeLink(firstUuid, secondUuid)); this.remove(NodeLink(secondUuid, firstUuid)) }

        fun MutableList<NodeLink>.removeNodeLink(nodeLink : NodeLink) { this.removeNodeLink(nodeLink.firstNodeUuid, nodeLink.secondNodeUuid) }

        fun MutableList<NodeLink>.getNodeLinks(uuid: UUID): MutableList<NodeLink> = this.filter { nodeLink -> nodeLink.firstNodeUuid == uuid || nodeLink.secondNodeUuid == uuid }.toMutableList()

        fun MutableList<NodeLink>.getNodeLinks(uuids: List<UUID>) : MutableList<NodeLink> = this.filter { nodeLink -> uuids.contains(nodeLink.firstNodeUuid) || uuids.contains(nodeLink.secondNodeUuid) }.toMutableList()

        fun MutableList<NodeLink>.getNodeChildrenUuids(uuid: UUID, parentToExcludeUuid : UUID = uuid) : MutableList<UUID> = this.getNodeLinks(uuid).filter { nodeLink -> nodeLink.getNodeChildUuid(uuid)!! != parentToExcludeUuid }.map{ filteredLink -> filteredLink.getNodeChildUuid(uuid) }.filterNotNull().distinct().toMutableList()

        fun MutableList<NodeLink>.getNodeChildrenAngles(nodes: MutableList<Node>, uuid: UUID, parentToExcludeUuid : UUID = uuid) : MutableList<Angle> {

            return this.getNodeLinks( uuid ).map{ filteredLink -> filteredLink.getNodeChildAngle(nodes, uuid) ?: Angle.fromDegrees(0) }.toMutableList()

        }



        //noise goes from 0 to 100
        fun Pair<Node?, Node?>.buildNodeLinkLine(noise : Int = 0) : INodeMesh {

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

            nodeLineList.addNode(startNode)
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
                        nodeLineList.addNode(currentNode)
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
                        nodeLineList.addNode(currentNode)
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
                        nodeLineList.addNode(currentNode)
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
                        nodeLineList.addNode(currentNode)
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

            nodeLineList.addNode(endNode)
            nodeLineLinkList.addNodeLink(nodeLineList, previousNode.uuid, endNode.uuid)

 //           println ("nodeLine end: $endNode, ${nodeLineList.size} nodes")
 //           nodeLineLinkList.forEach{ println("nodeLine link: $it") }

            return NodeMesh(description = "path${Random.nextInt(256)}", nodes = nodeLineList, nodeLinks = nodeLineLinkList)
        }

        fun MutableList<NodeLink>.buildNodeLinkLines(nodes: MutableList<Node>, noise : Int = 0) : INodeMesh {

            val returnNodeMesh = NodeMesh()

            this.forEach { link ->
//                println("before nodes: ${link.firstNodeUuid}, ${link.secondNodeUuid}")
                returnNodeMesh.addMesh(Pair(nodes.getNode(link.firstNodeUuid), nodes.getNode(link.secondNodeUuid)).buildNodeLinkLine(noise) )
                returnNodeMesh.nodeLinks.removeNodeLink(link)
//                returnNodeMesh.nodeLinks.forEach { println("after nodeLinks: $it")}
            }

            return returnNodeMesh
        }

        fun MutableList<NodeLink>.getRandomNextNodeAngle(nodes : MutableList<Node>, refNode : Node) : Angle {
            val childrenAngles = getNodeChildrenAngles(nodes, refNode.uuid)

            val randomAngle = childrenAngles[Random.nextInt(childrenAngles.size)]

            println("randomAngle : $randomAngle")

            return randomAngle
        }
    }
}