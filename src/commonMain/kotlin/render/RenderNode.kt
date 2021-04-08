package render

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.Korge
import com.soywiz.korge.view.graphics
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import leaf.ILeaf.Companion.nodes
import leaf.Leaf
import node.Node

object RenderNode {

    @ExperimentalUnsignedTypes
    suspend fun renderLeafAndNodes() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingPointLeaf = Point(412.0, 512.0)
        val nodeOffset = Point(200.0, 0.0)

        val leaf = Leaf(topHeight = 4, position = startingPointLeaf)

        graphics {
            stroke(Colors["#343484"], StrokeInfo(thickness = 3.0)) {

                for (line in leaf.getLineList()) {
                    if (line != null) line(line.first - nodeOffset, line.second - nodeOffset)
                }
            }
            stroke(Colors["#5f5ff0"], StrokeInfo(thickness = 3.0)) {

                for (listLeaf in leaf.getList()) {
                    circle(listLeaf.position - nodeOffset, radius = 5.0)
                }
            }


            delay(TimeSpan(1500.0))

            stroke(Colors["#818436"], StrokeInfo(thickness = 3.0)) {

                for (listLeaf in leaf.getList()) {

                    listLeaf.nodes().forEach { node -> line(listLeaf.position + nodeOffset, node.position + nodeOffset) }

                }
            }
            stroke(Colors["#f0f057"], StrokeInfo(thickness = 3.0)) {

                for (listLeaf in leaf.getList()) {

                    circle( Node(listLeaf).position + nodeOffset, radius = 5.0 )
                }
            }
        }
    }
}