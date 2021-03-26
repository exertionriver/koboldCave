package render

import node.NodeMesh
import com.soywiz.korge.Korge
import com.soywiz.korge.view.graphics
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import leaf.Leaf

object RenderNodeRooms {

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRooms() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {
/*
        val roomColors = listOf(Colors.DARKRED, Colors.DARKGREEN,  Colors.DARKBLUE, Colors.DARKMAGENTA, Colors.DARKSEAGREEN, Colors.DARKTURQUOISE
            , Colors.DARKORANGE, Colors.DARKOLIVEGREEN, Colors.DARKSALMON)

        val roomPathColor = Colors["#427d7a"]

        val startingMap = mapOf(
            90 to Point(750, 600)
            , 210 to Point(600, 400)
            , 330 to Point(900, 400)
        )
        graphics {

            val leafFirst = Leaf(initHeight = 4, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(initHeight = 4, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(initHeight = 4, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val nodeMesh = NodeMesh(leafNodes = leafFirst.getNodeList().plus(leafSecond.getNodeList()).plus(leafThird.getNodeList()))
            val nodeRooms = nodeMesh.getClusteredNodes(rooms = 8, maxIterations = 4)
            var colorIdx = 0

            nodeRooms.forEach { nodeRoom ->
                val roomNodes = NodeMesh(leafNodes=nodeRoom.nodes).getConsolidatedLeafNodes()
                roomNodes.forEach { node ->
                    stroke(roomColors[colorIdx % 9], StrokeInfo(thickness = 3.0)) {
                        circle(node.position, radius = 5.0)
                    }
                }
                colorIdx++
            }

            stroke(roomPathColor, StrokeInfo(thickness = 1.0)) {

                for (node in nodeMesh.leafNodes.sortedBy { node -> node.uuid.toString() } ) {
                    if (node.childNodes.isEmpty()) continue

                    //render line to childnode if line has not already been rendered in sorted nodelist
                    for (nodeChild in node.childNodes) {
                        if (nodeChild.uuid.toString() > node.uuid.toString()) continue

                        line(node.position, nodeChild.position)
                    }
                }
            }
        }*/
    }
}