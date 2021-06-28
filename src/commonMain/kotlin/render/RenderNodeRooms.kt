package render

import com.soywiz.klock.TimeSpan
import node.NodeMesh
import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import node.INodeMesh
import node.INodeMesh.Companion.addMesh
import node.Node
import node.Node.Companion.angleBetween
import node.Node.Companion.averagePositionWithinNodes
import node.Node.Companion.getFarthestNode
import node.Node.Companion.getNodeLineList
import node.Node.Companion.getRandomNode
import node.Node.Companion.moveNodes
import node.Node.Companion.nearestNodesOrderedAsc
import node.Node.Companion.scaleNodes
import render.RenderPalette.ForeColors

object RenderNodeRooms {

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRooms(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 0
        val funSize = 1

        while ( (funIdx >= 0) && (funIdx < funSize) ) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderNodeRoomsSizes(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
//  future directions:
//                1 -> if ( renderNodeRoomsSetCentroids(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
//                2 -> if ( renderNodeRoomsOrphanAdoptingDiff(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
//                3 -> if ( renderNodeRoomsElaboration(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
            }
        }

        return if (funIdx > 0) ButtonCommand.NEXT else ButtonCommand.PREV
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomsSizes(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeRoomsSizes() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing INodeMesh.buildRoomMesh() at various heights (sizes)")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("rooms are determined by k-means clustering with getClusters()")
        commandViews[CommandView.PREV_BUTTON]!!.visible = true
        commandViews[CommandView.NEXT_BUTTON]!!.visible = false

        RenderPalette.returnClick = null

        val meshListPosition = listOf(
            Point(300, 300)
            , Point(600, 600)
            , Point(800, 900)
        )

        val meshList = listOf(
            INodeMesh.buildRoomMesh(centerPoint = meshListPosition[0], height = 6)
            , INodeMesh.buildRoomMesh(centerPoint = meshListPosition[1], height = 4)
            , INodeMesh.buildRoomMesh(centerPoint = meshListPosition[2], height = 2)
        )

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            meshList.forEachIndexed { nodeMeshIdx, nodeMesh ->

                val rooms = nodeMesh.nodes.size / 8
                val maxIter = nodeMesh.nodes.size / 6

//                println("nodeMeshIdx:$nodeMeshIdx, rooms:$rooms, maxIter:$maxIter")

                secondContainer.text(text = "NodeMesh(height=${7 - nodeMeshIdx * 2}, rooms=${rooms}, maxIter=${maxIter})"
                    , color = ForeColors[nodeMeshIdx % ForeColors.size]
                    , alignment = RenderPalette.TextAlignCenter
                ).position(Point(meshListPosition[nodeMeshIdx].x, meshListPosition[nodeMeshIdx].y - 100))

                val clusters = nodeMesh.getClusters(rooms = rooms, maxIterations = maxIter)

                stroke(RenderPalette.BackColors[nodeMeshIdx % RenderPalette.BackColors.size], StrokeInfo(thickness = 3.0)) {

                    for (meshLine in nodeMesh.getNodeLineList()) {
                        if (meshLine != null) line(meshLine.first, meshLine.second)
                    }
                }

                val renderPoints = if (clusters.isNotEmpty()) clusters.values.flatten() else nodeMesh.nodes

                for (meshNode in renderPoints ) {
                    val numberRegex = Regex("\\d+")

                    val colorIdx = numberRegex.find(meshNode.description, 0)?.value?.toInt() ?: 0

                    secondContainer.circle {
                        position(meshNode.position)
                        radius = 5.0
                        color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                        }
                    }
                }

                if (clusters.isNotEmpty()) {

                    clusters.keys.forEach { node ->

                        val numberRegex = Regex("\\d+")

                        val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                        secondContainer.circle {
                            position(node.position)
                            radius = 10.0
                            color = ForeColors[colorIdx % ForeColors.size]
                            strokeThickness = 3.0
                            onClick {
                                commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                                commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                            }
                        }
                    }
                }
            }
        }
        while (RenderPalette.returnClick == null) {
            delay(TimeSpan(100.0))
        }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }
