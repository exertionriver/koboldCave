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
import leaf.ILeaf.Companion.getLeafLineList
import leaf.ILeaf.Companion.getLeafList
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import node.INodeMesh.Companion.absorbMesh
import node.INodeMesh.Companion.addMesh
import node.Node.Companion.buildNodePaths

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

            val leafFirst = Leaf(initHeight = 6, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(initHeight = 6, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(initHeight = 6, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val nodeMesh = leafFirst.getLeafList().plus(leafSecond.getLeafList()).plus(leafThird.getLeafList()).nodeMesh()
            val nodeClusters = nodeMesh.getClusters(rooms = 12, maxIterations = 5)
            var colorIdx = 0

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

            delay(3000)
            
            nodeClusters.forEach { nodeCluster ->
                val nodeRoom = NodeMesh(linkNodes = nodeCluster.value)

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

            val leafFirst = Leaf(initHeight = 6, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(initHeight = 6, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(initHeight = 6, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getLeafList().plus(leafSecond.getLeafList()).plus(leafThird.getLeafList())
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

            val leafFirst = Leaf(initHeight = 7, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(initHeight = 7, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(initHeight = 7, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getLeafList().plus(leafSecond.getLeafList()).plus(leafThird.getLeafList())
            val nodeMesh = threeLeaf.nodeMesh()
            val nodeClusters = nodeMesh.getClusters(rooms = 14, maxIterations = 7)

            var colorIdx = 0

            nodeMesh.linkNearNodes()

            nodeMesh.consolidateNearNodes()

            val allRooms = NodeMesh()

            nodeClusters.values.forEachIndexed { clusterIdx, clusterNodes -> allRooms.addMesh(NodeMesh("room$clusterIdx", clusterNodes)) }

            allRooms.linkNearNodes()

            allRooms.consolidateNearNodes()

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
}