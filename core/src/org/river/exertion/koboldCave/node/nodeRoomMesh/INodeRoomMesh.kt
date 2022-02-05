package org.river.exertion.koboldCave.node.nodeRoomMesh

import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoomLink
import java.util.*

interface INodeRoomMesh {

    val uuid : UUID

    val description : String

    var nodeRooms : MutableSet<NodeRoom>

    var nodeRoomLinks : MutableSet<NodeRoomLink>
}