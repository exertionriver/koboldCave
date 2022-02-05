package org.river.exertion.koboldCave.node

import org.river.exertion.Angle
import org.river.exertion.NextDistancePx
import org.river.exertion.koboldCave.Line
import org.river.exertion.koboldCave.Line.Companion.getIntersection
import org.river.exertion.koboldCave.node.Node.Companion.addNode
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.Node.Companion.getNode
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine.Companion.getLineLength
import org.river.exertion.normalizeDeg
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

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

    fun getDistance(nodes : MutableSet<Node>) : Double? {
        val firstNode = nodes.getNode(firstNodeUuid)
        val secondNode = nodes.getNode(secondNodeUuid)

        return if (firstNode != null && secondNode != null) firstNode.position.dst(secondNode.position).toDouble() else null
    }

    override fun toString() = "${NodeLink::class.simpleName}($firstNodeUuid, $secondNodeUuid)"

    enum class NextAngle { LEFT, RIGHT, FORWARD, BACKWARD, CENTER }

    companion object {
        val consolidateNodeDistance = NextDistancePx * 3 / 4
        val linkNodeDistance = NextDistancePx
        val stackedNodeDistance = 0.1 // px

        fun NodeLink.getNodeChildUuid(uuid: UUID) : UUID? = if (this.firstNodeUuid == uuid) secondNodeUuid else if (this.secondNodeUuid == uuid) firstNodeUuid else null

        fun NodeLink.getNodeChildAngle(nodes : MutableSet<Node>, uuid: UUID) : Angle? {
            val firstNode = nodes.getNode(this.firstNodeUuid)
            val secondNode = nodes.getNode(this.secondNodeUuid)

            if ( (firstNode == null) || (secondNode == null) ) return null

            val returnAngle = if (this.firstNodeUuid == uuid) firstNode.angleBetween(secondNode)
            else secondNode.angleBetween(firstNode)

//            println("getting angle $returnAngle between $firstNode and $secondNode")

            return returnAngle.normalizeDeg()
        }

        fun MutableSet<NodeLink>.removeNode(nodes : MutableSet<Node>, uuid : UUID) {
            this.getNodeLinks(uuid).let { this.removeAll(it) }
            nodes.remove( nodes.getNode(uuid) )
        }

        fun MutableSet<NodeLink>.getLineSet(nodes : MutableSet<Node>) : MutableSet<Line> {

            val returnNodeLineSet : MutableSet<Line> = mutableSetOf()

            this.forEach { nodeLink ->
                val firstNodePosition = nodes.getNode(nodeLink.firstNodeUuid)!!.position
                val secondNodePosition = nodes.getNode(nodeLink.secondNodeUuid)!!.position

//                println("firstNodePosition: $firstNodePosition; secondNodePosition: $secondNodePosition")
                returnNodeLineSet.add(Line(firstNodePosition, secondNodePosition) )
            }

            return returnNodeLineSet
        }

        //link order matters with getNodeLink, least is first
        fun MutableSet<NodeLink>.getNodeLink(firstUuid: UUID, secondUuid: UUID) : NodeLink? =
            this.firstOrNull { nodeLink -> nodeLink.firstNodeUuid.toString() == firstUuid.toString() && nodeLink.secondNodeUuid.toString() == secondUuid.toString() }
             ?: this.firstOrNull { nodeLink -> nodeLink.firstNodeUuid.toString() == secondUuid.toString() && nodeLink.secondNodeUuid.toString() == firstUuid.toString() }

        fun MutableSet<NodeLink>.getRandomNodeLink() : NodeLink = this.toList()[Random.nextInt(this.size)]

        fun MutableSet<NodeLink>.areNodesLinked(firstUuid: UUID, secondUuid: UUID) : Boolean = getNodeLink(firstUuid, secondUuid) != null

        fun MutableSet<NodeLink>.addNodeLink(nodes: MutableSet<Node>, firstUuid : UUID, secondUuid: UUID) : Boolean {
            if ( !areNodesLinked(firstUuid, secondUuid) && nodes.getNode(firstUuid) != null && nodes.getNode(secondUuid) != null )
                return if (firstUuid.toString() < secondUuid.toString())
                    this.add( NodeLink( firstUuid, secondUuid) )
                else
                    this.add( NodeLink( secondUuid, firstUuid) )
            return false
        }

        fun MutableSet<NodeLink>.addNodeLinks(nodes: MutableSet<Node>, nodeLinksToAdd : MutableSet<NodeLink>) : Unit = nodeLinksToAdd.forEach { nodeLinkToAdd -> this.addNodeLink( nodes, nodeLinkToAdd.firstNodeUuid, nodeLinkToAdd.secondNodeUuid ) }

        fun MutableSet<NodeLink>.removeNodeLink(firstUuid : UUID, secondUuid: UUID) { this.remove(this.getNodeLink(firstUuid, secondUuid)) }

        fun MutableSet<NodeLink>.removeNodeLink(nodeLink : NodeLink) { this.removeNodeLink(nodeLink.firstNodeUuid, nodeLink.secondNodeUuid) }

        fun MutableSet<NodeLink>.removeNodeLinks(nodeLinksToRemove : MutableSet<NodeLink>) : Unit = nodeLinksToRemove.forEach { nodeLinkToRemove -> this.remove( nodeLinkToRemove ) }

        fun MutableSet<NodeLink>.getNodeLinks(uuid: UUID): MutableSet<NodeLink> = this.filter { nodeLink -> nodeLink.firstNodeUuid == uuid || nodeLink.secondNodeUuid == uuid }.toMutableSet()

        fun MutableSet<NodeLink>.getNodeLinks(uuids: List<UUID>) : MutableSet<NodeLink> = this.filter { nodeLink -> uuids.contains(nodeLink.firstNodeUuid) || uuids.contains(nodeLink.secondNodeUuid) }.toMutableSet()

        fun MutableSet<NodeLink>.getNodeChildrenUuids(uuid: UUID, parentToExcludeUuid : UUID = uuid) : MutableSet<UUID> = this.getNodeLinks(uuid).filter { nodeLink -> nodeLink.getNodeChildUuid(uuid)!! != parentToExcludeUuid }.map{ filteredLink -> filteredLink.getNodeChildUuid(uuid) }.filterNotNull().toMutableSet()

        fun MutableSet<NodeLink>.getNodeChildrenNodeAngles(nodes: MutableSet<Node>, uuid: UUID) : MutableSet<Pair<Node, Angle>> {

            val returnList : MutableSet<Pair<Node, Angle>> = mutableSetOf()

            this.getNodeLinks(uuid).forEach { childLink -> returnList.add(Pair(nodes.getNode(childLink.getNodeChildUuid(uuid)!!) ?: Node(), childLink.getNodeChildAngle(nodes, uuid) ?: 0F ) ) }

            return returnList
        }

        fun MutableSet<NodeLink>.getNodeChildrenLinkAngles(nodes: MutableSet<Node>, uuid: UUID) : MutableMap<NodeLink, Angle> {

            val returnMap = mutableMapOf<NodeLink, Angle>()

            this.getNodeLinks( uuid ).map{ childLink ->
                val angle = childLink.getNodeChildAngle(nodes, uuid) ?: 0F
                returnMap[childLink] = angle
            }

            return returnMap
        }

        fun MutableSet<NodeLink>.removeOrphanLinks(nodes: MutableSet<Node>) : MutableSet<NodeLink> {

            val returnLinks = mutableSetOf<NodeLink>()

            val nodeUuids = nodes.map { it.uuid }

            this.forEach { nodeLink -> if( nodeUuids.contains(nodeLink.firstNodeUuid) && nodeUuids.contains(nodeLink.secondNodeUuid) ) returnLinks.add(nodeLink) }

            return returnLinks
        }

        fun MutableSet<NodeLink>.getRandomNextNodeLinkAngle(nodes : MutableSet<Node>, refNode : Node) : Pair<NodeLink, Angle> {

            val childrenNodeLinkAngles = getNodeChildrenLinkAngles(nodes, refNode.uuid)

            val idxToReturn = Random.nextInt(childrenNodeLinkAngles.size)

            val randomLinkAngleKey = childrenNodeLinkAngles.keys.toList()[idxToReturn]

//            println("randomAngle : $randomAngle")

            return Pair(randomLinkAngleKey, childrenNodeLinkAngles[randomLinkAngleKey]!!)
        }

        fun MutableSet<NodeLink>.getNextAngle(nodes : MutableSet<Node>, refNode : Node, refAngle : Angle, nextAngle : NextAngle) : Angle {
            return this.getNextNodeAngle(nodes, refNode, refAngle, nextAngle).second
        }

        fun MutableSet<NodeLink>.getNextNodeAngle(nodes : MutableSet<Node>, refNode : Node, refAngle : Angle, nextAngle : NextAngle = NextAngle.FORWARD) : Pair<Node, Angle> {

            var returnNodeAngle : Pair<Node, Angle> = Pair(refNode, 0f)

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

    //consolidates nodeLinks if degree difference of minor links is < linkAngleMinDegrees
    fun MutableSet<NodeLink>.consolidateNodeLinksNode(nodes : MutableSet<Node>, nodeUuid : UUID) : MutableSet<NodeLink> {

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
        if (!sliceFound) return mutableSetOf()

    //        println("consolidating nodeLinks for ${nodes.getNode(nodeUuid)}:")
    //        this.forEach { println(it) }

    //        println ("slice count: ${angleDescSortedList[0].count()}")
    //        childNodeSliceMap.entries.sortedByDescending { it.value.count() }.forEach { println("angle: ${it.key}, count: ${it.value.count()}") }

    //        println("**found slice")
        val removeNodeLinks = mutableSetOf<NodeLink>()

        val refNode = nodes.getNode(nodeUuid)!!

        while (sliceFound) {
            var maxLength = 0f
            var nodeToRemoveLink = angleDescSortedList[0][0]

            angleDescSortedList[0].forEach { checkNode ->
                val lineLength = mutableSetOf(refNode, checkNode).getLineLength()

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

        fun MutableSet<NodeLink>.consolidateNodeLinks(nodes : MutableSet<Node>) : MutableSet<NodeLink> {
//        println("checking for nodelinks to consolidate...")
           val returnNodeLinks = this
            val checkNodeLinks = this

            nodes.sortedBy { it.uuid.toString() }.forEach { node ->
                this.filter{ it.firstNodeUuid == node.uuid || it.secondNodeUuid == node.uuid }.toMutableSet().consolidateNodeLinksNode(nodes, node.uuid).forEach { returnNodeLink ->
//                    println ("removing link(${node.uuid}, ${returnNode.uuid})")
                    returnNodeLinks.removeNodeLink(returnNodeLink)
                }
            }

        return returnNodeLinks
    }

        fun MutableSet<NodeLink>.nodifyIntersects(nodes : MutableSet<Node>) : MutableSet<Node> {

            val returnNodes = mutableSetOf<Node>().apply { addAll(nodes) }
            val addNodeLinks = mutableSetOf<NodeLink>()
            val removeNodeLinks = mutableSetOf<NodeLink>()

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