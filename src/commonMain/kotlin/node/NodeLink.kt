package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import leaf.ILeaf
import node.Node.Companion.getNode

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

    fun getAngle(nodes : MutableList<Node>, toNodeUuid : UUID) : Angle? {
        val firstNode = nodes.getNode(firstNodeUuid)
        val secondNode = nodes.getNode(secondNodeUuid)

        return if (firstNode != null && secondNode != null) {
            when {
                (toNodeUuid == firstNode.uuid) -> Angle.between(secondNode.position, firstNode.position)
                (toNodeUuid == secondNode.uuid) -> Angle.between(firstNode.position, secondNode.position)
                else -> null
            }
        } else null
    }

    override fun toString() = "${NodeLink::class.simpleName}($firstNodeUuid, $secondNodeUuid)"

    companion object {
        val consolidateNodeDistance = ILeaf.LeafDistancePx / 2
        val linkNodeDistance = ILeaf.LeafDistancePx

        fun NodeLink.getNodeChildUuid(uuid: UUID) : UUID? = if (this.firstNodeUuid == uuid) secondNodeUuid else if (this.secondNodeUuid == uuid) firstNodeUuid else null

        fun MutableList<NodeLink>.removeNode(nodes : MutableList<Node>, uuid : UUID) {
            this.getNodeLinks(uuid).let { this.removeAll(it) }
            nodes.remove( nodes.getNode(uuid) )
        }

        fun MutableList<NodeLink>.getNodeLineList(nodes : MutableList<Node>) : List<Pair<Point, Point>?> {

            val returnNodeLineList : MutableList<Pair<Point, Point>> = mutableListOf()

            this.forEach { nodeLink -> returnNodeLineList.add(Pair(nodes.getNode(nodeLink.firstNodeUuid)!!.position, nodes.getNode(nodeLink.secondNodeUuid)!!.position) ) }

            return returnNodeLineList
        }


        //only to be used by functions that handle first / second uuid interchangeability
        private fun MutableList<NodeLink>.getNodeLink(firstUuid: UUID, secondUuid: UUID) : NodeLink? = this.firstOrNull { nodeLink -> nodeLink.firstNodeUuid == firstUuid && nodeLink.secondNodeUuid == secondUuid }

        fun MutableList<NodeLink>.areNodesLinked(firstUuid: UUID, secondUuid: UUID) : Boolean = ( getNodeLink(firstUuid, secondUuid) != null || getNodeLink(secondUuid, firstUuid) != null )

        fun MutableList<NodeLink>.addNodeLink(firstUuid : UUID, secondUuid: UUID) : Boolean = if ( !areNodesLinked(firstUuid, secondUuid) ) this.add( NodeLink(firstUuid, secondUuid) ) else false

        fun MutableList<NodeLink>.addNodeLinks(nodeLinksToAdd : MutableList<NodeLink>) : Unit = nodeLinksToAdd.forEach { nodeLinkToAdd -> if ( !areNodesLinked(nodeLinkToAdd.firstNodeUuid, nodeLinkToAdd.secondNodeUuid) ) this.add( nodeLinkToAdd ) }

        fun MutableList<NodeLink>.getNodeLinks(uuid: UUID) : MutableList<NodeLink> = this.filter { nodeLink -> nodeLink.firstNodeUuid == uuid || nodeLink.secondNodeUuid == uuid }.toMutableList()

        fun MutableList<NodeLink>.getNodeChildrenUuids(uuid: UUID, parentToExcludeUuid : UUID = uuid) : MutableList<UUID> = this.getNodeLinks(uuid).filter { nodeLink -> nodeLink.getNodeChildUuid(uuid)!! != parentToExcludeUuid }.map{ filteredLink -> filteredLink.getNodeChildUuid(uuid)!! }.distinct().toMutableList()
    }
}