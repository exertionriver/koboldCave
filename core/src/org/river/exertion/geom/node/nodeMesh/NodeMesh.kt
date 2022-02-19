package org.river.exertion.geom.node.nodeMesh

import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.NodeLink
import org.river.exertion.geom.node.NodeLink.Companion.removeOrphanLinks
import java.util.*
import kotlin.random.Random

class NodeMesh(override val uuid: UUID = UUID.randomUUID(), override val description: String = "nodeMesh${Random.nextInt(256)}"
               , override var nodes : MutableSet<Node> = mutableSetOf(), override var nodeLinks : MutableSet<NodeLink> = mutableSetOf() ) :
    INodeMesh {

    //copy constructor
    constructor(copyNodeMesh : NodeMesh
                , updUuid: UUID = copyNodeMesh.uuid
                , updDescription: String = copyNodeMesh.description) : this (
        uuid = updUuid
        , description = updDescription
    ) {
        nodes = mutableSetOf<Node>().apply { addAll(copyNodeMesh.nodes) }
        nodeLinks = mutableSetOf<NodeLink>().apply { addAll(copyNodeMesh.nodeLinks) }
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

        val workNodes = mutableSetOf<Node>().apply { addAll(workNodeMesh.nodes); addAll(secondMesh.nodes) }
        var workNodeLinks = mutableSetOf<NodeLink>().apply { addAll(workNodeMesh.nodeLinks); addAll(secondMesh.nodeLinks) }

        return NodeMesh(description ="${workNodeMesh.description} + ${secondMesh.description}", nodes = workNodes, nodeLinks = workNodeLinks).apply { consolidateStackedNodes() }
    }

    operator fun minus(secondMesh : NodeMesh) : NodeMesh {
        val workNodeMesh = this

        val workNodes = mutableSetOf<Node>().apply { addAll(workNodeMesh.nodes); removeAll(secondMesh.nodes) }
        val workNodeLinks = mutableSetOf<NodeLink>().apply { addAll(workNodeMesh.nodeLinks); this.removeOrphanLinks(workNodes) }

        return NodeMesh(description ="${workNodeMesh.description} + ${secondMesh.description}", nodes = workNodes, nodeLinks = workNodeLinks)

    }

    override fun toString() = "${NodeMesh::class.simpleName}(${uuid}) : $description, $nodes, $nodeLinks"

}