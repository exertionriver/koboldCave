package node

import com.soywiz.korge.view.graphics
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.normalized
import com.soywiz.korma.geom.plus
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import leaf.ILeaf
import leaf.ILeaf.Companion.LeafDistancePx
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import leaf.Stream
import node.Node.Companion.addNode
import node.Node.Companion.addNodes
import node.Node.Companion.buildNodePaths
import node.Node.Companion.cluster
import node.Node.Companion.consolidateNearNodes
import node.Node.Companion.consolidateStackedNodes
import node.Node.Companion.getFarthestNode
import node.Node.Companion.getNodeLineList
import node.Node.Companion.getRandomNode
import node.Node.Companion.linkNearNodes
import node.Node.Companion.nearestNodesOrderedAsc
import node.NodeLink.Companion.addNodeLinks
import node.NodeLink.Companion.buildNodeLinkLines
import node.NodeLink.Companion.consolidateNodeLinks
import node.NodeLink.Companion.getNextAngle
import node.NodeLink.Companion.getNextNodeAngle
import node.NodeLink.Companion.getNodeLinks
import node.NodeLink.Companion.getRandomNextNodeAngle

@ExperimentalUnsignedTypes
interface INodeMesh {

    val uuid : UUID

    val description : String

    var nodes : MutableList<Node>

    var nodeLinks : MutableList<NodeLink>

    var roomIdx : Int

    fun linkNearNodes(linkOrphans : Boolean = true) { nodeLinks = nodes.linkNearNodes(linkOrphans) }

    fun consolidateNearNodes() { nodeLinks = nodes.consolidateNearNodes(nodeLinks) }

    fun consolidateNodeLinks() { nodeLinks = nodeLinks.consolidateNodeLinks(nodes) }

    fun consolidateStackedNodes() { nodeLinks = nodes.consolidateStackedNodes(nodeLinks) }

    fun getClusters(rooms : Int = 4, maxIterations : Int = 4) : Map<Node, MutableList<Node>> = nodes.cluster(rooms, maxIterations)

    fun setClusters(rooms : Int = 4, maxIterations : Int = 4) { val nodeClusters = nodes.cluster(rooms, maxIterations, roomIdx) ; roomIdx += nodeClusters.size ; nodes = nodeClusters.flatMap { it.value }.toMutableList() }

    //todo: combine next two
    fun getRandomNode() = nodes.getRandomNode()

    fun getRandomNextNodeAngle(node : Node) = nodeLinks.getRandomNextNodeAngle(nodes, node)

    fun getFarthestNode(refNode : Node) = nodes.getFarthestNode(refNode)

    fun getNextNodeAngle(refNode : Node, refAngle : Angle) : Pair<Node, Angle> = nodeLinks.getNextNodeAngle(nodes, refNode, refAngle)

    fun getNextAngle(refNode : Node, refAngle : Angle, rangeAngle : Angle) : Angle = nodeLinks.getNextAngle(nodes, refNode, refAngle, rangeAngle)

    fun getNodeLineList() : List<Pair<Point, Point>?> = nodes.getNodeLineList(nodeLinks)

    fun buildNodeLinkLines(noise : Int = 0, description : String = this.description) { this.addMesh( nodeLinks.buildNodeLinkLines(nodes, noise, description) ) }

    companion object {

        fun INodeMesh.addMesh(nodeMeshToAdd : INodeMesh, description : String = this.description) {
            this.nodes.addNodes(nodeMeshToAdd.nodes, description)
            this.nodeLinks.addNodeLinks(nodeMeshToAdd.nodeLinks)

            this.nodes = this.nodes.distinct().toMutableList()
            this.consolidateStackedNodes()
        }

        fun INodeMesh.replaceMesh(nodeMeshToAdd : INodeMesh, description : String = this.description) {
            this.nodes.clear()
            nodeMeshToAdd.nodes.forEach { addNode -> this.nodes.addNode(addNode, description) }
            this.nodeLinks = nodeMeshToAdd.nodeLinks

            this.nodes = this.nodes.distinct().toMutableList()
            this.consolidateStackedNodes()
        }

        fun INodeMesh.absorbMesh(centerNode : Node, radius : Double, nodeMeshToAbsorb : INodeMesh) {

            val nodesWithinRange = nodeMeshToAbsorb.nodes.filter {node -> Point.distance(node.position, centerNode.position) <= radius }.toMutableList()
            val nodeLinksWithinRange = nodeMeshToAbsorb.nodeLinks.getNodeLinks(nodesWithinRange.map { it.uuid } )

            this.addMesh(nodeMeshToAdd = NodeMesh(nodes= nodesWithinRange, nodeLinks = nodeLinksWithinRange) )
            this.consolidateStackedNodes()
        }

        fun buildRoomMesh(centerPoint : Point, height : Int) : INodeMesh {

            val leafPoints = height + 1

            val leafMap = mutableMapOf<Angle, Point>()

            val roomMesh = NodeMesh()

            (0 until leafPoints).toList().forEach{ leafIndex ->
                val angleOnCircle = Angle.fromDegrees( 360 / leafPoints * leafIndex ).normalized

                //angleInMap points back to the center of the circle
                leafMap[(Angle.fromDegrees(180) + angleOnCircle).normalized] = ILeaf.getChildPosition(centerPoint, (height - 2) * LeafDistancePx, angleOnCircle)
            }

            leafMap.forEach {

                roomMesh.addMesh( Leaf(initHeight = height, angleFromParent = it.key, position = it.value ).getLeafList().nodeMesh() )
            }

            roomMesh.consolidateStackedNodes()

            roomMesh.consolidateNearNodes()

            roomMesh.linkNearNodes()

            roomMesh.consolidateNodeLinks()

            roomMesh.setClusters(rooms = roomMesh.nodes.size / 20, maxIterations = roomMesh.nodes.size / 20)

            roomMesh.consolidateStackedNodes()

            roomMesh.consolidateNearNodes()

            roomMesh.linkNearNodes()

            roomMesh.consolidateNodeLinks()

            return roomMesh
        }
    }
}