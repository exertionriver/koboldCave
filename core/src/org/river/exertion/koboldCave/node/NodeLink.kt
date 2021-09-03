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
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeLink(val firstNodeUuid : UUID, val secondNodeUuid : UUID
        , var attributes : List<String> = listOf() ) {

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

        fun MutableList<NodeLink>.getNextNodeAngle(nodes : MutableList<Node>, refNode : Node, refAngle : Angle) : Pair<Node, Angle> {

//            println ("refNode, refAngle: $refNode, $refAngle")

            val childrenNodeAngles = getNodeChildrenNodeAngles(nodes, refNode.uuid)

//            childrenNodeAngles.forEach { println ("childrenNodeAngles ${it.first}, ${it.second}")}

            val combinedAngleNodes : MutableMap<Angle, Node> = mutableMapOf()

            childrenNodeAngles.forEach { combinedAngleNodes[it.second] = it.first }

            childrenNodeAngles.forEach { combinedAngleNodes[it.second + 360F] = it.first }

            childrenNodeAngles.forEach { combinedAngleNodes[it.second - 360F] = it.first }

//            combinedAngleNodes.forEach { println ("combinedAngles ${it.key}, ${it.value}")}

            val combinedAngles = combinedAngleNodes.keys.toList().filter { abs(it - refAngle) < 60F }.sortedBy { abs(it - refAngle) }

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

            val childrenAnglesOver360 = childrenAngles.map { 360F + it }

            val childrenAnglesUnder0 = childrenAngles.map { 360F - it }

            val combinedAngles = (childrenAngles + childrenAnglesOver360 + childrenAnglesUnder0).sortedBy { it }

            val nextAngle = when {
                (rangeAngle > 0F) -> {
                    val nextAngles = combinedAngles.filter { it > refAngle && it <= refAngle + rangeAngle }
                    if (nextAngles.isNotEmpty()) nextAngles[0] else refAngle + rangeAngle
                }
                (rangeAngle < 0F) -> {
                    val nextAngles = combinedAngles.filter { it < refAngle && it >= refAngle + rangeAngle }
                    if (nextAngles.isNotEmpty()) nextAngles[0] else refAngle + rangeAngle
                }
                else -> refAngle + rangeAngle
            }

//            println ("returnNextAngle: ${nextAngle.normalized}")

            return nextAngle.normalizeDeg()
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
/*
        var bestAngle = 0
        var bestAngleDiff = childNodeAngles.size * linkAngleMinDegree.toFloat()

        println("pre-consolidation childNodeAngles:")
        childNodeAngles.forEach { println(it) }

        (0 until linkAngleMinDegree).forEach { angleIdx ->
            var thisAngleDiff = 0F

            childNodeAngles.forEach {
                //    println ("angle modded: ${(it.second.degrees % angleMinDegrees).toInt()} ")
                thisAngleDiff += abs(angleIdx - (it.second % linkAngleMinDegree) )
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

        childNodeAngles.forEach { checkNodeAngles[it.second + 360F] = it.first }

        childNodeAngles.forEach { checkNodeAngles[it.second - 360F] = it.first }

        val keepNodeAngles = mutableMapOf<Node, Angle>()

        checkNodeAngles.keys.sortedBy {
            if (it >= 0F)
                abs (( it % linkAngleMinDegree) - bestAngle).toInt()
            else abs ( linkAngleMinDegree - (it % -linkAngleMinDegree) - bestAngle ).toInt()
        }.forEach { checkNodeAngle ->

            var keepNodeAngle = false

            if (!keepNodeAngles.containsKey(checkNodeAngles[checkNodeAngle])) {

                val checkMod = if (checkNodeAngle >= 0F)
                    abs((checkNodeAngle % linkAngleMinDegree) - bestAngle).toInt()
                else abs(linkAngleMinDegree - (checkNodeAngle % -linkAngleMinDegree) - bestAngle).toInt()

//                println("node:${checkNodeAngles[checkNodeAngle]} angle:${checkNodeAngle.degrees} mod:${checkMod}")

                if (keepNodeAngles.isNotEmpty()) {
                    if (checkNodeAngle.toInt() in 0..360) keepNodeAngle = true

                    keepNodeAngles.forEach { keepNode ->
                        if ( abs(checkNodeAngle - keepNode.value) < linkAngleMinDegree) {
//                            println("angle diff : ${abs(checkNodeAngle.degrees - keepNode.value.degrees)}")
                            keepNodeAngle = false
                        }
                    }
                } else if (checkNodeAngle.toInt() in 0..360) keepNodeAngle = true

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
*/
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
/*
        fun MutableList<NodeLink>.pruneNodeLinks(nodes : MutableList<Node>) : MutableList<NodeLink> {

            val returnNodeLinks = this
            val checkNodes = nodes.toList()

            checkNodes.sortedBy { it.uuid.toString() }.forEach { refNode ->
                val nearestNodes = nodes.nearestNodesOrderedAsc(refNode)

                val refNodeLinks = this.getNodeLinks(refNode.uuid)

//                refNodeLinks.forEach { println("refNodeLink: $it") }

                nearestNodes.forEach { checkNode ->
                    if (checkNode.uuid.toString() > refNode.uuid.toString()) {

                        val checkNodeLinks = this.getNodeLinks(checkNode.uuid)

//                        checkNodeLinks.forEach { println("checkNodeLink: $it") }

                        refNodeLinks.forEach { refNodeLink ->
                            checkNodeLinks.forEach { checkNodeLink ->
                                val checkSet = mutableSetOf(refNodeLink.firstNodeUuid, refNodeLink.secondNodeUuid, checkNodeLink.firstNodeUuid, checkNodeLink.secondNodeUuid)
                                    if (checkSet.size == 4) // check four unique points for intersect
                                        if (nodes.getNode(refNodeLink.firstNodeUuid) != null
                                            && nodes.getNode(refNodeLink.secondNodeUuid) != null
                                            && nodes.getNode(checkNodeLink.firstNodeUuid) != null
                                            && nodes.getNode(checkNodeLink.secondNodeUuid) != null) {
                                        if ( Pair(nodes.getNode(refNodeLink.firstNodeUuid)!!.position, nodes.getNode (refNodeLink.secondNodeUuid)!!.position).intersects(
                                            Pair(nodes.getNode(checkNodeLink.firstNodeUuid)!!.position, nodes.getNode (checkNodeLink.secondNodeUuid)!!.position)
                                        ) ) {
//                                        println ("removing $checkNodeLink")
                                            returnNodeLinks.removeNodeLink(checkNodeLink)
                                        }
                                }
                            }
                        }
                    }
                }
            }
            return returnNodeLinks
        }
*/
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