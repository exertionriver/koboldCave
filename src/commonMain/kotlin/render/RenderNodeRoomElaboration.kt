package render

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.input.onClick
import node.NodeMesh
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tween.moveBy
import com.soywiz.korge.view.tween.moveTo
import com.soywiz.korge.view.tween.scaleTo
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.line
import exploreKeys
import leaf.ILeaf.Companion.NextDistancePx
import leaf.Line.Companion.extend
import node.INodeMesh
import node.INodeMesh.Companion.addMesh
import node.INodeMesh.Companion.getBorderingMesh
import node.INodeMesh.Companion.nodes
import node.Node
import node.Node.Companion.addNode
import node.Node.Companion.averagePositionWithinNodes
import node.Node.Companion.getFarthestNode
import node.Node.Companion.moveNodes
import node.Node.Companion.nearestNodesOrderedAsc
import node.Node.Companion.scaleNodes
import node.NodeLink
import node.NodeLink.Companion.addNodeLink
import node.NodeLink.Companion.buildNodeLinkLine

object RenderNodeRoomElaboration {

    @ExperimentalUnsignedTypes
    suspend fun renderNodeRoomElaboration(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 1
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

//            println("allNodeMeshes: ${allBorderingNodeRoomMeshes.nodes.size}; avg position: $avgPosition")

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

                //from this centroid, find the nearest close centroid
                val nearestCloseCentroid = nodeRoomCentroids.minus(centroid).nearestNodesOrderedAsc(centroid)[0]

//                println ("idx: ${nodeRoomCentroids.indexOf(centroid)}; centroid: $centroid; idx nearest: ${nodeRoomCentroids.indexOf(nearestCloseCentroid)}; nearestCloseCentroid: $nearestCloseCentroid")

                //from this nodemesh, find the node nearest to the nearest close centroid
                val nearestNodeThisMesh = borderingNodeRoomMeshCases[idx].nodes.nearestNodesOrderedAsc(nearestCloseCentroid)

//                println ("sizeof nearestNodeThisMesh: ${nearestNodeThisMesh.size}")

//                    println("nearestNodeThisMesh: $nearestNodeThisMesh; index: ${nodeRoomCentroids.indexOf(centroid)}")

//                  nearestNodeThisMesh.forEach { println ("node: $it; distance: ${it.position.distanceTo(nearestCloseCentroid.position)}") }

                //from the nearest-centroid-nodemesh, find the node nearest to this centroid's node-nearest
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

//            println("scaledCentroidMesh: $centroidMesh")

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
        secondContainer.exploreKeys()

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

        val centroidMeshPosition = Point(600, 200)

        val meshPosition = Point(512, 512)

        val centroidMeshOffset = meshPosition - centroidMeshPosition

        val centroidTextOffsetPosition = Point (0, -100)

        val centroidMeshes = mutableListOf<NodeMesh>()
        val centroidMeshCentroids = mutableListOf<Node>()

        val scaledMeshes = mutableListOf<NodeMesh>()
        val scaledCentroids = mutableListOf<Node>()

        val secondContainer = renderContainer.container()
        val thirdContainer = renderContainer.container()

        //display centroid-of-meshes
        secondContainer.circle {
            position(meshPosition)
            radius = 15.0
            color = RenderPalette.ForeColors[1]
            strokeThickness = 3.0
            onClick {
                commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText("meshPosition")
                commandViews[CommandView.NODE_POSITION_TEXT].setText(meshPosition.toString())
            }
        }

        //display centroid-of-centroid-mesh and label
        thirdContainer.graphics {

            thirdContainer.text(
                text = "Centroid Meshes (top-down)",
                color = RenderPalette.ForeColors[0],
                alignment = RenderPalette.TextAlignCenter
            ).position(centroidMeshPosition + centroidTextOffsetPosition)

            thirdContainer.circle {
                position(centroidMeshPosition)
                radius = 15.0
                color = RenderPalette.ForeColors[0]
                strokeThickness = 3.0
                onClick {
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText("centroidPosition")
                    commandViews[CommandView.NODE_POSITION_TEXT].setText(centroidMeshPosition.toString())
                }
            }
        }

        val allMeshNodes = mutableListOf<Node>()
        val allMeshNodeLinks = mutableListOf<NodeLink>()
        val allPreviousBorderingCentroidMeshes = NodeMesh()

        val meshesToBuild = 3
        val meshesHeight = 2

        // create bordering centroid mesh and then create scaled, previously bordering nodeMesh at some offset position
        (0 until meshesToBuild).forEach { idx ->

            var linkToMesh = -1

            if (idx == 0) {
                //generate first centroid nodeMesh
                centroidMeshes.add( INodeMesh.buildRoomMesh(centerPoint = centroidMeshPosition, height = meshesHeight) as NodeMesh )
                centroidMeshCentroids.add(Node(position = centroidMeshPosition) )

                allPreviousBorderingCentroidMeshes.addMesh(NodeMesh(copyNodeMesh = centroidMeshes[idx]))
            }
            else {
                //generate subsequent bordering nodeMesh
                val refCentroidNode = centroidMeshCentroids[idx - 1]
                val refEdgeNode = centroidMeshes[idx - 1].nodes.getFarthestNode()

                val extendedFarthestPosition = Pair(refCentroidNode.position, refEdgeNode.position).extend((NextDistancePx * 0.2).toInt())

                //add centroidMesh at idx
                centroidMeshes.add( INodeMesh.buildRoomMesh(centerPoint = extendedFarthestPosition.second, height = 2)
                    .getBorderingMesh(allPreviousBorderingCentroidMeshes) as NodeMesh )

                centroidMeshCentroids.add( Node(position = centroidMeshes[idx].nodes.averagePositionWithinNodes()) )

                allPreviousBorderingCentroidMeshes.addMesh(NodeMesh(copyNodeMesh = centroidMeshes[idx]))

                //link new mesh to previous mesh
                //from this (next) centroid, find the nearest close centroid
                val nearestCloseCentroid = centroidMeshCentroids.minus(centroidMeshCentroids[idx]).nearestNodesOrderedAsc(centroidMeshCentroids[idx])[0]

//                println ("idx: $idx; centroid: ${centroidMeshCentroids[idx]}; closestIdx: ${centroidMeshCentroids.indexOf(nearestCloseCentroid)}; nearestCloseCentroid: $nearestCloseCentroid")
                println ("thisIdx: $idx; closestIdx: ${centroidMeshCentroids.indexOf(nearestCloseCentroid)}")

                //from this nodemesh, find the node nearest to the nearest close centroid
                val nearestNodeThisMesh = centroidMeshes[idx].nodes.nearestNodesOrderedAsc(nearestCloseCentroid)

                println ("sizeof nearestNodeThisMesh: ${nearestNodeThisMesh.size}")

//                println("nearestNodeThisMesh: $nearestNodeThisMesh; index: ${centroidMeshCentroids.indexOf(nearestCloseCentroid)}")

//                    nearestNodeThisMesh.forEach { println ("node: $it; distance: ${it.position.distanceTo(nearestCloseCentroid.position)}") }

                //from the nearest-centroid-nodemesh, find the node nearest to this centroid's node-nearest
                linkToMesh = centroidMeshCentroids.indexOf(nearestCloseCentroid)

                val nearestNodeCloseMesh = centroidMeshes[linkToMesh].nodes.nearestNodesOrderedAsc(nearestNodeThisMesh[0])

                println ("sizeof nearestNodeCloseMesh: ${nearestNodeCloseMesh.size}")

//                    println("nearestNodeCloseMesh: $nearestNodeCloseMesh; index: ${centroidMeshCentroids.indexOf(nearestCloseCentroid)}")

//                nearestNodeCloseMesh.forEach { println ("node: $it; distance: ${it.position.distanceTo(nearestCloseCentroid.position)}") }

                thirdContainer.graphics {
                    stroke(RenderPalette.BackColors[0], StrokeInfo(thickness = 3.0)) {
                        line(nearestNodeThisMesh[0].position, nearestNodeCloseMesh[0].position)
                    }
                }

                centroidMeshes[idx].nodeLinks.addNodeLink(allPreviousBorderingCentroidMeshes.nodes, nearestNodeThisMesh[0].uuid, nearestNodeCloseMesh[0].uuid)
                allPreviousBorderingCentroidMeshes.nodeLinks.addNodeLink(allPreviousBorderingCentroidMeshes.nodes, nearestNodeThisMesh[0].uuid, nearestNodeCloseMesh[0].uuid)
            }


            //build rooms off nodeMesh centroids
            //create nodeMesh centroids
            val scaledCentroidMesh = NodeMesh(
                nodes = centroidMeshes[idx].nodes.moveNodes(centroidMeshOffset)
                    .scaleNodes(pivot = meshPosition, scale = 2.0),
                nodeLinks = centroidMeshes[idx].nodeLinks
            )

            val borderingMesh = NodeMesh(nodes = allMeshNodes, nodeLinks = allMeshNodeLinks)

            val nodeRoomMesh = if (idx == 0) INodeMesh.buildCentroidRoomMesh(height = 2, centroids = scaledCentroidMesh.nodes)
                else INodeMesh.buildCentroidRoomMesh(height = 2, centroids = scaledCentroidMesh.nodes)
                .getBorderingMesh(NodeMesh(copyNodeMesh = borderingMesh))

            scaledMeshes.add(nodeRoomMesh as NodeMesh)
            scaledCentroids.add(Node(position = nodeRoomMesh.nodes.averagePositionWithinNodes()))
            //add this bordering nodeMesh to all nodeMeshes used
            allMeshNodes.addAll(nodeRoomMesh.nodes)
            allMeshNodeLinks.addAll(nodeRoomMesh.nodeLinks)


            // display centroid mesh
            thirdContainer.graphics {

                stroke(RenderPalette.BackColors[0], StrokeInfo(thickness = 3.0)) {

                    for (meshLine in centroidMeshes[idx].getNodeLineList()) {
                        if (meshLine != null) line(meshLine.first, meshLine.second)
                    }
                }

                for (meshNode in centroidMeshes[idx].nodes) {
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

//                    println("centroidMesh(${centroidMeshes[idx].nodes.size}: ${centroidMeshes[idx]}")
                }
            }

            //create and display scaled, offset, previously-bordering nodeMesh
            secondContainer.graphics {

                secondContainer.text(
                    text = "NodeMesh Test Case $idx",
                    color = RenderPalette.ForeColors[idx + 1],
                    alignment = RenderPalette.TextAlignCenter
                ).position(scaledCentroidMesh.nodes.averagePositionWithinNodes() + centroidTextOffsetPosition)

                //render nodeMesh rooms based upon scaledMesh centroids
                stroke(RenderPalette.BackColors[idx + 1], StrokeInfo(thickness = 3.0)) {

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
                        color = RenderPalette.ForeColors[colorIdx % RenderPalette.ForeColors.size]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                        }
                    }
                }

                // display scaled nodeMesh centroids last
                for (meshNode in scaledCentroidMesh.nodes) {
                    secondContainer.circle {
                        position(meshNode.position)
                        radius = 10.0
                        color = RenderPalette.ForeColors[idx + 1]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(meshNode.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(meshNode.description)
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(meshNode.position.toString())
                        }
                    }
//                      println("scaledCentroidMesh(${scaledCentroidMesh.nodes.size}: ${centroidMeshes[idx]}")
                }
            }

