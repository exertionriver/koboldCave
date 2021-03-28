package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import leaf.ILeaf

@ExperimentalUnsignedTypes
class NodeLink(val firstNodeUuid : UUID, val secondNodeUuid : UUID) {

    constructor(firstLeaf : ILeaf, secondLeaf : ILeaf) : this (
        firstNodeUuid = firstLeaf.uuid
        , secondNodeUuid = secondLeaf.uuid
    )

    constructor(copyNodeLink : NodeLink
                , updFirstNodeUuid: UUID = copyNodeLink.firstNodeUuid
                , updSecondNodeUuid: UUID = copyNodeLink.secondNodeUuid) : this (
        firstNodeUuid = updFirstNodeUuid
        , secondNodeUuid = updSecondNodeUuid
    )


    fun getDistance(nodes : List<Node>) : Double? {
        val firstNode = nodes.node(firstNodeUuid)
        val secondNode = nodes.node(secondNodeUuid)

        return if (firstNode != null && secondNode != null) Point.distance(firstNode.position, secondNode.position) else null
    }

    fun getFirstToSecondAngle(nodes : List<Node>) : Angle? {
        val firstNode = nodes.node(firstNodeUuid)
        val secondNode = nodes.node(secondNodeUuid)

        return if (firstNode != null && secondNode != null) Angle.between(firstNode.position, secondNode.position) else null
    }

    fun getSecondToFirstAngle(nodes : List<Node>) : Angle? {
        val firstNode = nodes.node(firstNodeUuid)
        val secondNode = nodes.node(secondNodeUuid)

        return if (firstNode != null && secondNode != null) Angle.between(secondNode.position, firstNode.position) else null
    }

    override fun toString() = "${NodeLink::class.simpleName}($firstNodeUuid, $secondNodeUuid)"

    companion object {

        fun List<Node>.node(uuid : UUID) : Node? {
            return this.firstOrNull { node -> node.uuid == uuid }
        }

    }
}