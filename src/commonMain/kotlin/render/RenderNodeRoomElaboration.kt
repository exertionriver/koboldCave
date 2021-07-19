package render

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.input.onClick
import node.NodeMesh
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tween.moveBy
import com.soywiz.korge.view.tween.scaleTo
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.line
import leaf.ILeaf.Companion.NextDistancePx
import leaf.Line.Companion.extend
import node.INodeMesh
import node.INodeMesh.Companion.addMesh
import node.INodeMesh.Companion.getBorderingMesh
import node.Node
import node.Node.Companion.addNode
import node.Node.Companion.averagePositionWithinNodes
import node.Node.Companion.getFarthestNode
import node.Node.Companion.moveNodes
import node.Node.Companion.nearestNodesOrderedAsc
import node.Node.Companion.scaleNodes
import node.NodeLink.Companion.addNodeLink
import node.NodeLink.Companion.buildNodeLinkLine

object RenderNodeRoomElaboration {

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomElaboration(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 0
        val funSize = 2

        while ( (funIdx >= 0) && (funIdx < funSize) ) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())
            commandViews[CommandView.NODE_POSITION_TEXT].setText(CommandView.NODE_POSITION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderNodeRoomElaborationBottomUp(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                1 -> if ( renderNodeRoomElaborationTopDown(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
            }
        }

        return if (funIdx > 0) ButtonCommand.NEXT else ButtonCommand.PREV
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomElaborationBottomUp(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeRoomElaborationBottomUp() [v0.5]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing getBorderingMesh() to build rooms in a 'bottom-up' elaboration")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("elaboration places new room mesh at random point around the existing edge")
        commandViews[CommandView.PREV_BUTTON]!!.visible = true
        commandViews[CommandView.NEXT_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val centroidMesh = NodeMesh()

        val meshesCenteringPoint = Point(512, 562)

        val nodeRoomCentroids = mutableListOf(
            Node(position = Point(500, 500))
        )

        val nodeRoomMeshCases = mutableListOf(
            INodeMesh.buildRoomMesh(centerPoint = nodeRoomCentroids[0].position, height = 5)
        )

        val borderingNodeRoomMeshCases = mutableListOf(
            NodeMesh(copyNodeMesh = nodeRoomMeshCases[0] as NodeMesh)
        )

        val allBorderingNodeRoomMeshes = NodeMesh(copyNodeMesh = borderingNodeRoomMeshCases[0])

        val textOffsetPosition = Point(0, -50)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            (0..5).forEach { idx ->
                secondContainer.text(
                    text = "NodeMesh Test Case $idx",
                    color = RenderPalette.ForeColors[idx % RenderPalette.BackColors.size],
                    alignment = RenderPalette.TextAlignCenter
                ).position(nodeRoomCentroids[idx].position + textOffsetPosition)

                //               println("meshIdx $idx position ${nodeRoomCentroids[idx].position}")

                //               println("borderingNodeRoomMeshCases size: ${borderingNodeRoomMeshCases[idx].nodes.size}")

                stroke(RenderPalette.BackColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in borderingNodeRoomMeshCases[idx].getNodeLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                val rooms = borderingNodeRoomMeshCases[idx].nodes.size / 8
                val maxIter = borderingNodeRoomMeshCases[idx].nodes.size / 6

                val clusters = borderingNodeRoomMeshCases[idx].getClusters(rooms = rooms, maxIterations = maxIter)

                val renderPoints =
                    if (clusters.isNotEmpty()) clusters.values.flatten() else borderingNodeRoomMeshCases[idx].nodes

                for (meshNode in renderPoints) {
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
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                        }
                    }
                }

                val refCentroidNode = Node(copyNode = nodeRoomCentroids[idx])

                val refEdgeNode = borderingNodeRoomMeshCases[idx].nodes.getFarthestNode(refCentroidNode)

                //               println ("refCentroidNode: ${refCentroidNode.position}, refEdgeNode: ${refEdgeNode.position}")

                val extendedFarthestPosition =
                    Pair(refCentroidNode.position, refEdgeNode.position).extend(NextDistancePx)

                nodeRoomMeshCases.add(
                    INodeMesh.buildRoomMesh(
                        centerPoint = extendedFarthestPosition.second,
                        height = 5
                    )
                )

                val allPreviousBorderingMeshes = NodeMesh(copyNodeMesh = borderingNodeRoomMeshCases[idx])

                //border all previous meshes
                //for the non-iterative version, see RenderNodeRoom.renderNodeRoomsBordering()
                (idx - 1 downTo 0).forEach { borderMeshIdx ->
                    allPreviousBorderingMeshes.addMesh(NodeMesh(copyNodeMesh = borderingNodeRoomMeshCases[borderMeshIdx]))
                }

                borderingNodeRoomMeshCases.add(
                    NodeMesh(copyNodeMesh = nodeRoomMeshCases[nodeRoomMeshCases.size - 1] as NodeMesh).getBorderingMesh(
                        allPreviousBorderingMeshes
                    ) as NodeMesh
                )

                val borderingNodeCenter =
                    Node(position = borderingNodeRoomMeshCases[borderingNodeRoomMeshCases.size - 1].nodes.averagePositionWithinNodes())

                //               val connectingLine = Pair(nodeRoomCentroids.nearestNodesOrderedAsc(borderingNodeCenter)[0], Node(copyNode = borderingNodeCenter)).buildNodeLinkLine(noise = 50)

                if (idx < 5) nodeRoomCentroids.add(Node(copyNode = borderingNodeCenter))

                allBorderingNodeRoomMeshes.addMesh(NodeMesh(copyNodeMesh = borderingNodeRoomMeshCases[borderingNodeRoomMeshCases.size - 1]))

//                println("allBorderingNodeRoomMeshNodes size: ${allBorderingNodeRoomMeshNodes.size}")

                delay(TimeSpan(200.0))
            }

            //center meshes
            val avgPosition = allBorderingNodeRoomMeshes.nodes.averagePositionWithinNodes()

            println("allNodeMeshes: ${allBorderingNodeRoomMeshes.nodes.size}; avg position: $avgPosition")

            secondContainer.moveBy(meshesCenteringPoint.x - avgPosition.x, meshesCenteringPoint.y - avgPosition.y)

            nodeRoomCentroids.forEach { centroid ->
                centroidMesh.nodes.addNode(Node(copyNode = centroid))
            }

            //build lines between meshes
            nodeRoomCentroids.forEachIndexed { idx, centroid ->
                secondContainer.circle {
                    position(centroid.position)
                    radius = 10.0
                    color = RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(centroid.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(centroid.description)
                        commandViews[CommandView.NODE_POSITION_TEXT].setText(centroid.position.toString())
                    }
                }

                val nearestCloseCentroid = nodeRoomCentroids.minus(centroid).nearestNodesOrderedAsc(centroid)[0]

//                println ("idx: ${nodeRoomCentroids.indexOf(centroid)}; centroid: $centroid; idx nearest: ${nodeRoomCentroids.indexOf(nearestCloseCentroid)}; nearestCloseCentroid: $nearestCloseCentroid")

                val nearestNodeThisMesh =
                    borderingNodeRoomMeshCases[idx].nodes.nearestNodesOrderedAsc(nearestCloseCentroid)

//                println ("sizeof nearestNodeThisMesh: ${nearestNodeThisMesh.size}")

//                    println("nearestNodeThisMesh: $nearestNodeThisMesh; index: ${nodeRoomCentroids.indexOf(centroid)}")

//                  nearestNodeThisMesh.forEach { println ("node: $it; distance: ${it.position.distanceTo(nearestCloseCentroid.position)}") }

                val nearestNodeCloseMesh =
                    borderingNodeRoomMeshCases[nodeRoomCentroids.indexOf(nearestCloseCentroid)].nodes.nearestNodesOrderedAsc(
                        nearestNodeThisMesh[0]
                    )

//                println ("sizeof nearestNodeCloseMesh: ${nearestNodeCloseMesh.size}")

//                    println("nearestNodeCloseMesh: $nearestNodeCloseMesh; index: ${nodeRoomCentroids.indexOf(nearestCloseCentroid)}")

//                nearestNodeCloseMesh.forEach { println ("node: $it; distance: ${it.position.distanceTo(nearestCloseCentroid.position)}") }

                stroke(RenderPalette.BackColors[idx], StrokeInfo(thickness = 3.0)) {
                    line(nearestNodeThisMesh[0].position, nearestNodeCloseMesh[0].position)
                }

                centroidMesh.nodeLinks.addNodeLink(nodeRoomCentroids, centroid.uuid, nearestCloseCentroid.uuid)
            }
        }

        val thirdContainer = renderContainer.container()
        thirdContainer.graphics {

            val centroidMeshPosition3 = Point(200, 300)

            val centroidMeshAvgPosition2 = centroidMesh.nodes.averagePositionWithinNodes()

            val centroidMeshOffset = centroidMeshPosition3 - centroidMeshAvgPosition2

            val avgPositionNodes = allBorderingNodeRoomMeshes.nodes.averagePositionWithinNodes()
            val centroidTextOffsetPosition = Point (0, -100)

            val scaledCentroidMesh = NodeMesh(nodes = centroidMesh.nodes.scaleNodes(pivot=avgPositionNodes, scale = 0.25), nodeLinks = centroidMesh.nodeLinks)

            println("scaledCentroidMesh: $centroidMesh")

            //draw resulting centroid mesh
            thirdContainer.text(text = "Centroid NodeMesh (bottom-up)"
                , color = RenderPalette.ForeColors[0]
                , alignment = RenderPalette.TextAlignCenter
            ).position(centroidMeshPosition3 + centroidTextOffsetPosition)

            stroke(RenderPalette.BackColors[0], StrokeInfo(thickness = 3.0)) {

                for (meshLine in scaledCentroidMesh.getNodeLineList()) {
                    if (meshLine != null) line(meshLine.first + centroidMeshOffset, meshLine.second + centroidMeshOffset)
                }
            }

            for (meshNode in scaledCentroidMesh.nodes) {
                thirdContainer.circle {
                    position(meshNode.position + centroidMeshOffset)
                    radius = 5.0
                    color = RenderPalette.ForeColors[0]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                        commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                    }
                }
            }
/*            secondContainer.circle {
                position(avgPositionNodes)
                radius = 24.0
                color = RenderPalette.ForeColors[1]
                strokeThickness = 3.0
                commandViews[CommandView.NODE_UUID_TEXT].setText("avgPositionNodes")
                commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText("avgPositionNodes")
                commandViews[CommandView.NODE_POSITION_TEXT].setText(avgPositionNodes.toString())
            }
*/        }

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()
        thirdContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomElaborationTopDown(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeRoomElaborationTopDown() [v0.5]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing getBorderingMesh() to build rooms in a 'top-down' elaboration")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("elaboration uses centroid node mesh as a trellis")
        commandViews[CommandView.PREV_BUTTON]!!.visible = true
        commandViews[CommandView.NEXT_BUTTON]!!.visible = false

        RenderPalette.returnClick = null

        val centroidMeshPosition = Point(800, 300)

        val centroidTextOffsetPosition = Point (0, -100)

        val centroidMeshes = mutableListOf (
            INodeMesh.buildRoomMesh(centroidMeshPosition, height = 2)
        )

        println("centroidMesh(${centroidMeshes[0].nodes.size}: ${centroidMeshes[0]}")

        val secondContainer = renderContainer.container()
        val thirdContainer = renderContainer.container()

        thirdContainer.graphics {

            thirdContainer.text(
                text = "Three Centroid Meshes (top-down)",
                color = RenderPalette.ForeColors[0],
                alignment = RenderPalette.TextAlignCenter
            ).position(centroidMeshPosition + centroidTextOffsetPosition)
        }

        // build three nodeMeshes
        (0..2).forEach { idx ->

            val allPreviousBorderingCentroidMeshes = NodeMesh(copyNodeMesh = centroidMeshes[idx] as NodeMesh)

            //border all previous meshes
            (idx - 1 downTo 0).forEach { borderMeshIdx ->
                allPreviousBorderingCentroidMeshes.addMesh(NodeMesh(copyNodeMesh = centroidMeshes[borderMeshIdx] as NodeMesh))
            }

            val refCentroidNode = centroidMeshes[idx].nodes.averagePositionWithinNodes()

            val refEdgeNode = centroidMeshes[idx].nodes.getFarthestNode()

            //               println ("refCentroidNode: ${refCentroidNode.position}, refEdgeNode: ${refEdgeNode.position}")

            val extendedFarthestPosition =
                Pair(refCentroidNode, refEdgeNode.position).extend((NextDistancePx * 0.2).toInt())

            val nextMesh = INodeMesh.buildRoomMesh(centerPoint = extendedFarthestPosition.second, height = 2)
                .getBorderingMesh(allPreviousBorderingCentroidMeshes) as NodeMesh

            centroidMeshes.add(nextMesh)

            println("nextMesh(${nextMesh.nodes.size}: $nextMesh")

            // display centroid mesh

            thirdContainer.graphics {

                centroidMeshes.forEach { centroidMesh ->

                    stroke(RenderPalette.BackColors[0], StrokeInfo(thickness = 3.0)) {

                        for (meshLine in centroidMesh.getNodeLineList()) {
                            if (meshLine != null) line(meshLine.first, meshLine.second)
                        }
                    }

                    for (meshNode in centroidMesh.nodes) {
                        thirdContainer.circle {
                            position(meshNode.position)
                            radius = 5.0
                            color = RenderPalette.ForeColors[0]
                            strokeThickness = 3.0
                            onClick {
                                commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                                commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                                commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                            }
                        }

                        println("centroidMesh(${centroidMesh.nodes.size}: $centroidMesh")
                    }

                    delay(TimeSpan(500.0))
                }
            }

            secondContainer.graphics {

                thirdContainer.text(
                    text = "test123",
                    color = RenderPalette.ForeColors[0],
                    alignment = RenderPalette.TextAlignCenter
                ).position(centroidMeshPosition + centroidTextOffsetPosition)

                centroidMeshes.forEach { centroidMesh ->

                    val scaledCentroidMesh = NodeMesh(
                        nodes = centroidMesh.nodes.scaleNodes(scale = 2.5).moveNodes(Point(-500, 400)),
                        nodeLinks = centroidMesh.nodeLinks
                    )

                    stroke(RenderPalette.BackColors[0], StrokeInfo(thickness = 3.0)) {

                        for (meshLine in scaledCentroidMesh.getNodeLineList()) {
                            if (meshLine != null) line(meshLine.first, meshLine.second)
                        }
                    }

                    for (meshNode in scaledCentroidMesh.nodes) {
                        secondContainer.circle {
                            position(meshNode.position)
                            radius = 5.0
                            color = RenderPalette.ForeColors[0]
                            strokeThickness = 3.0
                            onClick {
                                commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                                commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                                commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                            }
                        }

                        println("scaledCentroidMesh(${scaledCentroidMesh.nodes.size}: $centroidMesh")
                    }

                    delay(TimeSpan(500.0))
                }
            }
        }
   /*
            val meshesCenteringPoint = Point(512, 562)

        val nodeRoomCentroids = mutableListOf(
            Node(position = Point(500, 500))
        )

        val nodeRoomMeshCases = mutableListOf(
            INodeMesh.buildRoomMesh(centerPoint = nodeRoomCentroids[0].position, height = 5)
        )

        val borderingNodeRoomMeshCases = mutableListOf(
            NodeMesh(copyNodeMesh = nodeRoomMeshCases[0] as NodeMesh)
        )

        val allBorderingNodeRoomMeshes = NodeMesh(copyNodeMesh = borderingNodeRoomMeshCases[0])

        val textOffsetPosition = Point(0, -50)


            (0..5).forEach { idx ->
                secondContainer.text(text= "NodeMesh Test Case $idx", color = RenderPalette.ForeColors[idx % RenderPalette.BackColors.size], alignment = RenderPalette.TextAlignCenter).position(nodeRoomCentroids[idx].position + textOffsetPosition)

                //               println("meshIdx $idx position ${nodeRoomCentroids[idx].position}")

                //               println("borderingNodeRoomMeshCases size: ${borderingNodeRoomMeshCases[idx].nodes.size}")

                stroke(RenderPalette.BackColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in borderingNodeRoomMeshCases[idx].getNodeLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                val rooms = borderingNodeRoomMeshCases[idx].nodes.size / 8
                val maxIter = borderingNodeRoomMeshCases[idx].nodes.size / 6

                val clusters = borderingNodeRoomMeshCases[idx].getClusters(rooms = rooms, maxIterations = maxIter)

                val renderPoints = if (clusters.isNotEmpty()) clusters.values.flatten() else borderingNodeRoomMeshCases[idx].nodes

                for (meshNode in renderPoints) {
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
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                        }
                    }
                }

                val refCentroidNode = Node(copyNode = nodeRoomCentroids[idx])

                val refEdgeNode = borderingNodeRoomMeshCases[idx].nodes.getFarthestNode(refCentroidNode)

                //               println ("refCentroidNode: ${refCentroidNode.position}, refEdgeNode: ${refEdgeNode.position}")

                val extendedFarthestPosition = Pair(refCentroidNode.position, refEdgeNode.position).extend(NextDistancePx)

                nodeRoomMeshCases.add(INodeMesh.buildRoomMesh(centerPoint = extendedFarthestPosition.second, height = 5))

                val allPreviousBorderingMeshes = NodeMesh(copyNodeMesh = borderingNodeRoomMeshCases[idx])

                //border all previous meshes
                //for the non-iterative version, see RenderNodeRoom.renderNodeRoomsBordering()
                (idx - 1 downTo 0).forEach { borderMeshIdx ->
                    allPreviousBorderingMeshes.addMesh(NodeMesh(copyNodeMesh = borderingNodeRoomMeshCases[borderMeshIdx]))
                }

                borderingNodeRoomMeshCases.add(NodeMesh(copyNodeMesh = nodeRoomMeshCases[nodeRoomMeshCases.size - 1] as NodeMesh).getBorderingMesh(allPreviousBorderingMeshes) as NodeMesh)

                val borderingNodeCenter = Node(position = borderingNodeRoomMeshCases[borderingNodeRoomMeshCases.size - 1].nodes.averagePositionWithinNodes() )

                //               val connectingLine = Pair(nodeRoomCentroids.nearestNodesOrderedAsc(borderingNodeCenter)[0], Node(copyNode = borderingNodeCenter)).buildNodeLinkLine(noise = 50)

                if (idx < 5) nodeRoomCentroids.add(Node(copyNode = borderingNodeCenter))

                allBorderingNodeRoomMeshes.addMesh(NodeMesh(copyNodeMesh = borderingNodeRoomMeshCases[borderingNodeRoomMeshCases.size - 1]))

//                println("allBorderingNodeRoomMeshNodes size: ${allBorderingNodeRoomMeshNodes.size}")

                delay(TimeSpan(200.0))
            }

            //center meshes
            val avgPosition = allBorderingNodeRoomMeshes.nodes.averagePositionWithinNodes()

            println("allNodeMeshes: ${allBorderingNodeRoomMeshes.nodes.size}; avg position: $avgPosition")

            secondContainer.moveBy(meshesCenteringPoint.x - avgPosition.x, meshesCenteringPoint.y - avgPosition.y )

            nodeRoomCentroids.forEach { centroid ->
                centroidMesh.nodes.addNode(Node(copyNode = centroid))
            }

            //build lines between meshes
            nodeRoomCentroids.forEachIndexed { idx, centroid ->
                secondContainer.circle {
                    position(centroid.position)
                    radius = 10.0
                    color = RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(centroid.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(centroid.description)
                        commandViews[CommandView.NODE_POSITION_TEXT].setText(centroid.position.toString())
                    }
                }

                val nearestCloseCentroid = nodeRoomCentroids.minus(centroid).nearestNodesOrderedAsc(centroid)[0]

//                println ("idx: ${nodeRoomCentroids.indexOf(centroid)}; centroid: $centroid; idx nearest: ${nodeRoomCentroids.indexOf(nearestCloseCentroid)}; nearestCloseCentroid: $nearestCloseCentroid")

                val nearestNodeThisMesh = borderingNodeRoomMeshCases[idx].nodes.nearestNodesOrderedAsc(nearestCloseCentroid)

//                println ("sizeof nearestNodeThisMesh: ${nearestNodeThisMesh.size}")

//                    println("nearestNodeThisMesh: $nearestNodeThisMesh; index: ${nodeRoomCentroids.indexOf(centroid)}")

//                  nearestNodeThisMesh.forEach { println ("node: $it; distance: ${it.position.distanceTo(nearestCloseCentroid.position)}") }

                val nearestNodeCloseMesh = borderingNodeRoomMeshCases[nodeRoomCentroids.indexOf(nearestCloseCentroid)].nodes.nearestNodesOrderedAsc(nearestNodeThisMesh[0])

//                println ("sizeof nearestNodeCloseMesh: ${nearestNodeCloseMesh.size}")

//                    println("nearestNodeCloseMesh: $nearestNodeCloseMesh; index: ${nodeRoomCentroids.indexOf(nearestCloseCentroid)}")

//                nearestNodeCloseMesh.forEach { println ("node: $it; distance: ${it.position.distanceTo(nearestCloseCentroid.position)}") }

                stroke(RenderPalette.BackColors[idx], StrokeInfo(thickness = 3.0)) {
                    line(nearestNodeThisMesh[0].position, nearestNodeCloseMesh[0].position)
                }

                centroidMesh.nodeLinks.addNodeLink(nodeRoomCentroids, centroid.uuid, nearestCloseCentroid.uuid)
            }

            val avgPositionNodes = allBorderingNodeRoomMeshes.nodes.averagePositionWithinNodes()
            val centroidMeshOffsetPosition = Point(400, -350)
            val centroidTextOffsetPosition = Point (0, -100)

            val scaledCentroidMesh = NodeMesh(nodes = centroidMesh.nodes.scaleNodes(pivot=avgPositionNodes, scale = 0.15).moveNodes(centroidMeshOffsetPosition), nodeLinks = centroidMesh.nodeLinks)

            //           println("scaledCentroidMesh: $centroidMesh")

            //draw resulting centroid mesh
            secondContainer.text(text = "Centroid NodeMesh (bottom-up)"
                , color = RenderPalette.ForeColors[0]
                , alignment = RenderPalette.TextAlignCenter
            ).position(scaledCentroidMesh.nodes.averagePositionWithinNodes() + centroidTextOffsetPosition)

            stroke(RenderPalette.BackColors[0], StrokeInfo(thickness = 3.0)) {

                for (meshLine in scaledCentroidMesh.getNodeLineList()) {
                    if (meshLine != null) line(meshLine.first, meshLine.second)
                }
            }

            for (meshNode in scaledCentroidMesh.nodes) {
                secondContainer.circle {
                    position(meshNode.position)
                    radius = 5.0
                    color = RenderPalette.ForeColors[0]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                        commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                    }
                }
            }
            secondContainer.circle {
                position(avgPositionNodes)
                radius = 24.0
                color = RenderPalette.ForeColors[1]
                strokeThickness = 3.0
                commandViews[CommandView.NODE_UUID_TEXT].setText("avgPositionNodes")
                commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText("avgPositionNodes")
                commandViews[CommandView.NODE_POSITION_TEXT].setText(avgPositionNodes.toString())
            }
*/

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()
        thirdContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }
}