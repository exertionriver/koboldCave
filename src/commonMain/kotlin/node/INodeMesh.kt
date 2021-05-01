package node

import com.soywiz.korio.util.UUID
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.normalized
import com.soywiz.korma.geom.plus
import leaf.ILeaf
import leaf.ILeaf.Companion.NextDistancePx
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import node.Node.Companion.addNode
import node.Node.Companion.addNodes
import node.Node.Companion.adoptRoomOrphans
import node.Node.Companion.averagePositionWithinNodes
import node.Node.Companion.cluster
import node.Node.Companion.consolidateNearNodes
import node.Node.Companion.consolidateStackedNodes
import node.Node.Companion.getFarthestNode
import node.Node.Companion.getNodeLineList
import node.Node.Companion.getRandomNode
import node.Node.Companion.linkNearNodes
import node.Node.Companion.removeOrphans
import node.NodeLink.Companion.addNodeLinks
import node.NodeLink.Companion.buildNodeLinkLines
import node.NodeLink.Companion.consolidateNodeLinks
import node.NodeLink.Companion.getNextAngle
import node.NodeLink.Companion.getNextNodeAngle
import node.NodeLink.Companion.getNodeLinks
import node.NodeLink.Companion.getRandomNextNodeAngle
import node.NodeLink.Companion.pruneNodeLinks
import node.NodeLink.Companion.removeOrphanLinks

@ExperimentalUnsignedTypes
interface INodeMesh {

    val uuid : UUID

    val description : String

    var nodes : MutableList<Node>

    var nodeLinks : MutableList<NodeLink>

    var centroids : MutableList<Node>

    var roomIdx : Int

    fun linkNearNodes(linkOrphans : Boolean = true) { nodeLinks = nodes.linkNearNodes(nodeLinks, linkOrphans) }

    fun consolidateNearNodes() { nodeLinks = nodes.consolidateNearNodes(nodeLinks) }

    fun consolidateNodeLinks() { nodeLinks = nodeLinks.consolidateNodeLinks(nodes) }

    fun consolidateStackedNodes() { nodeLinks = nodes.consolidateStackedNodes(nodeLinks) }

    fun pruneNodeLinks() { nodeLinks = nodeLinks.pruneNodeLinks(nodes) }

    fun removeOrphans() { nodes = nodes.removeOrphans(nodeLinks, minPercent = 0.25); nodeLinks = nodeLinks.removeOrphanLinks(nodes) }

    fun adoptRoomOrphans() { nodes = nodes.adoptRoomOrphans(nodeLinks, getRoomNodes()) }

    fun getClusters(rooms : Int = 4, maxIterations : Int = 4) : Map<Node, MutableList<Node>> = nodes.cluster(rooms, maxIterations)

    fun setClusters(rooms : Int = 4, maxIterations : Int = 4, setCentroids : MutableList<Node> = mutableListOf()) {
        val nodeClusters = nodes.cluster(rooms, maxIterations, roomIdx, setCentroids)
        roomIdx += nodeClusters.size
        nodes = nodeClusters.flatMap { it.value }.toMutableList()
        centroids = nodeClusters.keys.toMutableList()
    }

    fun getRandomNode() = nodes.getRandomNode()

    fun getRandomNextNodeAngle(node : Node) = nodeLinks.getRandomNextNodeAngle(nodes, node)

    fun getFarthestNode(refNode : Node) = nodes.getFarthestNode(refNode)

    fun getNextNodeAngle(refNode : Node, refAngle : Angle) : Pair<Node, Angle> = nodeLinks.getNextNodeAngle(nodes, refNode, refAngle)

    fun getNextAngle(refNode : Node, refAngle : Angle, rangeAngle : Angle) : Angle = nodeLinks.getNextAngle(nodes, refNode, refAngle, rangeAngle)

    fun getNodeLineList() : List<Pair<Point, Point>?> = nodes.getNodeLineList(nodeLinks)

