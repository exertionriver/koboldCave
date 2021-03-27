package render

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.Korge
import com.soywiz.korge.view.graphics
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.sin
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import leaf.ILeaf.Companion.addLeaf
import leaf.ILeaf.Companion.graftLeaf
import leaf.ILeaf.Companion.nodeLinks
import leaf.ILeaf.Companion.nodes
import leaf.Leaf
import node.INodeMesh
import node.Node
import node.NodeMesh
import kotlin.random.Random

object RenderNode {

    @ExperimentalUnsignedTypes
    suspend fun renderLeafAndNodes() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingPointLeaf = Point(412.0, 512.0)
        val nodeOffset = Point(200.0, 0.0)

        val leaf = Leaf(initHeight = 4, position = startingPointLeaf)

        graphics {
            stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                for (listLeaf in leaf.getLeafList()) {
                    circle(listLeaf.position, radius = 3.0)
                }
            }
            stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {

                for (line in leaf.getLeafLineList()) {
                    if (line != null) line(line.first, line.second)
                }
            }

            delay(TimeSpan(1500.0))

            stroke(Colors["#f0f057"], StrokeInfo(thickness = 3.0)) {

                for (listLeaf in leaf.getLeafList()) {

                    circle( Node(listLeaf).position.plus(nodeOffset), radius = 3.0 )
                }
            }
            stroke(Colors["#818436"], StrokeInfo(thickness = 3.0)) {

                for (listLeaf in leaf.getLeafList()) {

                    listLeaf.nodes().forEach { node -> line(listLeaf.position.plus(nodeOffset), node.position.plus(nodeOffset)) }

                }
            }
        }
    }
}