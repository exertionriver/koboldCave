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
import leaf.Line.Companion.intersectsBorder
import leaf.Line.Companion.isInBorder
import node.Node.Companion.addNode
import node.Node.Companion.addNodes
import node.Node.Companion.adoptRoomOrphans
import node.Node.Companion.averagePositionWithinNodes
import node.Node.Companion.cluster
import node.Node.Companion.consolidateNearNodes
import node.Node.Companion.consolidateStackedNodes
import node.Node.Companion.getFarthestNode
import node.Node.Companion.getNode
import node.Node.Companion.getNodeLineList
import node.Node.Companion.getRandomNode
import node.Node.Companion.linkNearNodes
import node.Node.Companion.removeNode
import node.Node.Companion.removeOrphans
import node.NodeLink.Companion.addNodeLinks
import node.NodeLink.Companion.buildNodeLinkLines
import node.NodeLink.Companion.consolidateNodeLinks
import node.NodeLink.Companion.getNextAngle
import node.NodeLink.Companion.getNextNodeAngle
import node.NodeLink.Companion.getNodeLineList
import node.NodeLink.Companion.getNodeLinks
import node.NodeLink.Companion.getRandomNextNodeAngle
import node.NodeLink.Companion.pruneNodeLinks
import node.NodeLink.Companion.removeNodeLink
import node.NodeLink.Companion.removeOrphanLinks

@ExperimentalUnsignedTypes
interface INodeMesh {

    val uuid : UUID

    val description : String

    var nodes : MutableList<Node>

    var nodeLinks : MutableList<NodeLink>

    var centroids : MutableList<Node>

    var roomIdx : Int

    fun linkNearNodes(linkOrphans : Boolean = true) { nodeLinks = nodes.linkNearNodes(nodeLinks, linkOrphans = linkOrphans) }

    fun linkNearNodesBordering(nodeMeshToBorder : INodeMesh, orthoBorderDistance : Double = NextDistancePx * 0.2, linkOrphans : Boolean = true) { nodeLinks = nodes.linkNearNodes(nodeLinks, nodeMeshToBorder, orthoBorderDistance, linkOrphans) }

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

        fun INodeMesh.addMesh(nodeMeshToAdd : INodeMesh, description : String = this.description) : INodeMesh {
//            println("adding nodes and links")
            this.nodes.addNodes(nodeMeshToAdd.nodes, description)
            this.nodeLinks.addNodeLinks(nodeMeshToAdd.nodeLinks)

//            println("distinct")
            this.nodes = this.nodes.distinct().toMutableList()
//            println("consolidate stacked")
//            this.consolidateStackedNodes()
            return this
        }

