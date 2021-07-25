package render

import com.soywiz.klock.TimeSpan
import node.NodeMesh
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.line
import exploreKeys
import node.INodeMesh
import node.INodeMesh.Companion.addMesh
import node.INodeMesh.Companion.getBorderingMesh
import node.Node.Companion.moveNodes
import node.Node.Companion.scaleNodes
import render.RenderPalette.BackColors
import render.RenderPalette.ForeColors

object RenderNodeRoom {

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoom(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 0
        val funSize = 4

        while ( (funIdx >= 0) && (funIdx < funSize) ) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())
            commandViews[CommandView.NODE_POSITION_TEXT].setText(CommandView.NODE_POSITION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderNodeRoomsSizes(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                1 -> if ( renderNodeRoomsBordering(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                2 -> if ( renderNodeRoomsSetCentroids(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                3 -> if ( renderNodeRoomsOrphanAdoptingDiff(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
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
        commandViews[CommandView.NEXT_BUTTON]!!.visible = true

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

                stroke(BackColors[nodeMeshIdx % BackColors.size], StrokeInfo(thickness = 3.0)) {

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
                        color = ForeColors[colorIdx % ForeColors.size]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
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
                                commandViews[CommandView.NODE_POSITION_TEXT].setText(node.position.toString())
                            }
                        }
                    }
                }
            }
        }
        secondContainer.exploreKeys()

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }


    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomsBordering(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeRoomsBordering() [v0.4]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing getBorderingMesh()")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("different colored points indicate different rooms within a node mesh")
        commandViews[CommandView.NEXT_BUTTON]!!.visible = true
        RenderPalette.returnClick = null

        val meshListPositions = listOf(
            Point(500, 500)
            , Point(600, 600)
            , Point(400, 600)
            , Point(400, 400)
            , Point(600, 400)
            , Point(500, 700)
        )

        val borderingNodeRoomCases = meshListPositions.map { meshListPosition ->
            INodeMesh.buildRoomMesh(centerPoint = meshListPosition, height = 5)
        }

        val borderingMesh = listOf(
            NodeMesh(copyNodeMesh = borderingNodeRoomCases[0] as NodeMesh)
            , NodeMesh(copyNodeMesh = borderingNodeRoomCases[1] as NodeMesh).getBorderingMesh(borderingNodeRoomCases[0])
            , NodeMesh(copyNodeMesh = borderingNodeRoomCases[2] as NodeMesh).getBorderingMesh(borderingNodeRoomCases[0].addMesh(borderingNodeRoomCases[1]))
            , NodeMesh(copyNodeMesh = borderingNodeRoomCases[3] as NodeMesh).getBorderingMesh(borderingNodeRoomCases[0].addMesh(borderingNodeRoomCases[1]).addMesh(borderingNodeRoomCases[2]))
            , NodeMesh(copyNodeMesh = borderingNodeRoomCases[4] as NodeMesh).getBorderingMesh(borderingNodeRoomCases[0].addMesh(borderingNodeRoomCases[1]).addMesh(borderingNodeRoomCases[2]).addMesh(borderingNodeRoomCases[3]))
            , NodeMesh(copyNodeMesh = borderingNodeRoomCases[5] as NodeMesh).getBorderingMesh(borderingNodeRoomCases[0].addMesh(borderingNodeRoomCases[1]).addMesh(borderingNodeRoomCases[2]).addMesh(borderingNodeRoomCases[3]).addMesh(borderingNodeRoomCases[4]))
        )

        val textOffsetPosition = Point(0, -50)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            (0..5).forEach { idx ->
                secondContainer.text(text= "NodeMesh Test Case $idx", color = ForeColors[idx % RenderPalette.BackColors.size], alignment = RenderPalette.TextAlignCenter).position(meshListPositions[idx] + textOffsetPosition)

                borderingMesh[idx].linkNearNodes()
                borderingMesh[idx].removeOrphans()

                stroke(BackColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in borderingMesh[idx].getNodeLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                val rooms = borderingMesh[idx].nodes.size / 8
                val maxIter = borderingMesh[idx].nodes.size / 6

                val clusters = borderingMesh[idx].getClusters(rooms = rooms, maxIterations = maxIter)

                val renderPoints = if (clusters.isNotEmpty()) clusters.values.flatten() else borderingMesh[idx].nodes

                for (meshNode in renderPoints) {
                    val numberRegex = Regex("\\d+")

                    val colorIdx = numberRegex.find(meshNode.description, 0)?.value?.toInt() ?: 0

                    secondContainer.circle {
                        position(meshNode.position)
                        radius = 5.0
                        color = ForeColors[colorIdx % ForeColors.size]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                        }
                    }
                }
            }
        }
        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomsSetCentroids(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeRoomsSetCentroids() [v0.4]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing INodeMesh.buildCentroidRoomMesh() with room height = 3")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("rooms are determined by input centroid mesh clustering with setClusters()")
        commandViews[CommandView.PREV_BUTTON]!!.visible = true
        commandViews[CommandView.NEXT_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val centroidMeshPosition = Point(800, 300)

        val centroidMesh = INodeMesh.buildRoomMesh(centroidMeshPosition, height = 2) as NodeMesh

        val centroidTextOffsetPosition = Point (0, -100)

        val nodeRoomMeshPosition = Point(400, 600)

        val nodeRoomMeshCentroids = NodeMesh(nodes = NodeMesh(copyNodeMesh = centroidMesh).nodes.moveNodes(nodeRoomMeshPosition - centroidMeshPosition).scaleNodes(scale = 2.5))

        val nodeRoomHeight = 3

        val nodeRoomMesh = INodeMesh.buildCentroidRoomMesh(height = nodeRoomHeight, centroids = nodeRoomMeshCentroids.nodes)

        val nodeRoomMeshTextOffsetPosition = Point (-300, -300)

        val secondContainer = renderContainer.container()
        val thirdContainer = renderContainer.container()

        thirdContainer.graphics {

            thirdContainer.text(
                text = "Centroid NodeMesh(height=2)", color = ForeColors[0], alignment = RenderPalette.TextAlignCenter
            ).position(centroidMeshPosition + centroidTextOffsetPosition)

            stroke(BackColors[0], StrokeInfo(thickness = 3.0)) {

                for (meshLine in centroidMesh.getNodeLineList()) {
                    if (meshLine != null) line(meshLine.first, meshLine.second)
                }
            }

            for (meshNode in centroidMesh.nodes) {
                thirdContainer.circle {
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
        }

        secondContainer.graphics {

            nodeRoomMeshCentroids.nodes.forEachIndexed { idx, meshNode ->
                secondContainer.circle {
                    position(meshNode.position)
                    radius = 15.0
                    color = ForeColors[idx % ForeColors.size]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                        commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                    }
                }
            }

            secondContainer.text(text = "NodeMesh(height=$nodeRoomHeight, rooms=${centroidMesh.nodes.size})"
                , color = ForeColors[1]
                , alignment = RenderPalette.TextAlignCenter
            ).position(nodeRoomMeshPosition + nodeRoomMeshTextOffsetPosition)

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (meshLine in nodeRoomMesh.getNodeLineList()) {
                    if (meshLine != null) line(meshLine.first, meshLine.second)
                }
            }

            for (meshNode in nodeRoomMesh.nodes) {
                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(meshNode.description, 0)?.value?.toInt() ?: 0

                secondContainer.circle {
                    position(meshNode.position)
                    radius = 5.0
                    color = ForeColors[colorIdx % ForeColors.size]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                        commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                    }
                }
            }
        }
        secondContainer.exploreKeys()

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()
        thirdContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomsOrphanAdoptingDiff(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeRoomsOrphanAdoptingDiff() [v0.4]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing INodeMesh.adoptRoomOrphans() with INodeMesh.buildRoomMesh() generated rooms")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("text console displays orphaned node adoptions between rooms (work in progress)")
        commandViews[CommandView.PREV_BUTTON]!!.visible = true
        commandViews[CommandView.NEXT_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            val textOffsetPosition = Point(0, -100)

            val orphanAdoptOffset = Point(200, 300)

            val centroidMesh = INodeMesh.buildRoomMesh(Point(100, 100), height = 1)

            val roomMeshPosition = Point(300, 300)

            secondContainer.text(text = "Generated NodeMesh Rooms"
                , color = ForeColors[1]
                , alignment = RenderPalette.TextAlignCenter
            ).position(roomMeshPosition + textOffsetPosition)

            secondContainer.text(text = "Orphan Adopt NodeMesh Rooms"
                , color = ForeColors[3]
                , alignment = RenderPalette.TextAlignCenter
            ).position(roomMeshPosition + orphanAdoptOffset + textOffsetPosition)

            val roomMesh = INodeMesh.buildCentroidRoomMesh(
                height = 3,
                centroids = centroidMesh.nodes.moveNodes(roomMeshPosition).scaleNodes(scale = 2.0)
            )

            for (nodeLine in roomMesh.getNodeLineList()) {

                stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

//            println("drawing nodes")
            roomMesh.nodes.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                secondContainer.circle {
                    position(node.position)
                    radius = 5.0
                    color = ForeColors[colorIdx % ForeColors.size]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                        commandViews[CommandView.NODE_POSITION_TEXT].setText(node.position.toString())
                    }
                }
            }

            roomMesh.centroids.forEach { node ->

                //      println(node.description)

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
                        commandViews[CommandView.NODE_POSITION_TEXT].setText(node.position.toString())
                    }
                }
            }

            roomMesh.adoptRoomOrphans()

            for (nodeLine in roomMesh.getNodeLineList()) {

                stroke(BackColors[3], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first + orphanAdoptOffset, nodeLine.second + orphanAdoptOffset )
                }
            }

            roomMesh.nodes.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                secondContainer.circle {
                    position(node.position + orphanAdoptOffset)
                    radius = 5.0
                    color = ForeColors[colorIdx % ForeColors.size]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                        commandViews[CommandView.NODE_POSITION_TEXT].setText(node.position.toString())
                    }
                }
            }

            roomMesh.centroids.forEach { node ->

                //      println(node.description)

                val numberRegex = Regex("\\d+")

                val colorIdx = numberRegex.find(node.description, 0)?.value?.toInt() ?: 0

                secondContainer.circle {
                    position(node.position + orphanAdoptOffset)
                    radius = 10.0
                    color = ForeColors[colorIdx % ForeColors.size]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                        commandViews[CommandView.NODE_POSITION_TEXT].setText(node.position.toString())
                    }
                }
            }
        }
        secondContainer.exploreKeys()

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }
}