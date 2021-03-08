import com.soywiz.klock.DateTime
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.cos
import com.soywiz.korma.geom.sin
import kotlin.math.max
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeMesh(val uuid: UUID = UUID.randomUUID(Random.Default), val oakLeafNodes : List<Node> = listOf(Node.emptyNode())) {

    val consolidateDistance = 12.0
    val linkDistance = 24.0

    fun getConsolidatedOakLeafNodes() : List<Node> {
        val includeNodes : MutableSet<Node> = mutableSetOf()
        val excludeNodes : MutableSet<Node> = mutableSetOf()
        var mutableNode : Node

        oakLeafNodes.forEach { outer ->
            oakLeafNodes.forEach { inner ->
                if (!excludeNodes.contains(outer) && !excludeNodes.contains(inner) && (inner != outer))
                when {
                    (Point.distance(inner.position, outer.position) < consolidateDistance) -> {
                        mutableNode = outer
                        inner.childNodes.let { mutableNode.childNodes.addAll(it) }
                        inner.childNodes.forEach { it.childNodes.add(mutableNode) }
                        inner.childNodes.forEach { it.childNodes.remove(inner) }
                        if (!includeNodes.contains(mutableNode)) includeNodes.add(mutableNode) //compare by UUID
                        excludeNodes.add(inner)
                    }
                    (Point.distance(inner.position, outer.position) in consolidateDistance..linkDistance) -> {
                        mutableNode = outer
                        mutableNode.childNodes.add(inner)
                        if (!includeNodes.contains(mutableNode)) includeNodes.add(mutableNode)
                    }
                    else -> if (!excludeNodes.contains(outer)) includeNodes.add(outer)
                }
            }
        }
        return includeNodes.toList()
    }

    override fun toString() = "NodeMesh(${uuid}) : ${oakLeafNodes}, ${getConsolidatedOakLeafNodes()}"

}