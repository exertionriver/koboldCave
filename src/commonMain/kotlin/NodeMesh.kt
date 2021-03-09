import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeMesh(val uuid: UUID = UUID.randomUUID(Random.Default), val leafNodes : List<Node> = listOf(Node.emptyNode())) {

    val consolidateDistance = 12.0
    val linkDistance = 24.0

    fun getConsolidatedLeafNodes() : List<Node> {
        val includeNodes : MutableSet<Node> = mutableSetOf()
        val excludeNodes : MutableSet<Node> = mutableSetOf()
        var mutableNode : Node

        leafNodes.forEach { outer ->
            leafNodes.forEach { inner ->
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
        return includeNodes.toList().sortedBy { it.uuid.toString() }
    }

    override fun toString() = "NodeMesh(${uuid}) : $leafNodes"

}