    //todo: move this to Line
    fun buildNodeLinkLines(noise : Int = 0, description : String = this.description) { this.addMesh( nodeLinks.buildNodeLinkLines(nodes, noise, description) ) }

    fun getRoomNodes() : Map<String, MutableList<Node>> {

        val returnMap = mutableMapOf<String, MutableList<Node>>()

        nodes.forEach { node ->
            if (returnMap[node.description].isNullOrEmpty()) returnMap[node.description] = mutableListOf()

            returnMap[node.description]!!.add(node)
        }

        return returnMap
    }

    companion object {

        fun INodeMesh.addMesh(nodeMeshToAdd : INodeMesh, description : String = this.description) {
            this.nodes.addNodes(nodeMeshToAdd.nodes, description)
            this.nodeLinks.addNodeLinks(nodeMeshToAdd.nodeLinks)

            this.nodes = this.nodes.distinct().toMutableList()
            this.consolidateStackedNodes()
        }

        fun INodeMesh.addBorderingMesh(nodeMeshToAdd : INodeMesh, description : String = this.description) {
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

        fun INodeMesh.processMesh() {

            this.consolidateStackedNodes()

//            println("consolidated stacked: $this")

            this.consolidateNearNodes()

//            println("consolidated near: $this")

            this.linkNearNodes()

//            println("linked near: $this")

            this.pruneNodeLinks()

//            println("pruned: $this")

            this.consolidateNodeLinks()

//            println("consolidated node links: $this")

            this.removeOrphans()

//            println("orphans removed: $this")

        }

        fun buildRoomMesh(centerPoint : Point, height : Int) : INodeMesh {

            val leafPoints = height

            val leafMap = mutableMapOf<Angle, Point>()

            val roomMesh = NodeMesh()

            (0 until leafPoints).toList().forEach{ leafIndex ->
                val angleOnCircle = Angle.fromDegrees( 360 / leafPoints * leafIndex ).normalized

//                println("building leaf mesh: $leafIndex, $angleOnCircle")

                //angleInMap points back to the center of the circle
                leafMap[(Angle.fromDegrees(180) + angleOnCircle).normalized] = ILeaf.getChildPosition(centerPoint, (height - 2) * NextDistancePx, angleOnCircle)
            }

            leafMap.forEach {

 //               println("leafMap: $it")
                val leaf = Leaf(topHeight = height, angleFromParent = it.key, position = it.value )
 //               println("leaf: $leaf")
                roomMesh.addMesh( leaf.getList().nodeMesh() )
 //               println("mesh: $roomMesh")
            }

            roomMesh.processMesh()

            //roomMesh.setClusters(rooms = roomMesh.nodes.size / 8, maxIterations = roomMesh.nodes.size / 8)

            return roomMesh
        }

        fun buildCentroidRoomMesh(height : Int = 3, centroids : MutableList<Node> = mutableListOf()) : INodeMesh {

            val leafPoints = height

            val leafMap = mutableMapOf<Angle, Point>()

            val roomMesh = NodeMesh()

            centroids.forEach { centroid ->
                (0 until leafPoints).toList().forEach{ leafIndex ->

                    val angleOnCircle = Angle.fromDegrees( 360 / leafPoints * leafIndex ).normalized

//                       println("building leaf mesh: $leafIndex, $angleOnCircle")

                    //angleInMap points back to the center of the circle
                    leafMap[(Angle.fromDegrees(180) + angleOnCircle).normalized] = ILeaf.getChildPosition(centroid.position, (height - 2) * NextDistancePx, angleOnCircle)
                }

                leafMap.forEach {

                    //     println("leafMap: $it")
                    roomMesh.addMesh( Leaf(topHeight = height, angleFromParent = it.key, position = it.value ).getList().nodeMesh() )
                }
            }

            roomMesh.processMesh()

            roomMesh.setClusters(rooms = roomMesh.nodes.size / 8, maxIterations = roomMesh.nodes.size / 8, centroids)

            return roomMesh
        }
    }
}