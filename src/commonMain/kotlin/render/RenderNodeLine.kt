package render

import com.soywiz.klock.TimeSpan
import com.soywiz.korio.async.delay
import node.NodeMesh
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.line
import leaf.ILeaf.Companion.NextDistancePx
import leaf.ILeaf.Companion.nodeMesh
import leaf.Leaf
import leaf.Line.Companion.borderLines
import node.INodeMesh
import node.INodeMesh.Companion.addMesh
import node.INodeMesh.Companion.getBorderingMesh
import node.Node
import node.NodeLink
import node.NodeLink.Companion.buildNodeLinkLine
import node.NodeLink.Companion.consolidateNodeDistance
import render.RenderPalette.BackColors
import render.RenderPalette.ForeColors
import render.RenderPalette.TextAlignLeft
import render.RenderPalette.TextSize

object RenderNodeLine {

    @ExperimentalUnsignedTypes
    suspend fun renderNodeLine(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 0
        val funSize = 3

        while ( (funIdx >= 0) && (funIdx < funSize) ) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderNodeLineLengthsNoises(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                1 -> if ( renderNodeLineMesh(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                2 -> if ( renderNodeLinesBordering(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
            }
        }

        return if (funIdx > 0) ButtonCommand.NEXT else ButtonCommand.PREV
    }

    @ExperimentalUnsignedTypes
    suspend fun renderNodeLineLengthsNoises(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeLineHeightsNoises() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing buildNodeLinkLines() at various lengths and noises")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        commandViews[CommandView.NEXT_BUTTON]!!.visible = true
        commandViews[CommandView.PREV_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val firstNodeList = List(8) { idx -> Node(description = "nodeLine${idx}", position = Point((8 - idx) * 100 + 50, (8 - idx) * 100 + 50) ) }
        val secondNodeList = List(8) { idx -> Node(description = "nodeLine${idx}", position = Point((8 - idx) * 100 + 50, 900 ) ) }

        //no need to render link distance

        val thirdRefPoint = Point(600, 250)
        val fourthRefPoint = Point (600 + consolidateNodeDistance + 1, 250)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            secondContainer.text(text = "linkDistance", color = ForeColors[0], textSize = TextSize, alignment = TextAlignLeft).position(thirdRefPoint + Point(-10, -25))

            stroke(BackColors[0], StrokeInfo(thickness = 3.0)) {

                line(thirdRefPoint, fourthRefPoint)
            }

            stroke(ForeColors[0], StrokeInfo(thickness = 3.0)) {

                circle(thirdRefPoint, radius = 5.0)
                circle(fourthRefPoint, radius = 5.0)
            }

            firstNodeList.reversed().forEachIndexed { nodeIdx, firstNode ->
                val secondNode = secondNodeList[7 - nodeIdx]
                val lineNoise = (7 - nodeIdx) * 15
                val adjLineNoise = if (lineNoise > 100) 100 else lineNoise

                secondContainer.text(text= "Node() line length=${(secondNode.position.y - firstNode.position.y).toInt()}px, noise=${adjLineNoise}", color = ForeColors[nodeIdx % ForeColors.size], alignment = RenderPalette.TextAlignCenter).position(Point(firstNode.position.x, firstNode.position.y - 30))

                val nodeLineMesh = Pair(firstNode, secondNode).buildNodeLinkLine(noise = adjLineNoise, firstNode.description)

                stroke(BackColors[nodeIdx % BackColors.size], StrokeInfo(thickness = 3.0)) {

                    for (line in nodeLineMesh.getNodeLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (node in nodeLineMesh.nodes) {
                    secondContainer.circle { position(node.position)
                        radius = 5.0
                        color = ForeColors[nodeIdx % ForeColors.size]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
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

    @ExperimentalUnsignedTypes
    suspend fun renderNodeLineMesh(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeLineMesh() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing buildNodeLinkLines() and NodeMesh operations")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        commandViews[CommandView.PREV_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val startingMap = listOf(
            Node( position = Point(352, 474) )
            , Node( position = Point(262, 674) )
            , Node( position = Point(652, 574) )
            , Node( position = Point(752, 774) )
            , Node( position = Point(652, 280) )
        )

        val xOffset = Point(250, 0)

        val firstRefPoint = Point(500, 200)
        val secondRefPoint = Point (500 + NextDistancePx, 200)

        val thirdRefPoint = Point(500, 250)
        val fourthRefPoint = Point (500 + consolidateNodeDistance, 250)

        val fifthRefPoint = Point(500, 300)
        val sixthRefPoint = Point (500 + consolidateNodeDistance + 1, 300)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            secondContainer.text(text = "linkNodeDistance", color = ForeColors[0], textSize = TextSize, alignment = TextAlignLeft).position(firstRefPoint + Point(-10, -25))
            secondContainer.text(text = "consolidateNodeDistance", color = ForeColors[0], textSize = TextSize, alignment = TextAlignLeft).position(thirdRefPoint + Point(-10, -25))
            secondContainer.text(text = "linkDistance", color = ForeColors[0], textSize = TextSize, alignment = TextAlignLeft).position(fifthRefPoint + Point(-10, -25))

            stroke(BackColors[0], StrokeInfo(thickness = 3.0)) {

                line(firstRefPoint, secondRefPoint)
                line(thirdRefPoint, fourthRefPoint)
                line(fifthRefPoint, sixthRefPoint)
            }

            stroke(ForeColors[0], StrokeInfo(thickness = 3.0)) {

                circle(firstRefPoint, radius = 5.0)
                circle(secondRefPoint, radius = 5.0)
                circle(thirdRefPoint, radius = 5.0)
                circle(fourthRefPoint, radius = 5.0)
                circle(fifthRefPoint, radius = 5.0)
                circle(sixthRefPoint, radius = 5.0)
            }

            val lineNodeMesh = NodeMesh()
            val lineNoise = 70

            startingMap.forEachIndexed { outerIndex, outer ->
                startingMap.forEachIndexed { innerIndex, inner ->
                    if (innerIndex > outerIndex) {
                        lineNodeMesh.addMesh( Pair(outer, inner).buildNodeLinkLine(noise = lineNoise, lineNodeMesh.description) )
                    }
                }
            }

            secondContainer.text(text= "Node() fivePoints, noise=${lineNoise}", color = ForeColors[1], alignment = RenderPalette.TextAlignCenter).position(Point(startingMap[4].position.x, startingMap[4].position.y + 550) - xOffset)

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in lineNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first - xOffset, nodeLine.second - xOffset)
                }
            }

            for (node in lineNodeMesh.nodes) {
                secondContainer.circle {
                    position(node.position - xOffset)
                    radius = 5.0
                    color = ForeColors[1]
                    strokeThickness = 3.0
                    onClick{
                        commandViews[CommandView.NODE_UUID_TEXT].setText(node.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(node.description)
                    }
                }
            }

            lineNodeMesh.consolidateStackedNodes()
            lineNodeMesh.consolidateNearNodes()
            lineNodeMesh.linkNearNodes()
            lineNodeMesh.pruneNodeLinks()
            lineNodeMesh.consolidateNodeLinks()

            secondContainer.text(text= "Node() fivePoints with INodeMesh.processMesh(), noise=${lineNoise}", color = ForeColors[2], alignment = RenderPalette.TextAlignCenter).position(Point(startingMap[4].position.x + 150, startingMap[4].position.y + 550) )

            stroke(BackColors[2], StrokeInfo(thickness = 3.0)) {

                for (nodeLine in lineNodeMesh.getNodeLineList() ) {
                    line(nodeLine!!.first + xOffset, nodeLine.second + xOffset)
                }
            }

            for (node in lineNodeMesh.nodes) {

                secondContainer.circle {
                    position(node.position + xOffset)
                    radius = 5.0
                    color = ForeColors[2]
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
    suspend fun renderNodeLinesBordering(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderNodeLinesBordering() [v0.4]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing getBorderingMesh()")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("Original NodeLine NodeMesh (bg) and bordering NodeLine NodeMesh (fg) shown")
        RenderPalette.returnClick = null

        val refNodesCases = listOf(
            listOf(Node(position = Point(100, 200)), Node(position = Point(950, 300)))
            , listOf(Node(position = Point(300, 150)), Node(position = Point(250, 900)))
            , listOf(Node(position = Point(100, 400)), Node(position = Point(950, 550)))
            , listOf(Node(position = Point(600, 150)), Node(position = Point(550, 900)))
            , listOf(Node(position = Point(100, 700)), Node(position = Point(950, 800)))
            , listOf(Node(position = Point(900, 150)), Node(position = Point(800, 900)))
        )

        val adjLineNoise = 90

        val borderingNodeLineCases = refNodesCases.map { nodesCases: List<Node> ->
            Pair(nodesCases[0], nodesCases[1]).buildNodeLinkLine(noise = adjLineNoise).addMesh(
                Pair(nodesCases[0], nodesCases[1]).buildNodeLinkLine(noise = adjLineNoise).addMesh(
                    Pair(nodesCases[0], nodesCases[1]).buildNodeLinkLine(noise = adjLineNoise)
                )
            )
        }

        val borderingMesh = listOf(
            NodeMesh(copyNodeMesh = borderingNodeLineCases[0] as NodeMesh).getBorderingMesh(borderingNodeLineCases[1]).getBorderingMesh(borderingNodeLineCases[5])
            , NodeMesh(copyNodeMesh = borderingNodeLineCases[1] as NodeMesh).getBorderingMesh(borderingNodeLineCases[2])
            , NodeMesh(copyNodeMesh = borderingNodeLineCases[2] as NodeMesh).getBorderingMesh(borderingNodeLineCases[3])
            , NodeMesh(copyNodeMesh = borderingNodeLineCases[3] as NodeMesh).getBorderingMesh(borderingNodeLineCases[0]).getBorderingMesh(borderingNodeLineCases[4])
            , NodeMesh(copyNodeMesh = borderingNodeLineCases[4] as NodeMesh).getBorderingMesh(borderingNodeLineCases[1]).getBorderingMesh(borderingNodeLineCases[5])
            , NodeMesh(copyNodeMesh = borderingNodeLineCases[5] as NodeMesh).getBorderingMesh(borderingNodeLineCases[2])
        )

        val textOffsetPosition = Point(0, -30)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            (0..5).forEach { idx ->
                secondContainer.text(text= "NodeLine Test Case $idx", color = ForeColors[idx % BackColors.size], alignment = RenderPalette.TextAlignCenter).position(refNodesCases[idx][0].position + textOffsetPosition)

                stroke(BackColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in borderingNodeLineCases[idx].getNodeLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (listLeaf in borderingNodeLineCases[idx].nodes) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = BackColors[idx]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                        }
                    }
                }

                stroke(ForeColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in borderingMesh[idx].getNodeLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (listLeaf in borderingMesh[idx].nodes) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = ForeColors[idx]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                        }
                    }
                }
            }
        }
        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }
}