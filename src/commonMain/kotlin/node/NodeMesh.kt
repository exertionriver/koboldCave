package node

import com.soywiz.korio.util.UUID
import node.Node.Companion.linkNearNodes
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeMesh(override val uuid: UUID = UUID.randomUUID(Random.Default), override val description: String = "nodeMesh${Random.nextInt(256)}", override var nodes : MutableList<Node> = mutableListOf(), override var nodeLinks : MutableList<NodeLink> = mutableListOf(), override var centroids : MutableList<Node> = mutableListOf() ) : INodeMesh {

    init {
 //       this.consolidateStackedNodes()
 //       println("consolidating stacked nodes..!")
    }

    override var roomIdx = 0

    constructor(copyNodeMesh : NodeMesh
                , updUuid: UUID = copyNodeMesh.uuid
                , updDescription: String = copyNodeMesh.description) : this (
        uuid = updUuid
        , description = updDescription
    ) {
        nodes.addAll(copyNodeMesh.nodes)
        nodeLinks.addAll(copyNodeMesh.nodeLinks)
    }

    constructor(description : String = "${NodeMesh::class.simpleName}${Random.nextInt(256)}", linkNodes: MutableList<Node>) : this (
        description = description
    ) {
        nodes.addAll(linkNodes)
        nodeLinks.addAll(linkNodes.linkNearNodes())
    }

    override fun toString() = "${NodeMesh::class.simpleName}(${uuid}) : $description, $nodes, $nodeLinks"

}