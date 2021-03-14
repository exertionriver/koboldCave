import com.soywiz.korge.Korge
import com.soywiz.korge.view.graphics
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line

object RenderNodeMesh {

    @ExperimentalUnsignedTypes
    suspend fun renderNodeMeshStationary() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingMap = mapOf(
            0 to Point(512, 674)
            , 120 to Point(212, 374)
            , 240 to Point(812, 374)
        )
        graphics {

            val leafFirst = OakLeaf(initHeight = 5, startingPosition = startingMap[0], startingAngle = Angle.fromDegrees(0) , relativeAngle = true)
            val leafSecond = OakLeaf(initHeight = 5, startingPosition = startingMap[120], startingAngle = Angle.fromDegrees(120), relativeAngle = true )
            val leafThird = OakLeaf(initHeight = 5, startingPosition = startingMap[240], startingAngle = Angle.fromDegrees(240), relativeAngle = true)

            val nodeMesh = NodeMesh(leafNodes = leafFirst.getNodeList().plus(leafSecond.getNodeList()).plus(leafThird.getNodeList()))
            val consolidatedNodes = nodeMesh.getConsolidatedLeafNodes()

            stroke(Colors["#f4ff0b"], StrokeInfo(thickness = 3.0)) {
                for (node in leafFirst.getLeafNodeList() ) {
                    circle(node.position, radius = 5.0)
                }
                for (node in leafSecond.getLeafNodeList() ) {
                    circle(node.position, radius = 5.0)
                }
                for (node in leafThird.getLeafNodeList() ) {
                    circle(node.position, radius = 5.0)
                }
            }

            stroke(Colors["#9f9a3f"], StrokeInfo(thickness = 3.0)) {

                for (line in leafFirst.getLeafLineList() ) {
                    line(line.first, line.second)
                }
                for (line in leafSecond.getLeafLineList() ) {
                    line(line.first, line.second)
                }
                for (line in leafThird.getLeafLineList() ) {
                    line(line.first, line.second)
                }

            }

            stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                for (node in consolidatedNodes) {
                    if (node.childNodes.isEmpty()) continue

                    for (nodeChild in node.childNodes) {

                        circle(node.position, radius = 5.0)
                    }
                }
            }

            stroke(Colors["#4646b6"], StrokeInfo(thickness = 3.0)) {

                for (node in nodeMesh.getConsolidatedLeafNodes() ) {
                    if (node.childNodes.isEmpty()) continue

                    //render line to childnode if line has not already been rendered in sorted nodelist
                    for (nodeChild in node.childNodes) {
                        if (nodeChild.uuid.toString() > node.uuid.toString()) continue

                        line(node.position, nodeChild.position)
                    }
                }
            }
        }
    }
}