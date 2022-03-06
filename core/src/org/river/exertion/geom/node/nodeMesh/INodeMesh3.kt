package org.river.exertion.geom.node.nodeMesh

import org.river.exertion.geom.Line.Companion.intersectsBorder
import org.river.exertion.geom.Line.Companion.isInBorder
import org.river.exertion.geom.node.Node.Companion.consolidateNearNodes
import org.river.exertion.geom.node.Node.Companion.consolidateStackedNodes
import org.river.exertion.geom.node.Node.Companion.getFarthestNode
import org.river.exertion.geom.node.Node.Companion.getNode
import org.river.exertion.geom.node.Node.Companion.getRandomNode
import org.river.exertion.geom.node.Node.Companion.linkNearNodes
import org.river.exertion.geom.node.Node.Companion.processOrphans
import org.river.exertion.geom.node.NodeLink.Companion.consolidateNodeLinks
import org.river.exertion.geom.node.NodeLink.Companion.getNextAngle
import org.river.exertion.geom.node.NodeLink.Companion.getNextNodeAngle
import org.river.exertion.geom.node.NodeLink.Companion.getNodeLinks
import org.river.exertion.geom.node.NodeLink.Companion.removeOrphanLinks
import org.river.exertion.Angle
import org.river.exertion.NextDistancePx
import org.river.exertion.geom.Line
import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.Node.Companion.averagePositionWithinNodes
import org.river.exertion.geom.node.Node.Companion.bridgeSegments
import org.river.exertion.geom.node.Node.Companion.getLineSet
import org.river.exertion.geom.node.Node.Companion.getRandomUnoccupiedNode
import org.river.exertion.geom.node.Node.Companion.nearestNodesOrderedAsc
import org.river.exertion.geom.node.Node.Companion.randomPosition
import org.river.exertion.geom.node.Node3
import org.river.exertion.geom.node.NodeLink
import org.river.exertion.geom.node.NodeLink.Companion.getLineSet
import org.river.exertion.geom.node.NodeLink.Companion.getRandomNextNodeLinkAngle
import org.river.exertion.geom.node.NodeLink.Companion.nodifyIntersects
import org.river.exertion.geom.node.NodeLink3
import java.util.*

interface INodeMesh3 {

    val uuid : UUID

    val description : String

    var nodes : MutableSet<Node3>

    var nodeLinks : MutableSet<NodeLink3>
}