/*
    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomsSetCentroids(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeRoomsSetCentroids() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing INodeMesh.buildCentroidRoomMesh() at various heights (sizes)")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("rooms are determined by input centroid mesh clustering with setClusters()")
        commandViews[CommandView.PREV_BUTTON]!!.visible = true
        commandViews[CommandView.NEXT_BUTTON]!!.visible = false

        RenderPalette.returnClick = null

        val centroidPosition = Point(800, 200)

        val centroidMesh = INodeMesh.buildRoomMesh(centroidPosition, height = 2) as NodeMesh

        val meshListOffsetPosition = listOf(
            Point(-600, 200)
            , Point(-200, 400)
            , Point(-400, 700)
        )

        val centroidMeshList = listOf(
            NodeMesh(nodes = centroidMesh.nodes.scaleNodes(scale = 1.5).moveNodes(meshListOffsetPosition[0]) )
            , NodeMesh(nodes = centroidMesh.nodes.scaleNodes(scale = 1.0).moveNodes(meshListOffsetPosition[1]) )
            , NodeMesh(nodes = centroidMesh.nodes.scaleNodes(scale = 0.5).moveNodes(meshListOffsetPosition[2]) )
        )

        val textOffsetPosition = Point (0, -100)

        val meshList = listOf(
            INodeMesh.buildCentroidRoomMesh(height = 7, centroids = centroidMeshList[0].nodes)
            , INodeMesh.buildCentroidRoomMesh(height = 5, centroids = centroidMeshList[1].nodes)
            , INodeMesh.buildCentroidRoomMesh(height = 3, centroids = centroidMeshList[2].nodes)
        )

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            secondContainer.text(text = "Centroid NodeMesh(height=2)"
                , color = ForeColors[0]
                , alignment = RenderPalette.TextAlignCenter
            ).position(centroidPosition + textOffsetPosition)

            stroke(RenderPalette.BackColors[0], StrokeInfo(thickness = 3.0)) {

                for (meshLine in centroidMesh.getNodeLineList()) {
                    if (meshLine != null) line(meshLine.first, meshLine.second)
                }
            }

            for (meshNode in centroidMesh.nodes) {
                secondContainer.circle {
                    position(meshNode.position)
                    radius = 5.0
                    color = ForeColors[0]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                    }
                }
            }

            centroidMeshList.forEachIndexed { nodeMeshIdx, nodeMesh ->

                stroke(RenderPalette.BackColors[(nodeMeshIdx + 1) % RenderPalette.BackColors.size], StrokeInfo(thickness = 3.0)) {

                    for (meshLine in nodeMesh.getNodeLineList()) {
                        if (meshLine != null) line(meshLine.first, meshLine.second)
                    }
                }

                for (meshNode in nodeMesh.nodes) {
                    secondContainer.circle {
                        position(meshNode.position)
                        radius = 15.0
                        color = ForeColors[(nodeMeshIdx + 1) % ForeColors.size]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                        }
                    }
                }
            }

            meshList.forEachIndexed { nodeMeshIdx, nodeMesh ->

                val maxIter = nodeMesh.nodes.size / 6

                secondContainer.text(text = "NodeMesh(height=${4 - nodeMeshIdx}, rooms=${centroidMesh.nodes.size}, maxIter=${maxIter})"
                    , color = ForeColors[nodeMeshIdx % ForeColors.size]
                    , alignment = RenderPalette.TextAlignCenter
                ).position(centroidPosition + meshListOffsetPosition[nodeMeshIdx] + textOffsetPosition)

                stroke(RenderPalette.BackColors[nodeMeshIdx % RenderPalette.BackColors.size], StrokeInfo(thickness = 3.0)) {

                    for (meshLine in nodeMesh.getNodeLineList()) {
                        if (meshLine != null) line(meshLine.first, meshLine.second)
                    }
                }

                for (meshNode in nodeMesh.nodes) {
                    val numberRegex = Regex("\\d+")

                    val colorIdx = numberRegex.find(meshNode.description, 0)?.value?.toInt() ?: 0

                    secondContainer.circle {
                        position(meshNode.position)
                        radius = 5.0
                        color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                        }
                    }
                }
            }
        }
        while (RenderPalette.returnClick == null) {
            delay(TimeSpan(100.0))
        }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }
*/
/*
    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomsOrphanAdoptingDiff(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        graphics {

            RenderNodeRooms.textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            RenderNavigation.roomView = text(text = "current room", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 70)

            val orphanAdoptOffset = Point(0, 400)

            val centroidMesh = INodeMesh.buildRoomMesh(Point(100, 200), height = 2)

            //           println("centroidMesh:$centroidMesh")

            for (nodeLine in centroidMesh.getNodeLineList()) {

                stroke(RenderPalette.BackColors[2], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            centroidMesh.nodes.forEach { node ->

                circle { position(node.position)
                    radius = 5.0
                    color = RenderPalette.ForeColors[2]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }

            val roomMesh = INodeMesh.buildCentroidRoomMesh(
                height = 3,
                centroids = centroidMesh.nodes.moveNodes(Point(412, 112)).scaleNodes(scale = 1.5)
            )

            for (nodeLine in roomMesh.getNodeLineList()) {

                stroke(RenderPalette.BackColors[1], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

//            println("drawing nodes")
            roomMesh.nodes.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position)
                    radius = 5.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }

            roomMesh.centroids.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position)
                    radius = 10.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }

            roomMesh.adoptRoomOrphans()

            for (nodeLine in roomMesh.getNodeLineList()) {

                stroke(RenderPalette.BackColors[3], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first + orphanAdoptOffset, nodeLine.second + orphanAdoptOffset )
                }
            }

            roomMesh.nodes.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position + orphanAdoptOffset)
                    radius = 5.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }

            roomMesh.centroids.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                circle { position(node.position + orphanAdoptOffset)
                    radius = 10.0
                    color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick{
                        RenderNodeRooms.updateNodeText(node.uuid.toString())
                        RenderNavigation.updateRoomText(node.description)
                    }
                }
            }
        }
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomsElaboration(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        val startingMap = mapOf(
            90 to Point(450, 600)
            , 330 to Point(300, 400)
            , 210 to Point(600, 400)
        )
        graphics {

            textView = text(text = "click a node to get uuid", color = Colors.AZURE, textSize = 24.0, alignment = TextAlignment.BASELINE_LEFT).position(20, 20)

            val leafFirst = Leaf(topHeight = 7, position = startingMap[90]!!, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = 7, position = startingMap[210]!!, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = 7, position = startingMap[330]!!, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()
            val nodeClusters = nodeMesh.getClusters(rooms = 14, maxIterations = 7)

            var colorIdx = 0

            nodeMesh.consolidateNearNodes()

            nodeMesh.linkNearNodes()

            val allRooms = NodeMesh()

            nodeClusters.values.forEachIndexed { clusterIdx, clusterNodes -> allRooms.addMesh(NodeMesh("room$clusterIdx", clusterNodes)) }

            allRooms.consolidateNearNodes()

            allRooms.linkNearNodes()

            (1..5).forEach {

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

                val centroid = Node(position = allRooms.nodes.averagePositionWithinNodes())

                stroke(Colors["#3939ad"], StrokeInfo(thickness = 3.0)) {
                    circle(centroid.position, radius = 5.0)
                }

                val farthestNode = allRooms.nodes.getFarthestNode(centroid)

                val outerNodes60 = allRooms.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .5 }

                stroke(Colors["#56f636"], StrokeInfo(thickness = 3.0)) {

                    for (node in outerNodes60) {
                        circle(node.position, radius = 5.0)
                    }
                }

                val randomOuter60Node = outerNodes60.getRandomNode()

                stroke(Colors["#f65862"], StrokeInfo(thickness = 3.0)) {

                    circle(randomOuter60Node.position, radius = 5.0)
                }

                delay(3000)

                println("elaboration position: ${randomOuter60Node.position}")

                println("elaboration angle: ${centroid.angleBetween(randomOuter60Node)}")

                val newMesh = Leaf(topHeight = 5, position = randomOuter60Node.position, angleFromParent = centroid.angleBetween(randomOuter60Node) ).getList().nodeMesh()

                newMesh.consolidateNearNodes()

                newMesh.linkNearNodes()

                allRooms.addMesh(newMesh)

            }
        }
    }*/
}