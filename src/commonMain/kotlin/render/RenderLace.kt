package render

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.line
import exploreKeys
import leaf.ILeaf
import leaf.ILeaf.Companion.NextDistancePx
import leaf.ILeaf.Companion.nodeMesh
import leaf.Lace
import leaf.Line.Companion.borderLines
import node.INodeMesh
import node.INodeMesh.Companion.getBorderingMesh
import node.Node
import node.NodeLink
import node.NodeMesh

object RenderLace {

    @ExperimentalUnsignedTypes
    suspend fun renderLace(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 0
        val funSize = 4

        while ( (funIdx >= 0) && (funIdx < funSize) ) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())
            commandViews[CommandView.NODE_POSITION_TEXT].setText(CommandView.NODE_POSITION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderLaceHeights(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                1 -> if ( renderLaceAngled(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                2 -> if ( renderLaceSpiral(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                3 -> if ( renderLaceBordering(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
            }
        }

        return if (funIdx > 0) ButtonCommand.NEXT else ButtonCommand.PREV
    }

    @ExperimentalUnsignedTypes
    suspend fun renderLaceHeights(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        val convergeDegrees = 250

        commandViews[CommandView.LABEL_TEXT].setText("renderLaceStationary() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing Lace() at various heights")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("Lace attempts to converge to topAngle = $convergeDegrees degrees with variance of 60 degrees")
        commandViews[CommandView.PREV_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val laceList = List(8) { Lace(topHeight = it + 1, topAngle = Angle.fromDegrees(convergeDegrees), position = Point((8 - it) * 100 + 100, (8 - it) * 100 + 100) ) }

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            laceList.reversed().forEachIndexed {laceIdx, lace ->
                secondContainer.text(text= "Lace(height=${lace.topHeight})", color = RenderPalette.ForeColors[laceIdx % RenderPalette.ForeColors.size], alignment = RenderPalette.TextAlignCenter).position(Point(lace.position.x, lace.position.y - 30))

                stroke(RenderPalette.BackColors[laceIdx % RenderPalette.BackColors.size], StrokeInfo(thickness = 3.0)) {

                    for (line in lace.getLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (listLeaf in lace.getList() ) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = RenderPalette.ForeColors[laceIdx % RenderPalette.ForeColors.size]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(listLeaf.position.toString())
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
    suspend fun renderLaceAngled(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderLaceAngled() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing Lace() at various angleFromParents")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        RenderPalette.returnClick = null

        val startingPoint = Point(100.0, 500.0)

        val startingMap = mapOf(
            0 to startingPoint
            , 45 to Point(500 - 400 * sin(Angle.fromDegrees(45)), 500 + 400 * sin(Angle.fromDegrees(45)) )
            , 90 to Point(500.0, 900.0)
            , 135 to Point(512 + 400 * sin(Angle.fromDegrees(45)), 500 + 400 * sin(Angle.fromDegrees(45)) )
            , 180 to Point(900.0, 500.0)
            , 225 to Point(500 + 400 * sin(Angle.fromDegrees(45)), 500 - 400 * sin(Angle.fromDegrees(45)) )
            , 270 to Point(500.0, 100.0)
            , 315 to Point(500 - 400 * sin(Angle.fromDegrees(45)), 500 - 400 * sin(Angle.fromDegrees(45)) )
        )

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            startingMap.forEach {
                val idx = it.key / 45

                val lace = Lace(topHeight = 4, topAngle = Angle.fromDegrees(it.key), position = it.value )

                when {
                    (idx in 0..3) ->
                        secondContainer.text(text= "Lace(height=${lace.topHeight})\nangled ${it.key} degrees", color = RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size], alignment = RenderPalette.TextAlignCenter).position(Point(it.value.x, it.value.y + 30))
                    else ->
                        secondContainer.text(text= "Lace(height=${lace.topHeight})\nangled ${it.key} degrees", color = RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size], alignment = RenderPalette.TextAlignCenter).position(Point(it.value.x, it.value.y - 30))
                }

                stroke(RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size], StrokeInfo(thickness = 3.0)) {

                    for (line in lace.getLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (listLace in lace.getList() ) {
                    secondContainer.circle { position(listLace.position)
                        radius = 5.0
                        color = RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(listLace.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLace.description)
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(listLace.position.toString())
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
    suspend fun renderLaceSpiral(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderLaceSpiral() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing Lace() with spiraling algorithm")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("gold line shows top laceNode to center, red line shows last laceNode to center")
        RenderPalette.returnClick = null

        val centerPoint = Point(512.0, 512.0)

        val laceHeight = 5

        val lacePoints = laceHeight + 1

        val laceMap = mutableMapOf<Angle, Point>()

        (0 until lacePoints).toList().forEach{ laceIndex ->
            val angleOnCircle = Angle.fromDegrees( 360 / lacePoints * laceIndex ).normalized

            //angleInMap points back to the center of the circle
            laceMap[(Angle.fromDegrees(180) + angleOnCircle).normalized] = ILeaf.getChildPosition(centerPoint, (laceHeight + laceIndex - 2) * NextDistancePx, angleOnCircle)
        }

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            laceMap.forEach {

                val idx = it.key.degrees.toInt() / 45

                val lace = Lace(topHeight = laceHeight, topAngle = it.key, angleFromParent = it.key, position = Point(it.value.x, it.value.y))

                when {
                    (idx in 0..3) ->
                        secondContainer.text(text= "Lace(height=$laceHeight)\nangled ${it.key.degrees.toInt()} degrees", color = RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size], alignment = RenderPalette.TextAlignCenter).position(Point(it.value.x, it.value.y + 30))
                    else ->
                        secondContainer.text(text= "Lace(height=$laceHeight)\nangled ${it.key.degrees.toInt()} degrees", color = RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size], alignment = RenderPalette.TextAlignCenter).position(Point(it.value.x, it.value.y - 30))
                }

                val finalPoints = mutableListOf<Point>()

                stroke(RenderPalette.BackColors[2], StrokeInfo(thickness = 1.0)) {

                    line(it.value, centerPoint)
                }

                stroke(RenderPalette.BackColors[idx % RenderPalette.BackColors.size], StrokeInfo(thickness = 3.0)) {

                    for (line in lace.getLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                stroke(RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size], StrokeInfo(thickness = 3.0)) {

                    val laceList = lace.getList()
                    val laceListSize = laceList.size

                    laceList.forEachIndexed { laceIndex, listLace ->
                        secondContainer.circle { position(listLace.position)
                            radius = 5.0
                            color = RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size]
                            strokeThickness = 3.0
                            onClick {
                                commandViews[CommandView.NODE_UUID_TEXT].setText(listLace.uuid.toString())
                                commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLace.description)
                                commandViews[CommandView.NODE_POSITION_TEXT].setText(listLace.position.toString())
                            }
                        }

                        if (laceIndex == laceListSize - 1) finalPoints.add(listLace.position)
                    }
                }
                stroke(RenderPalette.BackColors[3], StrokeInfo(thickness = 3.0)) {

                    finalPoints.forEach { finalPoint ->
                        line(finalPoint, centerPoint)
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
    suspend fun renderLaceBordering(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderLaceBordering() [v0.4]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing getBorderingMesh()")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("Original Lace NodeMesh (bg) and bordering Lace NodeMesh (fg) shown")
        RenderPalette.returnClick = null

        val refNodesCases = listOf(
            listOf(Node(position = Point(150, 100)), Node(position = Point(200, 150)), Node(position = Point(100, 200)))
            , listOf(Node(position = Point(100, 400)), Node(position = Point(200, 450)), Node(position = Point(150, 500)))
            , listOf(Node(position = Point(200, 700)), Node(position = Point(150, 750)), Node(position = Point(100, 800)))
            , listOf(Node(position = Point(700, 200)), Node(position = Point(600, 250)), Node(position = Point(650, 300)))
            , listOf(Node(position = Point(600, 500)), Node(position = Point(650, 550)), Node(position = Point(700, 600)))
            , listOf(Node(position = Point(650, 800)), Node(position = Point(600, 850)), Node(position = Point(700, 900)))
        )

        val refNodeLinksCases = refNodesCases.map { nodesCases: List<Node> ->
            listOf(NodeLink(firstNodeUuid = nodesCases[0].uuid, secondNodeUuid = nodesCases[1].uuid), NodeLink(firstNodeUuid = nodesCases[1].uuid, secondNodeUuid = nodesCases[2].uuid))
        }

        val refNodeMeshCases = refNodesCases.mapIndexed { idx : Int, nodesCases: List<Node> ->
            NodeMesh( nodes = nodesCases.toMutableList(), nodeLinks = refNodeLinksCases[idx].toMutableList())
        }

        val topHeight = 5
        val borderingLaceCases = mutableListOf<INodeMesh>(
            Lace(topHeight = topHeight, position = Point(300, 150), topAngle = Angle.fromDegrees(180) ).getList().nodeMesh()
            , Lace(topHeight = topHeight, position = Point(300, 450), topAngle = Angle.fromDegrees(180) ).getList().nodeMesh()
            , Lace(topHeight = topHeight, position = Point(300, 750), topAngle = Angle.fromDegrees(180) ).getList().nodeMesh()
            , Lace(topHeight = topHeight, position = Point(800, 250), topAngle = Angle.fromDegrees(180) ).getList().nodeMesh()
            , Lace(topHeight = topHeight, position = Point(800, 550), topAngle = Angle.fromDegrees(180) ).getList().nodeMesh()
            , Lace(topHeight = topHeight, position = Point(800, 850), topAngle = Angle.fromDegrees(180) ).getList().nodeMesh()
            , Lace(topHeight = topHeight, position = Point(50, 150), topAngle = Angle.fromDegrees(0) ).getList().nodeMesh()
            , Lace(topHeight = topHeight, position = Point(50, 450), topAngle = Angle.fromDegrees(0) ).getList().nodeMesh()
            , Lace(topHeight = topHeight, position = Point(50, 750), topAngle = Angle.fromDegrees(0) ).getList().nodeMesh()
            , Lace(topHeight = topHeight, position = Point(550, 250), topAngle = Angle.fromDegrees(0) ).getList().nodeMesh()
            , Lace(topHeight = topHeight, position = Point(550, 550), topAngle = Angle.fromDegrees(0) ).getList().nodeMesh()
            , Lace(topHeight = topHeight, position = Point(550, 850), topAngle = Angle.fromDegrees(0) ).getList().nodeMesh()
        )

        val textOffsetPosition = Point(0, -30)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            (0..5).forEach { idx ->
                val secondLaceIdx = idx + 6
                secondContainer.text(text= "Test Case $idx", color = RenderPalette.ForeColors[idx % RenderPalette.BackColors.size], alignment = RenderPalette.TextAlignCenter).position(refNodeMeshCases[idx].nodes[0].position + textOffsetPosition)
                secondContainer.text(text= "Lace(height=$topHeight)", color = RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size], alignment = RenderPalette.TextAlignCenter).position(borderingLaceCases[idx].nodes[0].position + textOffsetPosition)
                secondContainer.text(text= "Lace(height=$topHeight)", color = RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size], alignment = RenderPalette.TextAlignCenter).position(borderingLaceCases[secondLaceIdx].nodes[0].position + textOffsetPosition)

                stroke(RenderPalette.BackColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in borderingLaceCases[idx].getNodeLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                    for (line in borderingLaceCases[secondLaceIdx].getNodeLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (listLeaf in borderingLaceCases[idx].nodes) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = RenderPalette.BackColors[idx]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(listLeaf.position.toString())
                        }
                    }
                }

                for (listLeaf in borderingLaceCases[secondLaceIdx].nodes) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = RenderPalette.BackColors[idx]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(listLeaf.position.toString())
                        }
                    }
                }

                stroke(RenderPalette.BackColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in refNodeMeshCases[idx].getNodeLineList() ) {
                        if (line != null) {
                            line(line.first, line.second)

                            val minBorderLines = line.borderLines((NextDistancePx * 0.2).toInt())

                            for (minBorderLine in minBorderLines) {
                                line(minBorderLine.first, minBorderLine.second)
                            }
                        }
                    }
                }

                for (listPoint in refNodeMeshCases[idx].nodes ) {
                    secondContainer.circle { position(listPoint.position)
                        radius = 5.0
                        color = RenderPalette.BackColors[idx]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(listPoint.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listPoint.description)
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(listPoint.position.toString())
                        }
                    }
                }

                val borderingMesh = borderingLaceCases[idx].getBorderingMesh(refNodeMeshCases[idx])
                val secondBorderingMesh = borderingLaceCases[secondLaceIdx].getBorderingMesh(refNodeMeshCases[idx])

                stroke(RenderPalette.ForeColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in borderingMesh.getNodeLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                    for (line in secondBorderingMesh.getNodeLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (listLeaf in borderingMesh.nodes) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = RenderPalette.ForeColors[idx]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(listLeaf.position.toString())
                        }
                    }
                }

                for (listLeaf in secondBorderingMesh.nodes) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = RenderPalette.ForeColors[idx]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                            commandViews[CommandView.NODE_POSITION_TEXT].setText(listLeaf.position.toString())
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
}