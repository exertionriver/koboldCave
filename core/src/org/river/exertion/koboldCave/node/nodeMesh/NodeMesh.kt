package org.river.exertion.koboldCave.node.nodeMesh

import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.removeOrphanLinks
import java.util.*
import kotlin.random.Random

@ExperimentalUnsignedTypes
class NodeMesh(override val uuid: UUID = UUID.randomUUID(), override val description: String = "nodeMesh${Random.nextInt(256)}"
               , override var nodes : MutableList<Node> = mutableListOf(), override var nodeLinks : MutableList<NodeLink> = mutableListOf() ) :
    INodeMesh {

    //copy constructor
    constructor(copyNodeMesh : NodeMesh
                , updUuid: UUID = copyNodeMesh.uuid
                , updDescription: String = copyNodeMesh.description) : this (
        uuid = updUuid
        , description = updDescription
    ) {
        nodes = mutableListOf<Node>().apply { addAll(copyNodeMesh.nodes) }
        nodeLinks = mutableListOf<NodeLink>().apply { addAll(copyNodeMesh.nodeLinks) }
    }
/*
    //link constructor
    constructor(description : String = "${NodeMesh::class.simpleName}${Random.nextInt(256)}", linkNodes: MutableList<Node>) : this (
        description = description
    ) {
        nodes.addAll(linkNodes)
        nodeLinks.addAll(linkNodes.linkNearNodes())
    }
*/
    operator fun plus(secondMesh : NodeMesh) : NodeMesh {
        val workNodeMesh = this

        val workNodes = mutableListOf<Node>().apply { addAll(workNodeMesh.nodes); addAll(secondMesh.nodes) }
        var workNodeLinks = mutableListOf<NodeLink>().apply { addAll(workNodeMesh.nodeLinks); addAll(secondMesh.nodeLinks) }

        return NodeMesh(description ="${workNodeMesh.description} + ${secondMesh.description}", nodes = workNodes, nodeLinks = workNodeLinks).apply { consolidateStackedNodes() }
    }

    operator fun minus(secondMesh : NodeMesh) : NodeMesh {
        val workNodeMesh = this

        val workNodes = mutableListOf<Node>().apply { addAll(workNodeMesh.nodes); removeAll(secondMesh.nodes) }
        val workNodeLinks = mutableListOf<NodeLink>().apply { addAll(workNodeMesh.nodeLinks); this.removeOrphanLinks(workNodes) }

        return NodeMesh(description ="${workNodeMesh.description} + ${secondMesh.description}", nodes = workNodes, nodeLinks = workNodeLinks)

    }

    override fun toString() = "${NodeMesh::class.simpleName}(${uuid}) : $description, $nodes, $nodeLinks"

}