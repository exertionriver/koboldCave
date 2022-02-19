package org.river.exertion.geom.room

import org.river.exertion.geom.node.nodeMesh.NodeRoom
import java.util.*

interface IRoom {

    val uuid : UUID

    val description : String

    val nodeRoom : NodeRoom?

}