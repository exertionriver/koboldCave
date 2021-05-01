package render

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import node.INodeMesh.Companion.absorbMesh
import node.Node
import node.Node.Companion.averagePositionWithinNodes
import node.Node.Companion.getFarthestNode
import node.Node.Companion.nearestNodesOrderedAsc
import node.Node.Companion.randomPosition
import node.NodeLink.Companion.consolidateNodeDistance
import node.NodeLink.Companion.getRandomNodeLink
import node.NodeLink.Companion.linkNodeDistance
import node.NodeMesh
import render.RenderPalette.BackColors
import render.RenderPalette.ForeColors
import render.RenderPalette.TextAlignCenter
import render.RenderPalette.TextAlignLeft
import render.RenderPalette.TextAlignRight
import render.RenderPalette.TextSize

object RenderNodeMesh {

    @ExperimentalUnsignedTypes
    suspend fun renderNodeMesh(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 4
        val funSize = 5

        while ( (funIdx >= 0) && (funIdx < funSize) ) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderNodeMeshOperations(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                1 -> if ( renderNodeMeshOperationsExtended(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                2 -> if ( renderNodeMeshAbsorbing(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                3 -> if ( renderNodeMeshOrphans(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                4 -> if ( renderNodeMeshEdges(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
            //  future directions:
//                5 -> if ( renderGraftedNodeMeshes(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
//                6 -> if ( renderLeafGraftedNodeMesh(renderContainer, commandViews) == ButtonCommand.NEXT) funIdx++ else funIdx--
            }
        }

        return if (funIdx > 0) ButtonCommand.NEXT else ButtonCommand.PREV
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeMeshOperations(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        val leafHeight = 3

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeMeshOperations() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("showing operations upon a NodeMesh generated from three Leaf(height=$leafHeight)")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        commandViews[CommandView.PREV_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val startingPoint = Point(212, 374)

        val textOffset = Point(0, -30)
        val centerMeshOffset = Point(250, 200)

        val xNodeOffset = Point(550.0, 0.0)
        val yNodeOffset = Point(0.0, 400.0)

        val firstRefPoint = Point(350, 200)
        val secondRefPoint = Point (350 + linkNodeDistance, 200)

        val thirdRefPoint = Point(350, 250)
        val fourthRefPoint = Point (350 + consolidateNodeDistance, 250)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            val leafFirst = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()

            secondContainer.text(text = "linkNodeDistance", color = ForeColors[0], textSize = TextSize, alignment = TextAlignLeft).position(firstRefPoint + Point(-10, -25))
            secondContainer.text(text = "consolidateNodeDistance", color = ForeColors[0], textSize = TextSize, alignment = TextAlignLeft).position(thirdRefPoint + Point(-10, -25))

            stroke(BackColors[0], StrokeInfo(thickness = 3.0)) {

                line(firstRefPoint, secondRefPoint)
                line(thirdRefPoint, fourthRefPoint)
            }

            stroke(ForeColors[0], StrokeInfo(thickness = 3.0)) {

                circle(firstRefPoint, radius = 5.0)
                circle(secondRefPoint, radius = 5.0)
                circle(thirdRefPoint, radius = 5.0)
                circle(fourthRefPoint, radius = 5.0)
            }

            secondContainer.text(text = "NodeMesh() from threeLeaf", color = ForeColors[1], textSize = TextSize, alignment = TextAlignCenter).position(startingPoint + centerMeshOffset + textOffset)

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + centerMeshOffset, nodeLine.second + centerMeshOffset )
                }
            }

            for (node in nodeMesh.nodes) {
                secondContainer.circle {
                    position(node.position + centerMeshOffset)
                    radius = 5.0
                    color = ForeColors[1]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            val consolidateNodeMesh = NodeMesh(nodeMesh)

            consolidateNodeMesh.consolidateNearNodes()

            secondContainer.text(text = "INodeMesh.consolidateNearNodes()", color = ForeColors[2], textSize = TextSize, alignment = TextAlignCenter).position(startingPoint + textOffset)

            stroke(BackColors[2], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in consolidateNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            for (node in consolidateNodeMesh.nodes ) {
                secondContainer.circle {
                    position(node.position)
                    radius = 5.0
                    color = ForeColors[2]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            val linkedNodeMesh = NodeMesh(nodeMesh)

            linkedNodeMesh.linkNearNodes()

            secondContainer.text(text = "INodeMesh.linkNearNodes()", color = ForeColors[3], textSize = TextSize, alignment = TextAlignCenter).position(startingPoint + xNodeOffset + textOffset)

            stroke(BackColors[3], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in linkedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first+ xNodeOffset, nodeLine.second + xNodeOffset )
                }
            }

            for (node in linkedNodeMesh.nodes) {
                secondContainer.circle {
                    position(node.position + xNodeOffset)
                    radius = 5.0
                    color = ForeColors[3]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            val consolidatedLinkedNodeMesh = NodeMesh(nodeMesh)

            consolidatedLinkedNodeMesh.consolidateNearNodes()

            consolidatedLinkedNodeMesh.linkNearNodes()

            secondContainer.text(text = "INodeMesh consolidated + linked", color = ForeColors[4], textSize = TextSize, alignment = TextAlignCenter).position(startingPoint + yNodeOffset + textOffset)

            stroke(BackColors[4], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in consolidatedLinkedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + yNodeOffset, nodeLine.second + yNodeOffset )
                }
            }

            for (node in consolidatedLinkedNodeMesh.nodes) {
                secondContainer.circle {
                    position(node.position + yNodeOffset)
                    radius = 5.0
                    color = ForeColors[4]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            val linkedConsolidatedNodeMesh = NodeMesh(nodeMesh)

            linkedConsolidatedNodeMesh.linkNearNodes()

            linkedConsolidatedNodeMesh.consolidateNearNodes()

            secondContainer.text(text = "INodeMesh linked + consolidated", color = ForeColors[5], textSize = TextSize, alignment = TextAlignCenter).position(startingPoint + xNodeOffset + yNodeOffset + textOffset)

            stroke(BackColors[5], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in linkedConsolidatedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + xNodeOffset + yNodeOffset, nodeLine.second + xNodeOffset + yNodeOffset )
                }
            }

            for (node in linkedConsolidatedNodeMesh.nodes) {
                secondContainer.circle {
                    position(node.position + xNodeOffset + yNodeOffset)
                    radius = 5.0
                    color = ForeColors[5]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
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

    @ExperimentalUnsignedTypes
    suspend fun renderNodeMeshOperationsExtended(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        val leafHeight = 3

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeMeshOperationsExtended() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("showing extended operations upon a NodeMesh generated from three Leaf(height=$leafHeight)")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        commandViews[CommandView.PREV_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val startingPoint = Point(212, 374)

        val textOffset = Point(0, -30)
        val centerMeshOffset = Point(250, 200)

        val xNodeOffset = Point(550.0, 0.0)
        val yNodeOffset = Point(0.0, 400.0)

        val firstRefPoint = Point(350, 200)
        val secondRefPoint = Point (350 + linkNodeDistance, 200)

        val thirdRefPoint = Point(350, 250)
        val fourthRefPoint = Point (350 + consolidateNodeDistance, 250)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            val leafFirst = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()

            secondContainer.text(text = "linkNodeDistance", color = ForeColors[0], textSize = TextSize, alignment = TextAlignLeft).position(firstRefPoint + Point(-10, -25))
            secondContainer.text(text = "consolidateNodeDistance", color = ForeColors[0], textSize = TextSize, alignment = TextAlignLeft).position(thirdRefPoint + Point(-10, -25))

            stroke(BackColors[0], StrokeInfo(thickness = 3.0)) {

                line(firstRefPoint, secondRefPoint)
                line(thirdRefPoint, fourthRefPoint)
            }

            stroke(ForeColors[0], StrokeInfo(thickness = 3.0)) {

                circle(firstRefPoint, radius = 5.0)
                circle(secondRefPoint, radius = 5.0)
                circle(thirdRefPoint, radius = 5.0)
                circle(fourthRefPoint, radius = 5.0)
            }

            val consolidatedLinkedNodeMesh = NodeMesh(nodeMesh)

            consolidatedLinkedNodeMesh.consolidateNearNodes()

            consolidatedLinkedNodeMesh.linkNearNodes()

            secondContainer.text(text = "INodeMesh consolidated + linked", color = ForeColors[1], textSize = TextSize, alignment = TextAlignCenter).position(startingPoint + centerMeshOffset + textOffset)

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in consolidatedLinkedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + centerMeshOffset, nodeLine.second + centerMeshOffset )
                }
            }

            for (node in consolidatedLinkedNodeMesh.nodes) {
                secondContainer.circle {
                    position(node.position + centerMeshOffset)
                    radius = 5.0
                    color = ForeColors[1]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            val clcProcessedNodeMesh = NodeMesh(nodeMesh)

            clcProcessedNodeMesh.consolidateNearNodes()

            clcProcessedNodeMesh.linkNearNodes()

            clcProcessedNodeMesh.consolidateNodeLinks()

            secondContainer.text(text = "INodeMesh cons + link + consLinks", color = ForeColors[2], textSize = TextSize, alignment = TextAlignCenter).position(startingPoint + textOffset)

            stroke(BackColors[2], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in clcProcessedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            for (node in clcProcessedNodeMesh.nodes ) {
                secondContainer.circle {
                    position(node.position )
                    radius = 5.0
                    color = ForeColors[2]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            val clpProcessedNodeMesh = NodeMesh(nodeMesh)

            clpProcessedNodeMesh.consolidateNearNodes()

            clpProcessedNodeMesh.linkNearNodes()

            clpProcessedNodeMesh.pruneNodeLinks()

            secondContainer.text(text = "INodeMesh cons + link + prunedLinks", color = ForeColors[3], textSize = TextSize, alignment = TextAlignCenter).position(startingPoint + xNodeOffset + textOffset)

            stroke(BackColors[3], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in clpProcessedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first+ xNodeOffset, nodeLine.second + xNodeOffset )
                }
            }

            for (node in clpProcessedNodeMesh.nodes) {
                secondContainer.circle {
                    position(node.position + xNodeOffset)
                    radius = 5.0
                    color = ForeColors[3]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            val clpcProcessedNodeMesh = NodeMesh(nodeMesh)

            clpcProcessedNodeMesh.consolidateNearNodes()

            clpcProcessedNodeMesh.linkNearNodes()

            clpcProcessedNodeMesh.pruneNodeLinks()

            clpcProcessedNodeMesh.consolidateNodeLinks()

            secondContainer.text(text = "INodeMesh cons + link + prunedLinks + consLinks", color = ForeColors[4], textSize = TextSize, alignment = TextAlignCenter).position(startingPoint + yNodeOffset + textOffset)

            stroke(BackColors[4], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in clpcProcessedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + yNodeOffset, nodeLine.second + yNodeOffset )
                }
            }

            for (node in clpcProcessedNodeMesh.nodes) {
                secondContainer.circle {
                    position(node.position + yNodeOffset)
                    radius = 5.0
                    color = ForeColors[4]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            val clcpProcessedNodeMesh = NodeMesh(nodeMesh)

            clcpProcessedNodeMesh.consolidateNearNodes()

            clcpProcessedNodeMesh.linkNearNodes()

            clcpProcessedNodeMesh.consolidateNodeLinks()

            clcpProcessedNodeMesh.pruneNodeLinks()

            secondContainer.text(text = "INodeMesh cons + link + consLinks + prunedLinks", color = ForeColors[5], textSize = TextSize, alignment = TextAlignCenter).position(startingPoint + xNodeOffset + yNodeOffset + textOffset)

            stroke(ForeColors[5], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in clcpProcessedNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + xNodeOffset + yNodeOffset, nodeLine.second + xNodeOffset + yNodeOffset )
                }
            }

            for (node in clcpProcessedNodeMesh.nodes) {
                secondContainer.circle {
                    position(node.position + xNodeOffset + yNodeOffset)
                    radius = 5.0
                    color = ForeColors[5]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
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


    @ExperimentalUnsignedTypes
    suspend fun renderNodeMeshAbsorbing(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        val leafHeight = 5

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeMeshAbsorbing() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("demonstration of absorbMesh() upon a NodeMesh generated from three Leaf(height=$leafHeight)")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        commandViews[CommandView.PREV_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val startingPoint = Point(462, 574)

        val textOffset = Point(0, -30)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            val leafFirst = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()

            nodeMesh.consolidateStackedNodes()
            nodeMesh.consolidateNearNodes()
            nodeMesh.linkNearNodes()
            nodeMesh.pruneNodeLinks()
            nodeMesh.consolidateNodeLinks()

            secondContainer.text(text = "NodeMesh() from threeLeaf", color = ForeColors[1], textSize = TextSize, alignment = TextAlignCenter).position(startingPoint + textOffset)

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            for (node in nodeMesh.nodes) {
                secondContainer.circle {
                    position(node.position)
                    radius = 5.0
                    color = ForeColors[1]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            val absorbingNodeMesh500 = NodeMesh()
            val randomNode500 = Node(position = nodeMesh.nodes.randomPosition())

            secondContainer.text(text = "Centroid 1 (radius=500px)", color = ForeColors[3], textSize = TextSize, alignment = TextAlignCenter).position(randomNode500.position + textOffset)

            secondContainer.circle {
                position(randomNode500.position)
                radius = 10.0
                color = ForeColors[3]
                strokeThickness = 3.0
                onClick{
                    commandViews[CommandView.NODE_UUID_TEXT].setText(randomNode500.uuid.toString())
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(randomNode500.description)
                }
            }

            (0..10).toList().forEach { index ->
                absorbingNodeMesh500.absorbMesh(randomNode500, index * 50.0, nodeMesh)

                stroke(BackColors[3], StrokeInfo(thickness = 3.0)) {

                    for (nodeLine in absorbingNodeMesh500.getNodeLineList() ) {
                        line(nodeLine!!.first, nodeLine.second )
                    }
                }

                for (node in absorbingNodeMesh500.nodes) {
                    secondContainer.circle {
                        position(node.position)
                        radius = 5.0
                        color = ForeColors[3]
                        strokeThickness = 3.0
                        onClick{
                            commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                        }
                    }
                }
                delay(TimeSpan(500.0) )
            }

            val absorbingNodeMesh300 = NodeMesh()
            val randomNode300 = Node(position = nodeMesh.nodes.randomPosition())

            secondContainer.text(text = "Centroid 2 (radius=300px)", color = ForeColors[4], textSize = TextSize, alignment = TextAlignCenter).position(randomNode300.position + textOffset)

            secondContainer.circle {
                position(randomNode300.position)
                radius = 10.0
                color = ForeColors[4]
                strokeThickness = 3.0
                onClick{
                    commandViews[CommandView.NODE_UUID_TEXT].setText(randomNode300.uuid.toString())
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(randomNode300.description)
                }
            }

            (0..6).toList().forEach { index ->
                absorbingNodeMesh300.absorbMesh(randomNode300, index * 50.0, nodeMesh)

                stroke(BackColors[4], StrokeInfo(thickness = 3.0)) {

                    for (nodeLine in absorbingNodeMesh300.getNodeLineList() ) {
                        line(nodeLine!!.first, nodeLine.second )
                    }
                }

                for (node in absorbingNodeMesh300.nodes) {
                    secondContainer.circle {
                        position(node.position)
                        radius = 5.0
                        color = ForeColors[4]
                        strokeThickness = 3.0
                        onClick{
                            commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                        }
                    }
                }
                delay(TimeSpan(500.0) )
            }

            val absorbingNodeMesh200 = NodeMesh()
            val randomNode200 = Node(position = nodeMesh.nodes.randomPosition())

            secondContainer.text(text = "Centroid 3 (radius=200px)", color = ForeColors[5], textSize = TextSize, alignment = TextAlignCenter).position(randomNode200.position + textOffset)

            secondContainer.circle {
                position(randomNode200.position)
                radius = 10.0
                color = ForeColors[5]
                strokeThickness = 3.0
                onClick{
                    commandViews[CommandView.NODE_UUID_TEXT].setText(randomNode200.uuid.toString())
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(randomNode200.description)
                }
            }

            (0..4).toList().forEach { index ->
                absorbingNodeMesh200.absorbMesh(randomNode200, index * 50.0, nodeMesh)

                stroke(BackColors[5], StrokeInfo(thickness = 3.0)) {

                    for (nodeLine in absorbingNodeMesh200.getNodeLineList() ) {
                        line(nodeLine!!.first, nodeLine.second )
                    }
                }

                for (node in absorbingNodeMesh200.nodes) {
                    secondContainer.circle {
                        position(node.position)
                        radius = 5.0
                        color = ForeColors[5]
                        strokeThickness = 3.0
                        onClick{
                            commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                        }
                    }
                }
                delay(TimeSpan(500.0) )
            }
        }

        while (RenderPalette.returnClick == null) {
            delay(TimeSpan(100.0))
        }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeMeshOrphans(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        val leafHeight = 5

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeMeshOrphanDiff() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("demonstration of removeOrphans() upon a NodeMesh generated from three Leaf(height=$leafHeight)")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        commandViews[CommandView.PREV_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val startingPoint = Point(462, 574)

        val topTextOffset = Point(500, -300)
        val bottomTextOffset = Point(500, -240)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            val leafFirst = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()

            nodeMesh.consolidateStackedNodes()
            nodeMesh.consolidateNearNodes()
            nodeMesh.linkNearNodes()
            nodeMesh.pruneNodeLinks()
            nodeMesh.consolidateNodeLinks()

            (0 until 20).forEach { _ -> nodeMesh.nodeLinks.remove( nodeMesh.nodeLinks.getRandomNodeLink() ) }

            secondContainer.text(text = "NodeMesh() from threeLeaf with 20 links removed", color = ForeColors[1], textSize = TextSize, alignment = TextAlignRight).position(startingPoint + topTextOffset)

            for (nodeLine in nodeMesh.getNodeLineList()) {

                stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            nodeMesh.nodes.forEach { node ->
                secondContainer.circle {
                    position(node.position)
                    radius = 5.0
                    color = ForeColors[1]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            delay(TimeSpan(1000.0))

            nodeMesh.removeOrphans()

            nodeMesh.nodes.forEach { node ->

                for (nodeLine in nodeMesh.getNodeLineList()) {

                    stroke(BackColors[3], StrokeInfo(thickness = 3.0)) {
                        line(nodeLine!!.first, nodeLine.second )
                    }
                }

                secondContainer.circle {
                    position(node.position)
                    radius = 5.0
                    color = ForeColors[3]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            secondContainer.text(text = "NodeMesh() network(s) containing at least 25% of total mesh nodes", color = ForeColors[3], textSize = TextSize, alignment = TextAlignRight).position(startingPoint + bottomTextOffset)

        }

        while (RenderPalette.returnClick == null) {
            delay(TimeSpan(100.0))
        }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }


    @ExperimentalUnsignedTypes
    suspend fun renderNodeMeshEdges(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        val leafHeight = 7

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeMeshEdges() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("demonstration of edge finding upon a NodeMesh generated from three Leaf(height=$leafHeight)")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        commandViews[CommandView.PREV_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val startingPoint = Point(462, 574)

        val meshTextOffset = Point(500, -300)
        val textOffset60 = Point(500, -250)
        val textOffset70 = Point(500, -200)
        val textOffset80 = Point(500, -150)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            val leafFirst = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(90) )
            val leafSecond = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(210) )
            val leafThird = Leaf(topHeight = leafHeight, position = startingPoint, angleFromParent = Angle.fromDegrees(330) )

            val threeLeaf = leafFirst.getList().plus(leafSecond.getList()).plus(leafThird.getList())
            val nodeMesh = threeLeaf.nodeMesh()

            nodeMesh.consolidateStackedNodes()
            nodeMesh.consolidateNearNodes()
            nodeMesh.linkNearNodes()
            nodeMesh.pruneNodeLinks()
            nodeMesh.consolidateNodeLinks()

            secondContainer.text(text = "threeLeaf.nodeMesh()", color = ForeColors[1], textSize = TextSize, alignment = TextAlignRight).position(startingPoint + meshTextOffset)

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in nodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first, nodeLine.second )
                }
            }

            for (node in nodeMesh.nodes) {
                secondContainer.circle {
                    position(node.position)
                    radius = 5.0
                    color = ForeColors[1]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            val centroid = Node(position = nodeMesh.nodes.averagePositionWithinNodes())

            secondContainer.circle {
                position(centroid.position)
                radius = 12.0
                color = ForeColors[1]
                strokeThickness = 3.0
                onClick{
                    commandViews[CommandView.NODE_UUID_TEXT].setText(centroid.uuid.toString())
                    commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(centroid.description)
                }
            }

            val farthestNode = nodeMesh.nodes.getFarthestNode(centroid)

            val outerNodes80 = nodeMesh.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .8 }

            val outerNodes70 = nodeMesh.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .7 }

            val outerNodes60 = nodeMesh.nodes.nearestNodesOrderedAsc(centroid).filter { node -> Point.distance(centroid.position, node.position) >= Point.distance(centroid.position, farthestNode.position) * .6 }

            delay(TimeSpan(500.0) )

            secondContainer.text(text = "nodes >= 60% distance from centroid", color = ForeColors[2], textSize = TextSize, alignment = TextAlignRight).position(startingPoint + textOffset60)

            for (node in outerNodes60) {
                secondContainer.circle {
                    position(node.position)
                    radius = 5.0
                    color = ForeColors[2]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            delay(TimeSpan(500.0) )

            secondContainer.text(text = "nodes >= 70% distance from centroid", color = ForeColors[3], textSize = TextSize, alignment = TextAlignRight).position(startingPoint + textOffset70)

            for (node in outerNodes70) {
                secondContainer.circle {
                    position(node.position)
                    radius = 5.0
                    color = ForeColors[3]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            delay(TimeSpan(500.0) )

            secondContainer.text(text = "nodes >= 80% distance from centroid", color = ForeColors[4], textSize = TextSize, alignment = TextAlignRight).position(startingPoint + textOffset80)

            for (node in outerNodes80) {
                secondContainer.circle {
                    position(node.position)
                    radius = 5.0
                    color = ForeColors[4]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
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
}