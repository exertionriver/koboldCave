package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import leaf.ILeaf
import leaf.ILeaf.Companion.nodeLinks
import leaf.ILeaf.Companion.nodes
import node.INodeMesh.Companion.getNodeRelinks
import kotlin.random.Random
import kotlin.reflect.KClass

@ExperimentalUnsignedTypes
class NodeMesh(override val uuid: UUID = UUID.randomUUID(Random.Default), override val nodes : MutableList<Node>, override val nodeLinks : MutableList<NodeLink> ) : INodeMesh {

    constructor(copyNodeMesh : NodeMesh
                , updUuid: UUID = copyNodeMesh.uuid
                , updNodes: MutableList<Node> = copyNodeMesh.nodes
                , updNodeLinks: MutableList<NodeLink> = copyNodeMesh.nodeLinks) : this (
        uuid = updUuid
        , nodes = updNodes
        , nodeLinks = updNodeLinks
    )

    constructor(relinkNodes: List<Node>) : this (
        nodes = relinkNodes.toMutableList()
        , nodeLinks = getNodeRelinks(relinkNodes.toMutableList())
    )

    override fun toString() = "Node.NodeMesh(${uuid}) : $nodes, $nodeLinks"

}