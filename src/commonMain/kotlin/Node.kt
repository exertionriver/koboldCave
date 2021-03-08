import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import kotlin.random.Random

class Node(val uuid: UUID = UUID.randomUUID(Random.Default), val position : Point, val childNodes : MutableList<Node> = mutableListOf()) {

    companion object {
        fun emptyNode() = Node(position = Point(0, 0))
    }

    override fun equals(other: Any?): Boolean {
        return (this.uuid == (other as Node).uuid)
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}