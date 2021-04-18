package render

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
import leaf.ILeaf.Companion.NextDistancePx
import leaf.ILeaf.Companion.getLineList
import leaf.ILeaf.Companion.getList
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import node.INodeMesh.Companion.absorbMesh
import node.INodeMesh.Companion.buildRoomMesh
import node.Node
import node.Node.Companion.randomPosition
import node.NodeLink.Companion.consolidateNodeDistance
import node.NodeLink.Companion.linkNodeDistance
import node.NodeMesh

object RenderNodeMesh {

    lateinit var textView : View

    fun updateNodeText(uuidString : String) {
        textView.setText(uuidString)
    }

    fun updateRoomText(roomDescription : String) {
        RenderNavigation.roomView.setText(roomDescription)
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeMeshStationaryOperations() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingPoint = Point(212, 374)

        val textOffset = Point(-200, -200)
        val centerMeshOffset = Point(250, 200)

        val xNodeOffset = Point(600.0, 0.0)
        val yNodeOffset = Point(0.0, 400.0)

        val firstRefPoint = Point(350, 200)
        val secondRefPoint = Point (350 + linkNodeDistance, 200)

        val thirdRefPoint = Point(350, 250)
        val fourthRefPoint = Point (350 + consolidateNodeDistance, 250)


        graphics {

            RenderNodeRooms.textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            val leafFirst = Leaf(topHeight = 3, position = startingPoint, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = 3, position = startingPoint, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = 3, position = startingPoint, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()

            text(text = "linkNodeDistance", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(firstRefPoint + Point(-10, -25))
            text(text = "consolidateNodeDistance", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(thirdRefPoint + Point(-10, -25))

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

//            text(text = "threeLeaf", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(startingPoint + leafOffset + textOffset)

            stroke(Colors["#20842b"], StrokeInfo(thickness = 3.0)) {

                for (line in threeLeaf.getLineList() ) {
                    if (line != null) line(line.first + centerMeshOffset, line.second + centerMeshOffset)
                }
            }

            for (listLeaf in threeLeaf.getList() ) {
                println (listLeaf)
                circle {
                    position(listLeaf.position + centerMeshOffset)
                    radius = 5.0
                    color = Colors["#42f048"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(listLeaf.uuid.toString())
                    }
                }
            }

            text(text = "nodeMesh from threeLeaf", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(startingPoint + centerMeshOffset + textOffset)

            stroke(Colors["#4646b6"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + centerMeshOffset, nodeLine.second + centerMeshOffset )
                }
            }

            for (node in nodeMesh.nodes) {
                circle {
                    position(node.position + centerMeshOffset)
                    radius = 5.0
                    color = Colors["#5f5ff0"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            val consolidateNodeMesh = NodeMesh(nodeMesh)

            consolidateNodeMesh.consolidateNearNodes()

            text(text = "consolidatedNode nodeMesh", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(startingPoint + textOffset)

            stroke(Colors["#9f9a3f"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in consolidateNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            for (node in consolidateNodeMesh.nodes ) {
                circle {
                    position(node.position )
                    radius = 5.0
                    color = Colors["#f4ff0b"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            val linkedNodeMesh = NodeMesh(nodeMesh)

            linkedNodeMesh.linkNearNodes()

            text(text = "linkedNode nodeMesh", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(startingPoint + xNodeOffset + textOffset)

            stroke(Colors["#9f3762"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in linkedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first+ xNodeOffset, nodeLine.second + xNodeOffset )
                }
            }

            for (node in linkedNodeMesh.nodes) {
                circle {
                    position(node.position + xNodeOffset)
                    radius = 5.0
                    color = Colors["#ff4494"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            val consolidatedLinkedNodeMesh = NodeMesh(nodeMesh)

            consolidatedLinkedNodeMesh.consolidateNearNodes()

            consolidatedLinkedNodeMesh.linkNearNodes()

            text(text = "consolidated + linked nodeMesh", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(startingPoint + yNodeOffset + textOffset)

            stroke(Colors["#7e519f"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in consolidatedLinkedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + yNodeOffset, nodeLine.second + yNodeOffset )
                }
            }

            for (node in consolidatedLinkedNodeMesh.nodes) {
                circle {
                    position(node.position + yNodeOffset)
                    radius = 5.0
                    color = Colors["#b685ff"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            val linkedConsolidatedNodeMesh = NodeMesh(nodeMesh)

            linkedConsolidatedNodeMesh.linkNearNodes()

            linkedConsolidatedNodeMesh.consolidateNearNodes()

            text(text = "linked + consolidated nodeMesh", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(startingPoint + xNodeOffset + yNodeOffset + textOffset)

            stroke(Colors["#5c9f58"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in linkedConsolidatedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + xNodeOffset + yNodeOffset, nodeLine.second + xNodeOffset + yNodeOffset )
                }
            }

            for (node in linkedConsolidatedNodeMesh.nodes) {
                circle {
                    position(node.position + xNodeOffset + yNodeOffset)
                    radius = 5.0
                    color = Colors["#9dffa1"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeMeshStationaryOperationsExtended() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingPoint = Point(212, 374)

        val textOffset = Point(-200, -200)
        val centerMeshOffset = Point(250, 200)

        val xNodeOffset = Point(600.0, 0.0)
        val yNodeOffset = Point(0.0, 400.0)

        val firstRefPoint = Point(350, 200)
        val secondRefPoint = Point (350 + linkNodeDistance, 200)

        val thirdRefPoint = Point(350, 250)
        val fourthRefPoint = Point (350 + consolidateNodeDistance, 250)


        graphics {

            RenderNodeRooms.textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            val leafFirst = Leaf(topHeight = 3, position = startingPoint, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = 3, position = startingPoint, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = 3, position = startingPoint, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()

            text(text = "linkNodeDistance", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(firstRefPoint + Point(-10, -25))
            text(text = "consolidateNodeDistance", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(thirdRefPoint + Point(-10, -25))

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

//            text(text = "threeLeaf", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(startingPoint + leafOffset + textOffset)

            stroke(Colors["#20842b"], StrokeInfo(thickness = 3.0)) {

                for (line in threeLeaf.getLineList() ) {
                    if (line != null) line(line.first + centerMeshOffset, line.second + centerMeshOffset)
                }
            }

            for (listLeaf in threeLeaf.getList() ) {
//                println (listLeaf)
                circle {
                    position(listLeaf.position + centerMeshOffset)
                    radius = 5.0
                    color = Colors["#42f048"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(listLeaf.uuid.toString())
                    }
                }
            }

            val consolidatedLinkedNodeMesh = NodeMesh(nodeMesh)

            consolidatedLinkedNodeMesh.consolidateNearNodes()

            consolidatedLinkedNodeMesh.linkNearNodes()

            text(text = "consolidated + linked nodeMesh", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(startingPoint + centerMeshOffset + textOffset)

            stroke(Colors["#4646b6"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in consolidatedLinkedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + centerMeshOffset, nodeLine.second + centerMeshOffset )
                }
            }

            for (node in consolidatedLinkedNodeMesh.nodes) {
                circle {
                    position(node.position + centerMeshOffset)
                    radius = 5.0
                    color = Colors["#5f5ff0"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            val clcProcessedNodeMesh = NodeMesh(nodeMesh)

            clcProcessedNodeMesh.consolidateNearNodes()

            clcProcessedNodeMesh.linkNearNodes()

            clcProcessedNodeMesh.consolidateNodeLinks()

            text(text = "cons + link + consLink nodeMesh", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(startingPoint + textOffset)

            stroke(Colors["#9f9a3f"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in clcProcessedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            for (node in clcProcessedNodeMesh.nodes ) {
                circle {
                    position(node.position )
                    radius = 5.0
                    color = Colors["#f4ff0b"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            val clpProcessedNodeMesh = NodeMesh(nodeMesh)

            clpProcessedNodeMesh.consolidateNearNodes()

            clpProcessedNodeMesh.linkNearNodes()

            clpProcessedNodeMesh.pruneNodeLinks()

            text(text = "cons + link + pruned links nodeMesh", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(startingPoint + xNodeOffset + textOffset)

            stroke(Colors["#9f3762"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in clpProcessedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first+ xNodeOffset, nodeLine.second + xNodeOffset )
                }
            }

            for (node in clpProcessedNodeMesh.nodes) {
                circle {
                    position(node.position + xNodeOffset)
                    radius = 5.0
                    color = Colors["#ff4494"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            val clpcProcessedNodeMesh = NodeMesh(nodeMesh)

            clpcProcessedNodeMesh.consolidateNearNodes()

            clpcProcessedNodeMesh.linkNearNodes()

            clpcProcessedNodeMesh.pruneNodeLinks()

            clpcProcessedNodeMesh.consolidateNodeLinks()

            text(text = "cons + link + pruned + cl nodeMesh", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(startingPoint + yNodeOffset + textOffset)

            stroke(Colors["#7e519f"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in clpcProcessedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + yNodeOffset, nodeLine.second + yNodeOffset )
                }
            }

            for (node in clpcProcessedNodeMesh.nodes) {
                circle {
                    position(node.position + yNodeOffset)
                    radius = 5.0
                    color = Colors["#b685ff"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }

            val clcpProcessedNodeMesh = NodeMesh(nodeMesh)

            clcpProcessedNodeMesh.consolidateNearNodes()

            clcpProcessedNodeMesh.linkNearNodes()

            clcpProcessedNodeMesh.consolidateNodeLinks()

            clcpProcessedNodeMesh.pruneNodeLinks()

            text(text = "cons + link + cl + pruned nodeMesh", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(startingPoint + xNodeOffset + yNodeOffset + textOffset)

            stroke(Colors["#5c9f58"], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in clcpProcessedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + xNodeOffset + yNodeOffset, nodeLine.second + xNodeOffset + yNodeOffset )
                }
            }

            for (node in clcpProcessedNodeMesh.nodes) {
                circle {
                    position(node.position + xNodeOffset + yNodeOffset)
                    radius = 5.0
                    color = Colors["#9dffa1"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                    }
                }
            }
        }
    }


    @ExperimentalUnsignedTypes
    suspend fun renderAbsorbedNodeMesh() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val startingMap = mapOf(
            90 to Point(212, 374)
            , 210 to Point(212, 374)
            , 330 to Point(812, 374)
        )
        val leafOffset = Point(250, 200)

        val xNodeOffset = Point(600.0, 0.0)
        val yNodeOffset = Point(0.0, 400.0)

        val firstRefPoint = Point(300, 200)
        val secondRefPoint = Point (300 + NextDistancePx, 200)

        val thirdRefPoint = Point(300, 250)
        val fourthRefPoint = Point (300 + consolidateNodeDistance, 250)


        graphics {

            RenderNodeRooms.textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            val leafFirst = Leaf(topHeight = 3, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = 3, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = 3, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()

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

                for (line in threeLeaf.getLineList() ) {
                    if (line != null) line(line.first + leafOffset, line.second + leafOffset)
                }
            }

            for (listLeaf in threeLeaf.getList() ) {
                println (listLeaf)
                circle {
                    position(listLeaf.position + leafOffset)
                    radius = 5.0
                    color = Colors["#42f048"]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(listLeaf.uuid.toString())
                    }
                }
            }

            val absorbingNodeMesh = NodeMesh()
            val randomNode = Node(position = nodeMesh.nodes.randomPosition())

            circle {
                position(randomNode.position)
                radius = 10.0
                color = Colors["#e9f06e"]
                strokeThickness = 3.0
                onClick{
                    RenderNodeRooms.updateNodeText(randomNode.uuid.toString())
                }
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

            (0..10).toList().forEach { index ->
                absorbingNodeMesh.absorbMesh(randomNode, index * 50.0, nodeMesh)

                stroke(Colors["#b6b297"], StrokeInfo(thickness = 3.0)) {

                    for (nodeLine in absorbingNodeMesh.getNodeLineList() ) {
                        line(nodeLine!!.first, nodeLine.second )
                    }
                }

                for (node in absorbingNodeMesh.nodes) {
                    circle {
                        position(node.position)
                        radius = 5.0
                        color = Colors["#edf0a5"]
                        strokeThickness = 3.0
                        onClick{
                            RenderNodeRooms.updateNodeText(node.uuid.toString())
                        }
                    }
                }

                delay(1000)
            }
        }
    }
    @ExperimentalUnsignedTypes
    suspend fun renderNodeMeshRooms() = Korge(width = 1024, height = 1024, bgcolor = Colors["#2b2b2b"]) {

        val roomColors = listOf(Colors.DARKRED, Colors.DARKGREEN,  Colors.BLUE, Colors.DARKMAGENTA, Colors.DARKSEAGREEN, Colors.DARKTURQUOISE
            , Colors.DARKORANGE, Colors.DARKOLIVEGREEN, Colors.DARKSALMON)

        graphics {

            RenderNodeRooms.textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            RenderNavigation.roomView = text(text = "current room", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 70)

            val roomMesh = buildRoomMesh(Point(512, 512), height = 5)

            println("drawing lines")
            for (nodeLine in roomMesh.getNodeLineList()) {

                stroke(Colors["#343ab6"], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            println("drawing nodes")
            roomMesh.nodes.forEach { node ->

          //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position)
                    radius = 5.0
                    color = roomColors[colorIdx % 9]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }
        }
    }
}