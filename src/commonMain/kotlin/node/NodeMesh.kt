package node

import com.soywiz.korio.util.UUID
import node.Node.Companion.linkNodes
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeMesh(override val uuid: UUID = UUID.randomUUID(Random.Default), override val nodes : MutableList<Node>, override var nodeLinks : MutableList<NodeLink> ) : INodeMesh {

    constructor(copyNodeMesh : NodeMesh
                , updUuid: UUID = copyNodeMesh.uuid
                , updNodes: MutableList<Node> = copyNodeMesh.nodes
                , updNodeLinks: MutableList<NodeLink> = copyNodeMesh.nodeLinks) : this (
        uuid = updUuid
        , nodes = updNodes
        , nodeLinks = updNodeLinks
    )

    constructor(relinkNodes: MutableList<Node>) : this (
        nodes = relinkNodes
        , nodeLinks = relinkNodes.linkNodes()
    )

    override fun toString() = "Node.NodeMesh(${uuid}) : $nodes, $nodeLinks"

}