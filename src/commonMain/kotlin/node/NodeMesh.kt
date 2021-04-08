package node

import com.soywiz.korio.util.UUID
import node.Node.Companion.linkNearNodes
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeMesh(override val uuid: UUID = UUID.randomUUID(Random.Default), override val description: String = "nodeMesh${Random.nextInt(256)}", override var nodes : MutableList<Node>, override var nodeLinks : MutableList<NodeLink> ) : INodeMesh {

    init {
        this.consolidateStackedNodes()
 //       println("consolidating stacked nodes..!")
    }

    override var roomIdx = 0

    constructor(copyNodeMesh : NodeMesh
                , updUuid: UUID = copyNodeMesh.uuid
                , updDescription: String = copyNodeMesh.description
                , updNodes: MutableList<Node> = copyNodeMesh.nodes
                , updNodeLinks: MutableList<NodeLink> = copyNodeMesh.nodeLinks) : this (
        uuid = updUuid
        , description = updDescription
        , nodes = updNodes
        , nodeLinks = updNodeLinks
    )

    constructor(description : String = "${NodeMesh::class.simpleName}${Random.nextInt(256)}", linkNodes: MutableList<Node>) : this (
        description = description
        , nodes = linkNodes
        , nodeLinks = linkNodes.linkNearNodes()
    )

    constructor(description : String = "${NodeMesh::class.simpleName}${Random.nextInt(256)}") : this (
        description = description
        , nodes = mutableListOf()
        , nodeLinks = mutableListOf()
    )

    override fun toString() = "${NodeMesh::class.simpleName}(${uuid}) : $description, $nodes, $nodeLinks"

}