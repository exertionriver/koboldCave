package org.river.exertion.geom.node

import org.river.exertion.Angle
import org.river.exertion.NextDistancePx
import org.river.exertion.geom.Line3
import org.river.exertion.geom.node.Node3.Companion.angleBetween
import org.river.exertion.geom.node.Node3.Companion.getNode
import org.river.exertion.normalizeDeg
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class NodeLink3(val firstNodeUuid : UUID, val secondNodeUuid : UUID
                , var attributes : List<String> = listOf() ) {

    constructor(firstNode : Node3, secondNode : Node3, attributes : List<String> = listOf()) : this (
        firstNodeUuid = firstNode.uuid
        , secondNodeUuid = secondNode.uuid
        , attributes = attributes
    )

    constructor(copyNodeLink : NodeLink3
                , updFirstNodeUuid: UUID = copyNodeLink.firstNodeUuid
                , updSecondNodeUuid: UUID = copyNodeLink.secondNodeUuid) : this (
        firstNodeUuid = updFirstNodeUuid
        , secondNodeUuid = updSecondNodeUuid
    )

    fun getDistance(nodes : MutableSet<Node3>) : Double? {
        val firstNode = nodes.getNode(firstNodeUuid)
        val secondNode = nodes.getNode(secondNodeUuid)

        return if (firstNode != null && secondNode != null) firstNode.position.dst(secondNode.position).toDouble() else null
    }

    override fun toString() = "${NodeLink3::class.simpleName}($firstNodeUuid, $secondNodeUuid)"

    enum class NextAngle { LEFT, RIGHT, FORWARD, BACKWARD, CENTER }

    companion object {
        val consolidateNodeDistance = NextDistancePx * 3 / 4
        val linkNodeDistance = NextDistancePx
        val stackedNodeDistance = 0.1 // px

        fun NodeLink3.getNodeChildUuid(uuid: UUID) : UUID? = if (this.firstNodeUuid == uuid) secondNodeUuid else if (this.secondNodeUuid == uuid) firstNodeUuid else null

        fun NodeLink3.getNodeChildAngle(nodes : MutableSet<Node3>, uuid: UUID) : Angle? {
            val firstNode = nodes.getNode(this.firstNodeUuid)
            val secondNode = nodes.getNode(this.secondNodeUuid)

            if ( (firstNode == null) || (secondNode == null) ) return null

            val returnAngle = if (this.firstNodeUuid == uuid) firstNode.angleBetween(secondNode)
            else secondNode.angleBetween(firstNode)

//            println("getting angle $returnAngle between $firstNode and $secondNode")

            return returnAngle.normalizeDeg()
        }

        fun MutableSet<NodeLink3>.removeNode(nodes : MutableSet<Node3>, uuid : UUID) {
            this.getNodeLinks(uuid).let { this.removeAll(it) }
            nodes.remove( nodes.getNode(uuid) )
        }

        fun MutableSet<NodeLink3>.getLineSet(nodes : MutableSet<Node3>) : MutableSet<Line3> {

            val returnNodeLineSet : MutableSet<Line3> = mutableSetOf()

            this.forEach { nodeLink ->
                val firstNodePosition = nodes.getNode(nodeLink.firstNodeUuid)!!.position
                val secondNodePosition = nodes.getNode(nodeLink.secondNodeUuid)!!.position

//                println("firstNodePosition: $firstNodePosition; secondNodePosition: $secondNodePosition")
                returnNodeLineSet.add(Line3(firstNodePosition, secondNodePosition) )
            }

            return returnNodeLineSet
        }

        //link order matters with getNodeLink, least is first
        fun MutableSet<NodeLink3>.getNodeLink(firstUuid: UUID, secondUuid: UUID) : NodeLink3? =
            this.firstOrNull { nodeLink -> nodeLink.firstNodeUuid.toString() == firstUuid.toString() && nodeLink.secondNodeUuid.toString() == secondUuid.toString() }
             ?: this.firstOrNull { nodeLink -> nodeLink.firstNodeUuid.toString() == secondUuid.toString() && nodeLink.secondNodeUuid.toString() == firstUuid.toString() }

        fun MutableSet<NodeLink3>.getRandomNodeLink() : NodeLink3 = this.toList()[Random.nextInt(this.size)]

        fun MutableSet<NodeLink3>.areNodesLinked(firstUuid: UUID, secondUuid: UUID) : Boolean = getNodeLink(firstUuid, secondUuid) != null

        fun MutableSet<NodeLink3>.addNodeLink(nodes: MutableSet<Node3>, firstUuid : UUID, secondUuid: UUID) : Boolean {
            if ( !areNodesLinked(firstUuid, secondUuid) && nodes.getNode(firstUuid) != null && nodes.getNode(secondUuid) != null )
                return if (firstUuid.toString() < secondUuid.toString())
                    this.add( NodeLink3( firstUuid, secondUuid) )
                else
                    this.add( NodeLink3( secondUuid, firstUuid) )
            return false
        }

        fun MutableSet<NodeLink3>.addNodeLinks(nodes: MutableSet<Node3>, nodeLinksToAdd : MutableSet<NodeLink3>) : Unit = nodeLinksToAdd.forEach { nodeLinkToAdd -> this.addNodeLink( nodes, nodeLinkToAdd.firstNodeUuid, nodeLinkToAdd.secondNodeUuid ) }

        fun MutableSet<NodeLink3>.removeNodeLink(firstUuid : UUID, secondUuid: UUID) { this.remove(this.getNodeLink(firstUuid, secondUuid)) }

        fun MutableSet<NodeLink3>.removeNodeLink(nodeLink : NodeLink3) { this.removeNodeLink(nodeLink.firstNodeUuid, nodeLink.secondNodeUuid) }

        fun MutableSet<NodeLink3>.removeNodeLinks(nodeLinksToRemove : MutableSet<NodeLink3>) : Unit = nodeLinksToRemove.forEach { nodeLinkToRemove -> this.remove( nodeLinkToRemove ) }

        fun MutableSet<NodeLink3>.getNodeLinks(uuid: UUID): MutableSet<NodeLink3> = this.filter { nodeLink -> nodeLink.firstNodeUuid == uuid || nodeLink.secondNodeUuid == uuid }.toMutableSet()

        fun MutableSet<NodeLink3>.getNodeLinks(uuids: List<UUID>) : MutableSet<NodeLink3> = this.filter { nodeLink -> uuids.contains(nodeLink.firstNodeUuid) || uuids.contains(nodeLink.secondNodeUuid) }.toMutableSet()

        fun MutableSet<NodeLink3>.getNodeChildrenUuids(uuid: UUID, parentToExcludeUuid : UUID = uuid) : MutableSet<UUID> = this.getNodeLinks(uuid).filter { nodeLink -> nodeLink.getNodeChildUuid(uuid)!! != parentToExcludeUuid }.map{ filteredLink -> filteredLink.getNodeChildUuid(uuid) }.filterNotNull().toMutableSet()

        fun MutableSet<NodeLink3>.getNodeChildrenNodeAngles(nodes: MutableSet<Node3>, uuid: UUID) : MutableSet<Pair<Node3, Angle>> {

            val returnList : MutableSet<Pair<Node3, Angle>> = mutableSetOf()

            this.getNodeLinks(uuid).forEach { childLink -> returnList.add(Pair(nodes.getNode(childLink.getNodeChildUuid(uuid)!!) ?: Node3(), childLink.getNodeChildAngle(nodes, uuid) ?: 0F ) ) }

            return returnList
        }

        fun MutableSet<NodeLink3>.getNodeChildrenLinkAngles(nodes: MutableSet<Node3>, uuid: UUID) : MutableMap<NodeLink3, Angle> {

            val returnMap = mutableMapOf<NodeLink3, Angle>()

            this.getNodeLinks( uuid ).map{ childLink ->
                val angle = childLink.getNodeChildAngle(nodes, uuid) ?: 0F
                returnMap[childLink] = angle
            }

            return returnMap
        }

        fun MutableSet<NodeLink3>.getRandomNextNodeLinkAngle(nodes : MutableSet<Node3>, refNode : Node3) : Pair<NodeLink3, Angle> {

            val childrenNodeLinkAngles = getNodeChildrenLinkAngles(nodes, refNode.uuid)

            val idxToReturn = Random.nextInt(childrenNodeLinkAngles.size)

            val randomLinkAngleKey = childrenNodeLinkAngles.keys.toList()[idxToReturn]

//            println("randomAngle : $randomAngle")

            return Pair(randomLinkAngleKey, childrenNodeLinkAngles[randomLinkAngleKey]!!)
        }

        fun MutableSet<NodeLink3>.getNextAngle(nodes : MutableSet<Node3>, refNode : Node3, refAngle : Angle, nextAngle : NextAngle) : Angle {
            return this.getNextNodeAngle(nodes, refNode, refAngle, nextAngle).second
        }

        fun MutableSet<NodeLink3>.getNextNodeAngle(nodes : MutableSet<Node3>, refNode : Node3, refAngle : Angle, nextAngle : NextAngle = NextAngle.FORWARD) : Pair<Node3, Angle> {

            var returnNodeAngle : Pair<Node3, Angle> = Pair(refNode, 0f)

            when {
                (nextAngle == NextAngle.RIGHT) -> {
                    val childrenAngles = getNodeChildrenLinkAngles(nodes, refNode.uuid).values.sortedBy { it }

                    //check to the 'right' of ref angle, outside of 15f range
                    childrenAngles.forEach { checkAngle ->
//                        println("right checkAngle: $checkAngle; refAngle min linkAngleMin:${(refAngle - linkAngleMinDegree).normalizeDeg()}")
                        if (checkAngle < (refAngle - linkAngleMinDegree).normalizeDeg() ) returnNodeAngle = Pair(refNode, checkAngle)
                    }

                    if (returnNodeAngle.second == 0f) {
//                        println("right last angle:${childrenAngles[childrenAngles.size - 1]}")

                        returnNodeAngle = Pair(refNode, childrenAngles[childrenAngles.size - 1])
                    }
                }
                (nextAngle == NextAngle.LEFT) -> {
                    val childrenAngles = getNodeChildrenLinkAngles(nodes, refNode.uuid).values.sortedByDescending { it }

                    childrenAngles.forEach { checkAngle ->
//                        println("left checkAngle: $checkAngle; refAngle min linkAngleMin:${(refAngle - linkAngleMinDegree).normalizeDeg()}")
                        if (checkAngle > (refAngle + linkAngleMinDegree).normalizeDeg() ) returnNodeAngle = Pair(refNode, checkAngle)
                    }

                    if (returnNodeAngle.second == 0f) {
//                        println("left last angle:${childrenAngles[childrenAngles.size - 1]}")

                        returnNodeAngle = Pair(refNode, childrenAngles[childrenAngles.size - 1])
                    }
                }
                else -> { //NextAngle.FORWARD or BACKWARD
                    val childrenNodeAngles = getNodeChildrenNodeAngles(nodes, refNode.uuid)

                    val nextRefAngle = if (nextAngle == NextAngle.BACKWARD) (refAngle + 180f).normalizeDeg() else refAngle
                    val angleLimit = if (nextAngle == NextAngle.BACKWARD) 90 else 180

                    var angleIter = 0f
                    var foundNode = false

                    //probably a better way to do this, with min and max
                    while (!foundNode && (angleIter < angleLimit)) {

                        val checkAngleLeft = (nextRefAngle - angleIter).normalizeDeg()
                        val checkAngleRight = (nextRefAngle + angleIter).normalizeDeg()

                        childrenNodeAngles.forEach { childNodeAngle ->
                            if ( abs(childNodeAngle.second - checkAngleLeft) < 2f) {
                                returnNodeAngle = childNodeAngle
                                foundNode = true
                            }
                            else if ( abs(childNodeAngle.second - checkAngleRight) < 2f) {
                                returnNodeAngle = childNodeAngle
                                foundNode = true
                            }
                        }
                        angleIter++
                    }

                    //lookahead for forward angle
                    if (nextAngle == NextAngle.FORWARD) {
                        val nextNextAngle = this.getNextNodeAngle(nodes, returnNodeAngle.first, returnNodeAngle.second, NextAngle.CENTER)

                        returnNodeAngle = Pair(returnNodeAngle.first, nextNextAngle.second)
                    }

                    //can happen when backed up against a wall with no exits
                    if (!foundNode) returnNodeAngle = Pair(refNode, refAngle)
                }
            }

//            println ("$refNode, $refAngle, $nextAngle, $returnNodeAngle")

            return returnNodeAngle
        }

        val linkAngleMinDegree = 15
    }
}