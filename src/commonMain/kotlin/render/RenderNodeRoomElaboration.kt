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
        val funSize = 1

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
        commandViews[CommandView.NEXT_BUTTON]!!.visible = false

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
                secondContainer.text(text= "NodeMesh Test Case $idx", color = RenderPalette.ForeColors[idx % RenderPalette.BackColors.size], alignment = RenderPalette.TextAlignCenter).position(nodeRoomCentroids[idx].position + textOffsetPosition)

                println("meshIdx $idx position ${nodeRoomCentroids[idx].position}")

                println("borderingNodeRoomMeshCases size: ${borderingNodeRoomMeshCases[idx].nodes.size}")

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

                println ("refCentroidNode: ${refCentroidNode.position}, refEdgeNode: ${refEdgeNode.position}")

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

                val connectingLine = Pair(nodeRoomCentroids.nearestNodesOrderedAsc(borderingNodeCenter)[0], Node(copyNode = borderingNodeCenter)).buildNodeLinkLine(noise = 50)

                if (idx < 5) nodeRoomCentroids.add(Node(copyNode = borderingNodeCenter))

                allBorderingNodeRoomMeshes.addMesh(NodeMesh(copyNodeMesh = borderingNodeRoomMeshCases[borderingNodeRoomMeshCases.size - 1]))

//                println("allBorderingNodeRoomMeshNodes size: ${allBorderingNodeRoomMeshNodes.size}")

                delay(TimeSpan(500.0))
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

                println ("idx: ${nodeRoomCentroids.indexOf(centroid)}; centroid: $centroid; idx nearest: ${nodeRoomCentroids.indexOf(nearestCloseCentroid)}; nearestCloseCentroid: $nearestCloseCentroid")

                val nearestNodeThisMesh = borderingNodeRoomMeshCases[nodeRoomCentroids.indexOf(centroid)].nodes.nearestNodesOrderedAsc(nearestCloseCentroid)

                println ("sizeof nearestNodeThisMesh: ${nearestNodeThisMesh.size}")

//                    println("nearestNodeThisMesh: $nearestNodeThisMesh; index: ${nodeRoomCentroids.indexOf(centroid)}")

//                  nearestNodeThisMesh.forEach { println ("node: $it; distance: ${it.position.distanceTo(nearestCloseCentroid.position)}") }

                val nearestNodeCloseMesh = borderingNodeRoomMeshCases[nodeRoomCentroids.indexOf(nearestCloseCentroid)].nodes.nearestNodesOrderedAsc(nearestNodeThisMesh[0])

                println ("sizeof nearestNodeCloseMesh: ${nearestNodeCloseMesh.size}")

//                    println("nearestNodeCloseMesh: $nearestNodeCloseMesh; index: ${nodeRoomCentroids.indexOf(nearestCloseCentroid)}")

//                nearestNodeCloseMesh.forEach { println ("node: $it; distance: ${it.position.distanceTo(nearestCloseCentroid.position)}") }

                stroke(RenderPalette.BackColors[idx], StrokeInfo(thickness = 3.0)) {
                        line(nearestNodeThisMesh[0].position, nearestNodeCloseMesh[0].position)
                }

                centroidMesh.nodeLinks.addNodeLink(nodeRoomCentroids, centroid.uuid, nearestCloseCentroid.uuid)
            }

            val avgPositionNodes = allBorderingNodeRoomMeshes.nodes.averagePositionWithinNodes()
            val centroidMeshOffsetPosition = Point(400, -300)
            val centroidTextOffsetPosition = Point (0, -100)

            val scaledCentroidMesh = NodeMesh(nodes = centroidMesh.nodes.scaleNodes(pivot=avgPositionNodes, scale = 0.15).moveNodes(centroidMeshOffsetPosition), nodeLinks = centroidMesh.nodeLinks)

            println("scaledCentroidMesh: $centroidMesh")

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

        val secondContainer = renderContainer.container()
        secondContainer.graphics {
/*
            val startingMap = mapOf(
                90 to Point(450, 600)
                , 330 to Point(300, 400)
                , 210 to Point(600, 400)
            )

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
  */      }

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }
}