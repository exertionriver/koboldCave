package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import leaf.ILeaf
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Node(val uuid: UUID = UUID.randomUUID(Random.Default), val position : Point, val childNodeUuids : MutableList<UUID> = mutableListOf()) {

    constructor(leaf : ILeaf) : this (
        uuid = leaf.uuid
        , position = leaf.position
        , childNodeUuids = getChildNodeUuids(leaf)
    )

    constructor(copyNode : Node
        , updUuid : UUID = copyNode.uuid
        , updPosition : Point = copyNode.position
        , updChildNodeUuids : MutableList<UUID> = copyNode.childNodeUuids) : this (
        uuid = updUuid
        , position = updPosition
        , childNodeUuids = updChildNodeUuids
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

    override fun toString() = "${Node::class.simpleName}($uuid) : $position, ${childNodeUuids.size} : $childNodeUuids"

    companion object {
        fun emptyNode() = Node(position = Point(0, 0))

        fun getChildNodeUuids(leaf : ILeaf) : MutableList<UUID> {
            val returnChildNodesUUIDs : MutableList<UUID> = mutableListOf()

            if (!leaf.parentEmpty()) returnChildNodesUUIDs.add(returnChildNodesUUIDs.size, leaf.getParentLeaf()!!.uuid)

            if (!leaf.childrenEmpty()) leaf.getChildrenLeavesList()?.forEach { childLeaf -> returnChildNodesUUIDs.add(returnChildNodesUUIDs.size, childLeaf.uuid)}

            return returnChildNodesUUIDs
        }
    }
}