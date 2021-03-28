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
import leaf.ILeaf.Companion.LeafDistancePx
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import node.NodeLink.Companion.consolidateNodeDistance

object RenderNodeMesh {

    lateinit var textView : View

    fun updateNodeText(uuidString : String) {
        textView.setText(uuidString)
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeMeshStationary() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingMap = mapOf(
            90 to Point(212, 374)
   //         , 210 to Point(212, 374)
   //         , 330 to Point(812, 374)
        )

        val xNodeOffset = Point(400.0, 0.0)
        val yNodeOffset = Point(0.0, 400.0)

        val firstRefPoint = Point(300, 200)
        val secondRefPoint = Point (300 + LeafDistancePx, 200)

        val thirdRefPoint = Point(300, 250)
        val fourthRefPoint = Point (300 + consolidateNodeDistance, 250)


        graphics {

            RenderNodeRooms.textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            val leafFirst = Leaf(initHeight = 3, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(initHeight = 3, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(initHeight = 3, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(330) )

            val nodeMesh = leafFirst.getLeafList().plus(leafSecond.getLeafList()).plus(leafThird.getLeafList()).nodeMesh()

            stroke(Colors["#52b670"], StrokeInfo(thickness = 3.0)) {

                line(firstRefPoint, secondRefPoint)
                line(thirdRefPoint, fourthRefPoint)
            }

            stroke(Colors["#45f049"], StrokeInfo(thickness = 3.0)) {

                circle(firstRefPoint, radius = 5.0)
                circle(secondRefPoint, radius = 5.0)
                circle(thirdRefPoint, radius = 5.0)
                circle(fourthRefPoint, radius = 5.0)
            }

            stroke(Colors["#4646b6"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            for (node in nodeMesh.nodes) {
                circle {
                    position(node.position)
                    radius = 5.0
                    color = Colors["#5f5ff0"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            nodeMesh.consolidateNodes()

            delay(1000)

            stroke(Colors["#9f9a3f"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + xNodeOffset, nodeLine.second + xNodeOffset )
                }
            }

            for (node in nodeMesh.nodes) {
                circle {
                    position(node.position + xNodeOffset)
                    radius = 5.0
                    color = Colors["#f4ff0b"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            nodeMesh.linkNodes()
            
            delay(1000)

            stroke(Colors["#9f3762"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first+ yNodeOffset, nodeLine.second + yNodeOffset )
                }
            }

            for (node in nodeMesh.nodes) {
                circle {
                    position(node.position + yNodeOffset)
                    radius = 5.0
                    color = Colors["#ff4494"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            nodeMesh.consolidateNodes()

            delay(1000)

            stroke(Colors["#7e519f"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + xNodeOffset + yNodeOffset, nodeLine.second + xNodeOffset + yNodeOffset )
                }
            }

            for (node in nodeMesh.nodes) {
                circle {
                    position(node.position + xNodeOffset + yNodeOffset)
                    radius = 5.0
                    color = Colors["#b685ff"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }
        }
    }
}