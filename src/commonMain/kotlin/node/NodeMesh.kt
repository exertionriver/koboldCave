package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Point
import leaf.ILeaf
import leaf.ILeaf.Companion.nodeLinks
import leaf.ILeaf.Companion.nodes
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeMesh(override val uuid: UUID = UUID.randomUUID(Random.Default), override val nodes : MutableList<Node>, override val nodeLinks : MutableList<NodeLink> ) : INodeMesh {

    constructor(leafList : List<ILeaf>) : this (
        nodes = leafList.nodes().toMutableList()
        , nodeLinks = leafList.nodeLinks().toMutableList()
    )


    override fun toString() = "Node.NodeMesh(${uuid}) : $nodes, $nodeLinks"

}