package org.river.exertion.koboldCave.room

import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import java.util.*

interface IRoom {

    val uuid : UUID

    val description : String

    val nodeRoom : NodeRoom?

}