            if (idx > 0) {
                //propagate mesh link to scaled nodeMesh

                //from this (next) centroid, find the nearest close centroid
    //            val nearestCloseCentroid = scaledCentroids.minus(scaledCentroids[idx]).nearestNodesOrderedAsc(scaledCentroids[idx])[0]

    //                println ("idx: $idx; centroid: ${centroidMeshCentroids[idx]}; closestIdx: ${centroidMeshCentroids.indexOf(nearestCloseCentroid)}; nearestCloseCentroid: $nearestCloseCentroid")
     //           println ("thisIdx: $idx; closestIdx: ${scaledCentroids.indexOf(nearestCloseCentroid)}")

                //from this nodemesh, find the node nearest to the nearest close centroid
                val nearestNodeThisMesh = scaledMeshes[idx].nodes.nearestNodesOrderedAsc(scaledCentroids[linkToMesh])

                println ("sizeof nearestNodeThisMesh: ${nearestNodeThisMesh.size}")

    //                println("nearestNodeThisMesh: $nearestNodeThisMesh; index: ${centroidMeshCentroids.indexOf(nearestCloseCentroid)}")

    //                    nearestNodeThisMesh.forEach { println ("node: $it; distance: ${it.position.distanceTo(nearestCloseCentroid.position)}") }

                //from the nearest-centroid-nodemesh, find the node nearest to this centroid's node-nearest
                val nearestNodeCloseMesh = scaledMeshes[linkToMesh].nodes.nearestNodesOrderedAsc(nearestNodeThisMesh[0])

                println ("sizeof nearestNodeCloseMesh: ${nearestNodeCloseMesh.size}")

    //                    println("nearestNodeCloseMesh: $nearestNodeCloseMesh; index: ${centroidMeshCentroids.indexOf(nearestCloseCentroid)}")

    //                nearestNodeCloseMesh.forEach { println ("node: $it; distance: ${it.position.distanceTo(nearestCloseCentroid.position)}") }

                secondContainer.graphics {
                    stroke(RenderPalette.BackColors[idx], StrokeInfo(thickness = 3.0)) {
                        line(nearestNodeThisMesh[0].position, nearestNodeCloseMesh[0].position)
                    }
                }

                scaledMeshes[idx].nodeLinks.addNodeLink(allPreviousBorderingCentroidMeshes.nodes, nearestNodeThisMesh[0].uuid, nearestNodeCloseMesh[0].uuid)
                allPreviousBorderingCentroidMeshes.nodeLinks.addNodeLink(allPreviousBorderingCentroidMeshes.nodes, nearestNodeThisMesh[0].uuid, nearestNodeCloseMesh[0].uuid)
            }

            //pause for iteration
            delay(TimeSpan(300.0))
        }

        secondContainer.exploreKeys()

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()
        thirdContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }
}