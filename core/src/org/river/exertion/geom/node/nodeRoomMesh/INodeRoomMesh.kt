package org.river.exertion.geom.node.nodeRoomMesh

import org.river.exertion.geom.node.nodeMesh.NodeRoom
import org.river.exertion.geom.node.nodeMesh.NodeRoomLink
import java.util.*

interface INodeRoomMesh {

    val uuid : UUID

    val description : String

    var nodeRooms : MutableSet<NodeRoom>

    var nodeRoomLinks : MutableSet<NodeRoomLink>
}