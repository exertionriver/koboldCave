package render

import node.NodeMesh
import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import kotlinx.coroutines.delay
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import node.INodeMesh.Companion.addMesh
import node.Node
import node.Node.Companion.angleBetween
import node.Node.Companion.averagePositionWithinNodes
import node.Node.Companion.getFarthestNode
import node.Node.Companion.getRandomNode
import node.Node.Companion.nearestNodesOrderedAsc

object RenderNodeRooms {

    lateinit var textView : View

    fun updateNodeText(uuidString : String) {
        textView.setText(uuidString)
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRooms() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val roomColors = listOf(Colors.DARKRED, Colors.DARKGREEN,  Colors.BLUE, Colors.DARKMAGENTA, Colors.DARKSEAGREEN, Colors.DARKTURQUOISE
            , Colors.DARKORANGE, Colors.DARKOLIVEGREEN, Colors.DARKSALMON)

        val roomPathColor = Colors["#427d7a"]

        val startingMap = mapOf(
            90 to Point(750, 600)
            , 330 to Point(600, 400)
            , 210 to Point(900, 400)
        )
        graphics {

            textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            val leafFirst = Leaf(topHeight = 6, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = 6, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = 6, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val nodeMesh = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList()).nodeMesh()
            val nodeClusters = nodeMesh.getClusters(rooms = 12, maxIterations = 5)
            var colorIdx = 0

            nodeMesh.consolidateNearNodes()

            nodeMesh.linkNearNodes()

            stroke(Colors["#0f0f28"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            stroke(Colors["#151540"], StrokeInfo(thickness = 3.0)) {

                for (node in nodeMesh.nodes) {
                    circle(node.position, radius = 5.0)
                }
            }

            nodeClusters.forEach { nodeCluster ->
                val nodeRoom = NodeMesh(linkNodes = nodeCluster.value)

                nodeRoom.consolidateNearNodes()

                nodeRoom.linkNearNodes()

                stroke(roomColors[colorIdx % 9], StrokeInfo(thickness = 2.0)) {

                    for (nodeLine in nodeRoom.getNodeLineList()) {
                        line(nodeLine!!.first, nodeLine.second)
                    }
                }

                nodeRoom.nodes.forEach { node ->
                     circle { position(node.position)
                                radius = 5.0
                                color = roomColors[colorIdx % 9]
                                strokeThickness = 3.0
                                onClick{ updateNodeText(node.uuid.toString())
                            }
                     }
                }

                colorIdx++
            }
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomsBuiltLines() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val roomColors = listOf(Colors.DARKRED, Colors.DARKGREEN,  Colors["#0000ce"], Colors.DARKMAGENTA, Colors.DARKSEAGREEN, Colors.DARKTURQUOISE
            , Colors.DARKORANGE, Colors.DARKOLIVEGREEN, Colors.DARKSALMON)

        val roomPathColor = Colors["#427d7a"]

        val startingMap = mapOf(
            90 to Point(750, 600)
            , 330 to Point(600, 400)
            , 210 to Point(900, 400)
        )
        graphics {

            textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            val leafFirst = Leaf(topHeight = 6, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = 6, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = 6, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()
            val nodeClusters = nodeMesh.getClusters(rooms = 12, maxIterations = 5)
            var colorIdx = 0

            nodeMesh.buildNodeLinkLines()

            nodeMesh.linkNearNodes()

            nodeMesh.consolidateNearNodes()

            stroke(Colors["#0f0f28"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            stroke(Colors["#151540"], StrokeInfo(thickness = 3.0)) {

                for (node in nodeMesh.nodes) {
                    circle(node.position, radius = 5.0)
                }
            }

            nodeClusters.values.forEachIndexed { nodeClusterIndex, nodeCluster ->
                val nodeRoom = NodeMesh(description = "nodeRoom${nodeClusterIndex}", linkNodes = nodeCluster)

                nodeRoom.consolidateNearNodes()

                stroke(roomColors[colorIdx % 9], StrokeInfo(thickness = 2.0)) {

                    for (nodeLine in nodeRoom.getNodeLineList()) {
                        line(nodeLine!!.first, nodeLine.second)
                    }
                }

                nodeRoom.nodes.forEach { node ->
                    circle { position(node.position)
                        radius = 5.0
                        color = roomColors[colorIdx % 9]
                        strokeThickness = 3.0
                        onClick{ updateNodeText(node.uuid.toString())
                        }
                    }
                }

                colorIdx++
            }
        }
    }
    @ExperimentalUnsignedTypes
    suspend fun renderConnectedNodeRooms() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val roomColors = listOf(Colors.DARKRED, Colors.DARKGREEN,  Colors.DARKBLUE, Colors.DARKMAGENTA, Colors.DARKSEAGREEN, Colors.DARKTURQUOISE
            , Colors.DARKORANGE, Colors.DARKOLIVEGREEN, Colors.DARKSALMON)

        val roomPathColor = Colors["#427d7a"]

        val startingMap = mapOf(
            90 to Point(750, 600)
            , 330 to Point(600, 400)
            , 210 to Point(900, 400)
        )
        graphics {

            textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            val leafFirst = Leaf(topHeight = 7, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = 7, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = 7, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()
            val nodeClusters = nodeMesh.getClusters(rooms = 14, maxIterations = 7)

            var colorIdx = 0

            nodeMesh.consolidateNearNodes()

            nodeMesh.linkNearNodes()

            val allRooms = NodeMesh()

            nodeClusters.values.forEachIndexed { clusterIdx, clusterNodes -> allRooms.addMesh(NodeMesh("room$clusterIdx", clusterNodes)) }

            allRooms.consolidateNearNodes()

            allRooms.linkNearNodes()

            stroke(Colors["#0f0f28"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in allRooms.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            stroke(Colors["#151540"], StrokeInfo(thickness = 3.0)) {

                for (node in allRooms.nodes) {
                    circle(node.position, radius = 5.0)
                }
            }

            colorIdx = 0

            nodeClusters.keys.forEach { centroid ->

                circle { position(centroid.position)
                    radius = 10.0
                    color = roomColors[colorIdx % 9]
                    strokeThickness = 3.0
                    onClick{ updateNodeText(centroid.uuid.toString())
                    }
                }
                colorIdx++
            }

            colorIdx = 0

            nodeClusters.values.forEachIndexed { nodeClusterIndex, nodeCluster ->
                val nodeRoom = NodeMesh(description = "nodeRoom${nodeClusterIndex}", linkNodes = nodeCluster)


                nodeRoom.consolidateNearNodes()

                stroke(roomColors[colorIdx % 9], StrokeInfo(thickness = 2.0)) {

                    for (nodeLine in nodeRoom.getNodeLineList()) {
                        line(nodeLine!!.first, nodeLine.second)
                    }
                }

                nodeRoom.nodes.forEach { node ->
                    circle { position(node.position)
                        radius = 5.0
                        color = roomColors[colorIdx % 9]
                        strokeThickness = 3.0
                        onClick{ updateNodeText(node.uuid.toString())
                        }
                    }
                }

                colorIdx++
            }
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderConnectedNodeRoomBorder() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingMap = mapOf(
            90 to Point(750, 600)
            , 330 to Point(600, 400)
            , 210 to Point(900, 400)
        )
        graphics {

            textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            val leafFirst = Leaf(topHeight = 7, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = 7, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = 7, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()
            val nodeClusters = nodeMesh.getClusters(rooms = 14, maxIterations = 7)

            var colorIdx = 0

            nodeMesh.consolidateNearNodes()

            nodeMesh.linkNearNodes()

            val allRooms = NodeMesh()

            nodeClusters.values.forEachIndexed { clusterIdx, clusterNodes -> allRooms.addMesh(NodeMesh("room$clusterIdx", clusterNodes)) }

            allRooms.consolidateNearNodes()

            allRooms.linkNearNodes()

            stroke(Colors["#0f0f28"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in allRooms.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            stroke(Colors["#151540"], StrokeInfo(thickness = 3.0)) {

                for (node in allRooms.nodes) {
                    circle(node.position, radius = 5.0)
                }
            }

            val centroid = Node(position = allRooms.nodes.averagePositionWithinNodes())

            stroke(Colors["#3939ad"], StrokeInfo(thickness = 3.0)) {
                 circle(centroid.position, radius = 5.0)
            }

            val farthestNode = allRooms.nodes.getFarthestNode(centroid)
            
            val outerNodes80 = allRooms.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .8 }

            val outerNodes70 = allRooms.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .7 }

            val outerNodes60 = allRooms.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .6 }

            stroke(Colors["#56f636"], StrokeInfo(thickness = 3.0)) {

                for (node in outerNodes60) {
                    circle(node.position, radius = 5.0)
                }
            }

            delay(1000)

            stroke(Colors["#1d8a1e"], StrokeInfo(thickness = 3.0)) {

                for (node in outerNodes70) {
                    circle(node.position, radius = 5.0)
                }
            }

            delay(1000)

            stroke(Colors["#0d431b"], StrokeInfo(thickness = 3.0)) {

                for (node in outerNodes80) {
                    circle(node.position, radius = 5.0)
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderConnectedNodeRoomElaboration() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingMap = mapOf(
            90 to Point(450, 600)
            , 330 to Point(300, 400)
            , 210 to Point(600, 400)
        )
        graphics {

            textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            val leafFirst = Leaf(topHeight = 7, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = 7, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = 7, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()
            val nodeClusters = nodeMesh.getClusters(rooms = 14, maxIterations = 7)

            var colorIdx = 0

            nodeMesh.consolidateNearNodes()

            nodeMesh.linkNearNodes()

            val allRooms = NodeMesh()

            nodeClusters.values.forEachIndexed { clusterIdx, clusterNodes -> allRooms.addMesh(NodeMesh("room$clusterIdx", clusterNodes)) }

            allRooms.consolidateNearNodes()

            allRooms.linkNearNodes()

            (1..5).forEach {

                stroke(Colors["#0f0f28"], StrokeInfo(thickness = 3.0)) {

                    for (nodeLine in allRooms.getNodeLineList() ) {
                        line(nodeLine!!.first, nodeLine.second )
                    }
                }

                stroke(Colors["#151540"], StrokeInfo(thickness = 3.0)) {

                    for (node in allRooms.nodes) {
                        circle(node.position, radius = 5.0)
                    }
                }

                val centroid = Node(position = allRooms.nodes.averagePositionWithinNodes())

                stroke(Colors["#3939ad"], StrokeInfo(thickness = 3.0)) {
                    circle(centroid.position, radius = 5.0)
                }

                val farthestNode = allRooms.nodes.getFarthestNode(centroid)

                val outerNodes60 = allRooms.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .5 }

                stroke(Colors["#56f636"], StrokeInfo(thickness = 3.0)) {

                    for (node in outerNodes60) {
                        circle(node.position, radius = 5.0)
                    }
                }

                val randomOuter60Node = outerNodes60.getRandomNode()

                stroke(Colors["#f65862"], StrokeInfo(thickness = 3.0)) {

                    circle(randomOuter60Node.position, radius = 5.0)
                }

                delay(3000)

                println("elaboration position: ${randomOuter60Node.position}")

                println("elaboration angle: ${centroid.angleBetween(randomOuter60Node)}")

                val newMesh = Leaf(topHeight = 5, position = randomOuter60Node.position, angleFromParent = centroid.angleBetween(randomOuter60Node) ).getList().nodeMesh()

                newMesh.consolidateNearNodes()

                newMesh.linkNearNodes()

                allRooms.addMesh(newMesh)

            }
        }
    }
}