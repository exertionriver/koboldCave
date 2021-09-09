package org.river.exertion.koboldCave.node

import org.river.exertion.koboldCave.leaf.ILeaf
import org.river.exertion.Angle
import org.river.exertion.koboldCave.Line
import org.river.exertion.koboldCave.Line.Companion.getIntersection
import org.river.exertion.koboldCave.node.Node.Companion.addNode
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.Node.Companion.getNode
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine.Companion.getLineLength
import org.river.exertion.normalizeDeg
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeLink(val firstNodeUuid : UUID, val secondNodeUuid : UUID
        , var attributes : List<String> = listOf() ) {

    constructor(firstNode : Node, secondNode : Node, attributes : List<String> = listOf()) : this (
        firstNodeUuid = firstNode.uuid
        , secondNodeUuid = secondNode.uuid
        , attributes = attributes
    )

    constructor(copyNodeLink : NodeLink
                , updFirstNodeUuid: UUID = copyNodeLink.firstNodeUuid
                , updSecondNodeUuid: UUID = copyNodeLink.secondNodeUuid) : this (
        firstNodeUuid = updFirstNodeUuid
        , secondNodeUuid = updSecondNodeUuid
    )

    fun getDistance(nodes : MutableList<Node>) : Double? {
        val firstNode = nodes.getNode(firstNodeUuid)
        val secondNode = nodes.getNode(secondNodeUuid)

        return if (firstNode != null && secondNode != null) firstNode.position.dst(secondNode.position).toDouble() else null
    }

    override fun toString() = "${NodeLink::class.simpleName}($firstNodeUuid, $secondNodeUuid)"

    enum class NextAngle { LEFT, RIGHT, FORWARD, BACKWARD, CENTER }

    companion object {
        val consolidateNodeDistance = ILeaf.NextDistancePx * 3 / 4
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

            return returnAngle.normalizeDeg()
        }

        fun MutableList<NodeLink>.removeNode(nodes : MutableList<Node>, uuid : UUID) {
            this.getNodeLinks(uuid).let { this.removeAll(it) }
            nodes.remove( nodes.getNode(uuid) )
        }

        fun MutableList<NodeLink>.getLineList(nodes : MutableList<Node>) : List<Line> {

            val returnNodeLineList : MutableList<Line> = mutableListOf()

            this.forEach { nodeLink -> returnNodeLineList.add(Line(nodes.getNode(nodeLink.firstNodeUuid)!!.position, nodes.getNode(nodeLink.secondNodeUuid)!!.position) ) }

            return returnNodeLineList
        }

        //link order matters with getNodeLink, least is first
        fun MutableList<NodeLink>.getNodeLink(firstUuid: UUID, secondUuid: UUID) : NodeLink? =
            this.firstOrNull { nodeLink -> nodeLink.firstNodeUuid.toString() == firstUuid.toString() && nodeLink.secondNodeUuid.toString() == secondUuid.toString() }
             ?: this.firstOrNull { nodeLink -> nodeLink.firstNodeUuid.toString() == secondUuid.toString() && nodeLink.secondNodeUuid.toString() == firstUuid.toString() }

        fun MutableList<NodeLink>.getRandomNodeLink() : NodeLink = this[Random.nextInt(this.size)]

        fun MutableList<NodeLink>.areNodesLinked(firstUuid: UUID, secondUuid: UUID) : Boolean = getNodeLink(firstUuid, secondUuid) != null

        fun MutableList<NodeLink>.addNodeLink(nodes: MutableList<Node>, firstUuid : UUID, secondUuid: UUID) : Boolean {
            if ( !areNodesLinked(firstUuid, secondUuid) && nodes.getNode(firstUuid) != null && nodes.getNode(secondUuid) != null )
                return if (firstUuid.toString() < secondUuid.toString())
                    this.add( NodeLink( firstUuid, secondUuid) )
                else
                    this.add( NodeLink( secondUuid, firstUuid) )
            return false
        }

        fun MutableList<NodeLink>.addNodeLinks(nodes: MutableList<Node>, nodeLinksToAdd : MutableList<NodeLink>) : Unit = nodeLinksToAdd.forEach { nodeLinkToAdd -> this.addNodeLink( nodes, nodeLinkToAdd.firstNodeUuid, nodeLinkToAdd.secondNodeUuid ) }

        fun MutableList<NodeLink>.removeNodeLink(firstUuid : UUID, secondUuid: UUID) { this.remove(this.getNodeLink(firstUuid, secondUuid)) }

        fun MutableList<NodeLink>.removeNodeLink(nodeLink : NodeLink) { this.removeNodeLink(nodeLink.firstNodeUuid, nodeLink.secondNodeUuid) }

        fun MutableList<NodeLink>.removeNodeLinks(nodeLinksToRemove : MutableList<NodeLink>) : Unit = nodeLinksToRemove.forEach { nodeLinkToRemove -> this.remove( nodeLinkToRemove ) }

        fun MutableList<NodeLink>.getNodeLinks(uuid: UUID): MutableList<NodeLink> = this.filter { nodeLink -> nodeLink.firstNodeUuid == uuid || nodeLink.secondNodeUuid == uuid }.toMutableList()

        fun MutableList<NodeLink>.getNodeLinks(uuids: List<UUID>) : MutableList<NodeLink> = this.filter { nodeLink -> uuids.contains(nodeLink.firstNodeUuid) || uuids.contains(nodeLink.secondNodeUuid) }.toMutableList()

        fun MutableList<NodeLink>.getNodeChildrenUuids(uuid: UUID, parentToExcludeUuid : UUID = uuid) : MutableList<UUID> = this.getNodeLinks(uuid).filter { nodeLink -> nodeLink.getNodeChildUuid(uuid)!! != parentToExcludeUuid }.map{ filteredLink -> filteredLink.getNodeChildUuid(uuid) }.filterNotNull().distinct().toMutableList()

        fun MutableList<NodeLink>.getNodeChildrenNodeAngles(nodes: MutableList<Node>, uuid: UUID) : MutableList<Pair<Node, Angle>> {

            val returnList : MutableList<Pair<Node, Angle>> = mutableListOf()

            this.getNodeLinks(uuid).forEach { childLink -> returnList.add(Pair(nodes.getNode(childLink.getNodeChildUuid(uuid)!!) ?: Node(), childLink.getNodeChildAngle(nodes, uuid) ?: 0F ) ) }

            return returnList
        }

        fun MutableList<NodeLink>.getNodeChildrenAngles(nodes: MutableList<Node>, uuid: UUID) : MutableList<Angle> {

            return this.getNodeLinks( uuid ).map{ childLink -> childLink.getNodeChildAngle(nodes, uuid) ?: 0F }.toMutableList()

        }

        fun MutableList<NodeLink>.removeOrphanLinks(nodes: MutableList<Node>) : MutableList<NodeLink> {

            val returnLinks = mutableListOf<NodeLink>()

            val nodeUuids = nodes.map { it.uuid }

            this.forEach { nodeLink -> if( nodeUuids.contains(nodeLink.firstNodeUuid) && nodeUuids.contains(nodeLink.secondNodeUuid) ) returnLinks.add(nodeLink) }

            return returnLinks
        }

        fun MutableList<NodeLink>.getRandomNextNodeAngle(nodes : MutableList<Node>, refNode : Node) : Angle {
            val childrenAngles = getNodeChildrenAngles(nodes, refNode.uuid)

            val randomAngle = childrenAngles[Random.nextInt(childrenAngles.size)]

//            println("randomAngle : $randomAngle")

            return randomAngle
        }

        fun MutableList<NodeLink>.getNextAngle(nodes : MutableList<Node>, refNode : Node, refAngle : Angle, nextAngle : NextAngle) : Angle {
            return this.getNextNodeAngle(nodes, refNode, refAngle, nextAngle).second
        }

        fun MutableList<NodeLink>.getNextNodeAngle(nodes : MutableList<Node>, refNode : Node, refAngle : Angle, nextAngle : NextAngle = NextAngle.FORWARD) : Pair<Node, Angle> {

            var returnNodeAngle : Pair<Node, Angle> = Pair(refNode, 0f)

            when {
                (nextAngle == NextAngle.RIGHT) -> {
                    val childrenAngles = getNodeChildrenAngles(nodes, refNode.uuid).sortedBy { it }

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
                    val childrenAngles = getNodeChildrenAngles(nodes, refNode.uuid).sortedByDescending { it }

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

    //consolidates nodeLinks if degree difference of minor links is < linkAngleMinDegrees
    fun MutableList<NodeLink>.consolidateNodeLinksNode(nodes : MutableList<Node>, nodeUuid : UUID) : MutableList<NodeLink> {

        val childNodeAngles = this.getNodeChildrenNodeAngles(nodes, nodeUuid)

        val childNodeSliceMap = mutableMapOf<Int, MutableList<Node>>()
        (0 until 360).forEach { childNodeSliceMap[it] = mutableListOf() }

        childNodeAngles.forEach { childNodeAngle ->
            var minIdx = childNodeAngle.second.toInt() - (linkAngleMinDegree)// / 2 + 1)
            var maxIdx = childNodeAngle.second.toInt() + (linkAngleMinDegree)// / 2 + 1)

            (minIdx..maxIdx).forEach {
                var mapIdx = it

                if (it < 0) mapIdx += 360
                if (it >= 360) mapIdx -= 360

                childNodeSliceMap[mapIdx]!!.add(childNodeAngle.first)
            }

        }

       // println("childNodeSliceMap: $childNodeSliceMap")
        var angleDescSortedList = childNodeSliceMap.values.sortedByDescending { it.count() }
        var sliceFound = angleDescSortedList[0].count() > 1

        //if no nodelinks are in the same slice
        if (!sliceFound) return mutableListOf()

    //        println("consolidating nodeLinks for ${nodes.getNode(nodeUuid)}:")
    //        this.forEach { println(it) }

    //        println ("slice count: ${angleDescSortedList[0].count()}")
    //        childNodeSliceMap.entries.sortedByDescending { it.value.count() }.forEach { println("angle: ${it.key}, count: ${it.value.count()}") }

    //        println("**found slice")
        val removeNodeLinks = mutableListOf<NodeLink>()

        val refNode = nodes.getNode(nodeUuid)!!

        while (sliceFound) {
            var maxLength = 0f
            var nodeToRemoveLink = angleDescSortedList[0][0]

            angleDescSortedList[0].forEach { checkNode ->
                val lineLength = mutableListOf(refNode, checkNode).getLineLength()

    //               println("checking $refNode against $checkNode: $lineLength")

                if (lineLength > maxLength) {
                    maxLength = lineLength
                    nodeToRemoveLink = checkNode
                }
            }

            removeNodeLinks.add(this.getNodeLink(refNode.uuid, nodeToRemoveLink.uuid)!!)

            childNodeSliceMap.filter{ it.value.contains(nodeToRemoveLink) }.forEach { it.value.remove(nodeToRemoveLink) }

            angleDescSortedList = childNodeSliceMap.values.sortedByDescending { it.count() }
            sliceFound = angleDescSortedList[0].count() > 1

    //            println ("slice count: ${angleDescSortedList[0].count()}")
    //            childNodeSliceMap.entries.sortedByDescending { it.value.count() }.forEach { println("angle: ${it.key}, count: ${it.value.count()}") }
            }

         return removeNodeLinks
       }

        fun MutableList<NodeLink>.consolidateNodeLinks(nodes : MutableList<Node>) : MutableList<NodeLink> {
//        println("checking for nodelinks to consolidate...")
           val returnNodeLinks = this
            val checkNodeLinks = this.toList()

            nodes.sortedBy { it.uuid.toString() }.forEach { node ->
                this.filter{ it.firstNodeUuid == node.uuid || it.secondNodeUuid == node.uuid }.toMutableList().consolidateNodeLinksNode(nodes, node.uuid).forEach { returnNodeLink ->
//                    println ("removing link(${node.uuid}, ${returnNode.uuid})")
                    returnNodeLinks.removeNodeLink(returnNodeLink)
                }
            }

        return returnNodeLinks
    }

        fun MutableList<NodeLink>.nodifyIntersects(nodes : MutableList<Node>) : MutableList<Node> {

            val returnNodes = mutableListOf<Node>().apply { addAll(nodes) }
            val addNodeLinks = mutableListOf<NodeLink>()
            val removeNodeLinks = mutableListOf<NodeLink>()

            val returnNodePositions = mutableSetOf<Pair<Int, Int>>().apply positions@ { returnNodes.forEach { this@positions.add(Pair(it.position.x.toInt(), it.position.y.toInt())) } }
            val removeNodeLinkUuids = mutableSetOf<String>()

//            var addCounter = 0

//            println("total nodeLinks: ${this.size}; total nodes: ${nodes.size}")

            this.sortedBy { it.firstNodeUuid.toString() + it.secondNodeUuid.toString() }.forEach { refNodeLink ->

//                println("refNodeLink: $refNodeLink")

                this.filter { it.firstNodeUuid.toString() > refNodeLink.firstNodeUuid.toString() }.forEach { checkNodeLink ->

  //                  println("checkNodeLink: $checkNodeLink")

                    val intersect = Line(nodes.getNode(refNodeLink.firstNodeUuid)!!.position, nodes.getNode (refNodeLink.secondNodeUuid)!!.position).getIntersection(
                        Line(nodes.getNode(checkNodeLink.firstNodeUuid)!!.position, nodes.getNode (checkNodeLink.secondNodeUuid)!!.position)
                    )

                    if ( (intersect != null) && !returnNodePositions.contains(Pair(intersect.x.toInt(), intersect.y.toInt())) ) {
                        val newNode = Node(position = intersect)
                        returnNodePositions.add(Pair(intersect.x.toInt(), intersect.y.toInt()))

   //                     println("adding node $newNode, ${addCounter++}")
                        returnNodes.addNode( newNode )
                        addNodeLinks.add(NodeLink(newNode.uuid, refNodeLink.firstNodeUuid))
                        addNodeLinks.add(NodeLink(newNode.uuid, refNodeLink.secondNodeUuid))
                        addNodeLinks.add(NodeLink(newNode.uuid, checkNodeLink.firstNodeUuid))
                        addNodeLinks.add(NodeLink(newNode.uuid, checkNodeLink.secondNodeUuid))

                        if (!removeNodeLinkUuids.contains(refNodeLink.firstNodeUuid.toString() + refNodeLink.secondNodeUuid.toString())) {
                            removeNodeLinks.add(refNodeLink)
                            removeNodeLinkUuids.add(refNodeLink.firstNodeUuid.toString() + refNodeLink.secondNodeUuid.toString())
                        }

                        if (!removeNodeLinkUuids.contains(checkNodeLink.firstNodeUuid.toString() + checkNodeLink.secondNodeUuid.toString())) {
                            removeNodeLinks.add(checkNodeLink)
                            removeNodeLinkUuids.add(checkNodeLink.firstNodeUuid.toString() + checkNodeLink.secondNodeUuid.toString())
                        }
                    }
                }
            }
//            println("addNodeLinks: ${addNodeLinks.size}; removeNodeLinks: ${removeNodeLinks.size}")

            this.addNodeLinks(returnNodes, addNodeLinks)
            this.removeNodeLinks(removeNodeLinks)

//            println("total nodeLinks: ${this.size}; total nodes: ${returnNodes.size}")

            return returnNodes
        }
    }

}