        fun INodeMesh.getBorderingMesh(nodeMeshToBorder : INodeMesh, orthoBorderDistance : Double = NextDistancePx * 0.2, description : String = this.description) : INodeMesh {
            //result nodeMesh
            val borderingMesh = NodeMesh(copyNodeMesh = this as NodeMesh)

            this.nodes.forEach { node ->
                //get nodelinks associated with this node
                val borderingLeafNodeLinks = this.nodeLinks.getNodeLinks(node.uuid)

                //find nodes in the ref structure within four times border distance in the bordering leaf case
                val closestOrderedRefNodes = nodeMeshToBorder.nodes.sortedBy { iRef -> Point.distance(iRef.position, node.position) }

                var checkNodeIdx = 0
                val closestRefNodeLinks : MutableList<NodeLink> = mutableListOf()
                val closestRefNodeLines : MutableList<Pair<Point, Point>> = mutableListOf()

                while (Point.distance(node.position, closestOrderedRefNodes[checkNodeIdx].position) <= orthoBorderDistance * 4) {
                    val closestRefNode = closestOrderedRefNodes[checkNodeIdx]

                    //get the nodelinks and nodelink lines associated with the closest ref node
                    closestRefNodeLinks.addAll(nodeMeshToBorder.nodeLinks.getNodeLinks(closestRefNode.uuid))

                    if (!nodeMeshToBorder.nodes.isNullOrEmpty() && !closestRefNodeLinks.isNullOrEmpty()) {

                        val nodeLines = closestRefNodeLinks.getNodeLineList(nodeMeshToBorder.nodes)

                        if (!nodeLines.isNullOrEmpty())
                            closestRefNodeLines.addAll(nodeLines.toMutableList() as MutableList<Pair<Point, Point>>)

                    }

                    checkNodeIdx++
                }

                //check each line related to the closest node
                closestRefNodeLines.forEach { closestRefNodeLine ->
                    //check if this node falls within the borders of any line related to closest ref node
//                    println("node within border? node:$node, refLine:$closestRefNodeLine")
                    if (node.position.isInBorder(closestRefNodeLine!!, orthoBorderDistance.toInt())) {
                        //if node in border, remove from result nodeMesh
                        borderingMesh.nodes.removeNode(this.nodeLinks, node.uuid)
    //                    println("node within border! node:$node, refLine:$closestRefNodeLine")
                    }

                    //if this node has links
                    if ( borderingLeafNodeLinks.isNotEmpty() ) {

                        //for each of these links,
                        borderingLeafNodeLinks.forEach { borderingLeafNodeLink ->

                            //get the first and second nodes
                            val firstNode = this.nodes.getNode(borderingLeafNodeLink.firstNodeUuid)
                            val secondNode = this.nodes.getNode(borderingLeafNodeLink.secondNodeUuid)

                            //if these nodes are not null
                            if ( (firstNode != null) && (secondNode != null) ) {
                                //check if the nodeLink intersects with borderline
  //                              println("intersects? nodeLink:(${firstNode.position}, ${secondNode.position}), refLine:$closestRefNodeLine")
                                if ( Pair(firstNode.position, secondNode.position)
                                        .intersectsBorder(closestRefNodeLine, orthoBorderDistance.toInt()) ) {
                                    //if nodeLink intersects border, remove from result nodeMesh
                                    borderingMesh.nodeLinks.removeNodeLink(borderingLeafNodeLink)
//                                    println("intersection! nodeLink:(${firstNode.position}, ${secondNode.position}), refLine:$closestRefNodeLine")
                                }
                            }
                        }
                    }
                }
            }
            borderingMesh.linkNearNodesBordering(nodeMeshToBorder)
            borderingMesh.pruneNodeLinks()
            borderingMesh.consolidateNodeLinks()
            borderingMesh.removeOrphans()

            return borderingMesh
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

//            println("consolidated stacked")

            this.consolidateNearNodes()

//            println("consolidated near")

            this.linkNearNodes()

//            println("linked near")

            this.pruneNodeLinks()

//            println("pruned")

            this.consolidateNodeLinks()

//            println("consolidated node links")

            this.removeOrphans()

//            println("orphans removed")

        }

        fun buildRoomMesh(centerPoint : Point, height : Int) : INodeMesh {

            val leafPoints = height + 1

            val leafMap = mutableMapOf<Angle, Point>()

            val roomMesh = NodeMesh()

            (0 until leafPoints).toList().forEach{ leafIndex ->
                val angleOnCircle = Angle.fromDegrees( 360 / leafPoints * leafIndex ).normalized

//                println("building leaf mesh: $leafIndex, $angleOnCircle")

                //angleInMap points back to the center of the circle
                leafMap[(Angle.fromDegrees(180) + angleOnCircle).normalized] = ILeaf.getChildPosition(centerPoint, (height - 2) * NextDistancePx, angleOnCircle)
            }

            leafMap.forEach {

                roomMesh.addMesh( Leaf(topHeight = height, angleFromParent = it.key, position = it.value ).getList().nodeMesh() )

            }

//            println("processing Mesh")


            roomMesh.processMesh()

            return roomMesh
        }

        fun buildCentroidRoomMesh(height : Int = 3, centroids : MutableList<Node> = mutableListOf()) : INodeMesh {

            val leafPoints = height + 1

            val leafMap = mutableMapOf<Angle, Point>()

            val roomMesh = NodeMesh()

            centroids.forEach { centroid ->
//                println("centroid: $centroid")
                (0 until leafPoints).toList().forEach{ leafIndex ->

                    val angleOnCircle = Angle.fromDegrees( 360 / leafPoints * leafIndex ).normalized

//                    println("building leaf mesh: $leafIndex, $angleOnCircle")

                    //angleInMap points back to the center of the circle
                    leafMap[(Angle.fromDegrees(180) + angleOnCircle).normalized] = ILeaf.getChildPosition(centroid.position, (height - 2) * NextDistancePx, angleOnCircle)
                }
                leafMap.forEach {

//                    println("adding Mesh")
                    roomMesh.addMesh( Leaf(topHeight = height, angleFromParent = it.key, position = it.value ).getList().nodeMesh() )
                }
            }

//            println("processing Mesh")

            roomMesh.processMesh()

            roomMesh.setClusters(rooms = centroids.size, maxIterations = centroids.size, centroids)

            return roomMesh
        }
    }
}