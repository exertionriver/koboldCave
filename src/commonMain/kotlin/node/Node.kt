package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import leaf.Leaf
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Node(val uuid: UUID = UUID.randomUUID(Random.Default), val position : Point, val childNodeUUIDs : MutableList<UUID>? = mutableListOf()) {

/*    constructor(leaf : Leaf) : this (
        uuid = leaf.uuid
        , position = leaf.position
        , childNodeUUIDs = getChildNodeUUIDs(leaf)
    )

    fun getChildrenNodes(nodes : List<Node>) {

        MutableList

    } childNodeUUIDs.forEach { }

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

    companion object {
        fun emptyNode() = Node(position = Point(0, 0))

        fun getChildNodeUUIDs(leaf : Leaf) : MutableList<UUID>? {
            val returnChildNodesUUIDs : MutableList<UUID> = mutableListOf()

            if (!leaf.parentEmpty()) returnChildNodesUUIDs.add(returnChildNodesUUIDs.size, leaf.parentLeaf!!.uuid)

            if (!leaf.childrenEmpty()) leaf.childrenLeaves!!.forEach { childLeaf -> returnChildNodesUUIDs.add(returnChildNodesUUIDs.size, childLeaf.uuid)}

            return if (returnChildNodesUUIDs.size == 0) null else returnChildNodesUUIDs
        }
    }

    override fun equals(other: Any?): Boolean {
        return (this.uuid == (other as Node).uuid)
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }*/
}