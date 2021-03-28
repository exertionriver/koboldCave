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

object RenderNodeRooms {

    lateinit var textView : View

    fun updateNodeText(uuidString : String) {
        textView.setText(uuidString)
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRooms() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

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

            val leafFirst = Leaf(initHeight = 5, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(initHeight = 5, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(initHeight = 5, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val nodeMesh = leafFirst.getLeafList().plus(leafSecond.getLeafList()).plus(leafThird.getLeafList()).nodeMesh()
            val nodeRooms = nodeMesh.getClusteredNodes(rooms = 8, maxIterations = 4)
            var colorIdx = 0

            nodeMesh.consolidateNodes()

            nodeMesh.linkNodes()

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

            nodeRooms.forEach { nodeRoom ->
                val roomNodes = NodeMesh(relinkNodes = nodeRoom.nodes.toMutableList())

                nodeRoom.consolidateNodes()

                stroke(roomColors[colorIdx % 9], StrokeInfo(thickness = 2.0)) {

                    for (nodeLine in roomNodes.getNodeLineList()) {
                        line(nodeLine!!.first, nodeLine.second)
                    }
                }
                roomNodes.nodes.forEach { node ->
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