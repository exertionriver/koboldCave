package render

import node.NodeMesh
import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import kotlinx.coroutines.delay
import leaf.ILeaf.Companion.NextDistancePx
import node.INodeMesh.Companion.addMesh
import node.Node
import node.NodeLink.Companion.buildNodeLinkLine
import node.NodeLink.Companion.consolidateNodeDistance

object RenderNodeLine {
/*
    @ExperimentalUnsignedTypes
    suspend fun renderNodeLine(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 0
        val funSize = 2

        while ( (funIdx >= 0) && (funIdx < funSize) ) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderNodeLineHeightsNoises(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                1 -> if ( renderNodeLineMesh(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
//  future directions:
//                2 -> if ( renderGraftedNodeLines(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
//                3 -> if ( renderMeshGraftedNodeLine(renderContainer, commandViews) == ButtonCommand.NEXT) funIdx++ else funIdx--
            }
        }

        return if (funIdx > 0) ButtonCommand.NEXT else ButtonCommand.PREV
    }

    //	renderNodeLineStationary()

    //	renderNodeLineMesh()

    @ExperimentalUnsignedTypes
    suspend fun renderNodeLineHeightsNoises(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        val startingMap = mutableListOf(
            Node( position = Point(212, 374) )
            , Node( position = Point(122, 574) )
            , Node( position = Point(512, 474) )
            , Node( position = Point(612, 674) )
            , Node( position = Point(512, 180) )
        )

        val xOffset = Point(50, 0)

        val firstRefPoint = Point(300, 200)
        val secondRefPoint = Point (300 + NextDistancePx, 200)

        val thirdRefPoint = Point(300, 250)
        val fourthRefPoint = Point (300 + consolidateNodeDistance, 250)


        graphics {

            RenderNodeRooms.textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            val nodeMesh = NodeMesh(linkNodes = startingMap)

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

            stroke(Colors["#20842b"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    if (nodeLine != null) line(nodeLine.first, nodeLine.second)
                }
            }

            for (node in nodeMesh.nodes ) {
                circle {
                    position(node.position )
                    radius = 7.0
                    color = Colors["#42f048"]
                    strokeThickness = 1.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            delay(1000)

            nodeMesh.buildNodeLinkLines(noise = 60)

            stroke(Colors["#4646b6"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + xOffset, nodeLine.second + xOffset)
                }
            }

            for (node in nodeMesh.nodes) {
                circle {
                    position(node.position + xOffset)
                    radius = 7.0
                    color = Colors["#5f5ff0"]
                    strokeThickness = 1.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            nodeMesh.consolidateNearNodes()

            delay(1000)

            stroke(Colors["#9f9a3f"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + xOffset * 2, nodeLine.second + xOffset * 2)
                }
            }

            for (node in nodeMesh.nodes) {
 //               println("node: $node")

                circle {
                    position(node.position + xOffset * 2)
                    radius = 7.0
                    color = Colors["#f4ff0b"]
                    strokeThickness = 1.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeLineMesh(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        val startingMap = mutableListOf(
            Node( position = Point(352, 474) )
            , Node( position = Point(262, 674) )
            , Node( position = Point(652, 574) )
            , Node( position = Point(752, 774) )
            , Node( position = Point(652, 280) )
        )

        val xOffset = Point(250, 0)

        val firstRefPoint = Point(300, 200)
        val secondRefPoint = Point (300 + NextDistancePx, 200)

        val thirdRefPoint = Point(300, 250)
        val fourthRefPoint = Point (300 + consolidateNodeDistance, 250)


        graphics {

            RenderNodeRooms.textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

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

            val lineNodeMesh = NodeMesh()

            startingMap.forEachIndexed { outerIndex, outer ->
                startingMap.forEachIndexed { innerIndex, inner ->
                    if (innerIndex > outerIndex) {
                        lineNodeMesh.addMesh( Pair(outer, inner).buildNodeLinkLine(noise = 70, lineNodeMesh.description) )
                    }
                }
            }

            stroke(Colors["#4646b6"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in lineNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first - xOffset, nodeLine.second - xOffset)
                }
            }

            for (node in lineNodeMesh.nodes) {
                circle {
                    position(node.position - xOffset)
                    radius = 7.0
                    color = Colors["#5f5ff0"]
                    strokeThickness = 1.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            lineNodeMesh.linkNearNodes()

            lineNodeMesh.consolidateNearNodes()

            delay(1000)

            stroke(Colors["#9f9a3f"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in lineNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + xOffset, nodeLine.second + xOffset)
                }
            }

            for (node in lineNodeMesh.nodes) {
                //               println("node: $node")

                circle {
                    position(node.position + xOffset)
                    radius = 7.0
                    color = Colors["#f4ff0b"]
                    strokeThickness = 1.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }
        }
    }*/
}