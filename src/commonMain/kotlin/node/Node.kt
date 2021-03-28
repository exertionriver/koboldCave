package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import leaf.ILeaf
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Node(val uuid: UUID = UUID.randomUUID(Random.Default), val position : Point) {

    constructor(leaf : ILeaf) : this (
        uuid = leaf.uuid
        , position = leaf.position
    )

    constructor(copyNode : Node
        , updUuid : UUID = copyNode.uuid
        , updPosition : Point = copyNode.position) : this (
        uuid = updUuid
        , position = updPosition
    )

    fun nearestNodeRoom(nodeRooms : List<NodeRoom>) : NodeRoom {

        var minimumDistance = 1024.0
        lateinit var nearestNodeRoom : NodeRoom

        nodeRooms.forEach { nodeRoom ->
            val currentDistance = position.distanceTo(nodeRoom.position)

            if (currentDistance < minimumDistance) {
                minimumDistance = currentDistance
                nearestNodeRoom = nodeRoom
            }
        }

        return nearestNodeRoom
    }

    override fun equals(other: Any?): Boolean {
        return (this.uuid == (other as Node).uuid)
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString() = "${Node::class.simpleName}($uuid) : $position"

    companion object {
        fun emptyNode() = Node(position = Point(0, 0))
    }
}