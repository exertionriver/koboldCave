package org.river.exertion.koboldCave.node.nodeMesh

import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.Node.Companion.getNode
import java.util.*

class NodeRoomLink(val firstNodeRoomUuid : UUID, val secondNodeRoomUuid : UUID
                   , var attributes : List<String> = listOf() ) {

    constructor(copyNodeRoomLink : NodeRoomLink
                , updFirstNodeUuid: UUID = copyNodeRoomLink.firstNodeRoomUuid
                , updSecondNodeUuid: UUID = copyNodeRoomLink.secondNodeRoomUuid) : this (
        firstNodeRoomUuid = updFirstNodeUuid
        , secondNodeRoomUuid = updSecondNodeUuid
    )

    fun getDistance(nodes : MutableSet<Node>) : Double? {
        val firstNode = nodes.getNode(firstNodeRoomUuid)
        val secondNode = nodes.getNode(secondNodeRoomUuid)

        return if (firstNode != null && secondNode != null) firstNode.position.dst(secondNode.position).toDouble() else null
    }

    override fun toString() = "${NodeRoomLink::class.simpleName}($firstNodeRoomUuid, $secondNodeRoomUuid)"

}