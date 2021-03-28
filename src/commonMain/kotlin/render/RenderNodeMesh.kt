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
import kotlinx.coroutines.delay
import leaf.ILeaf.Companion.LeafDistancePx
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf

object RenderNodeMesh {

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
        
        graphics {

            val leafFirst = Leaf(initHeight = 3, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(initHeight = 3, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(initHeight = 3, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(330) )

            val nodeMesh = leafFirst.getLeafList().plus(leafSecond.getLeafList()).plus(leafThird.getLeafList()).nodeMesh()

            stroke(Colors["#45f049"], StrokeInfo(thickness = 3.0)) {

                circle(firstRefPoint, radius = 5.0)
                circle(secondRefPoint, radius = 5.0)
            }

            stroke(Colors["#52b670"], StrokeInfo(thickness = 3.0)) {

                line(firstRefPoint, secondRefPoint)
            }


            stroke(Colors["#4646b6"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                for (node in nodeMesh.nodes) {
                    circle(node.position, radius = 5.0)
                }
            }

            nodeMesh.consolidateNodes()

            delay(1000)

            stroke(Colors["#9f9a3f"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + xNodeOffset, nodeLine.second + xNodeOffset )
                }
            }

            stroke(Colors["#f4ff0b"], StrokeInfo(thickness = 3.0)) {

                for (node in nodeMesh.nodes) {
                    circle(node.position + xNodeOffset, radius = 5.0)
                }
            }

            nodeMesh.linkNodes()
            
            delay(1000)


            stroke(Colors["#9f3762"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first+ yNodeOffset, nodeLine.second + yNodeOffset )
                }
            }

            stroke(Colors["#ff4494"], StrokeInfo(thickness = 3.0)) {

                for (node in nodeMesh.nodes) {
                    circle(node.position + yNodeOffset, radius = 5.0)
                }
            }

            nodeMesh.consolidateNodes()

            delay(1000)

            stroke(Colors["#7e519f"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + xNodeOffset + yNodeOffset, nodeLine.second + xNodeOffset + yNodeOffset )
                }
            }

            stroke(Colors["#b685ff"], StrokeInfo(thickness = 3.0)) {

                for (node in nodeMesh.nodes) {
                    circle(node.position + xNodeOffset + yNodeOffset, radius = 5.0)
                }
            }
        }
    }
}