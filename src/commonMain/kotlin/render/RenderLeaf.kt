package render

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.line
import leaf.ILeaf
import leaf.ILeaf.Companion.NextDistancePx
import leaf.ILeaf.Companion.add
import leaf.ILeaf.Companion.getLineList
import leaf.ILeaf.Companion.getList
import leaf.ILeaf.Companion.graft
import leaf.ILeaf.Companion.prune
import leaf.Leaf
import render.RenderPalette.BackColors
import render.RenderPalette.ForeColors
import render.RenderPalette.TextAlignCenter
import render.RenderPalette.TextAlignLeft
import render.RenderPalette.TextAlignRight
import kotlin.random.Random

object RenderLeaf {

    @ExperimentalUnsignedTypes
    suspend fun renderLeaf(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 0
        val funSize = 6

        while (funIdx < funSize) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderLeafHeights(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++
                1 -> if ( renderLeafAddGraft(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                2 -> if ( renderLeafPrune(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                3 -> if ( renderLeafAngled(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                4 -> if ( renderLeafSpiral(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                5 -> if ( renderLeafBordering(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
//  future directions:
//                6 -> if ( renderGraftedLeafs(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--

            }
        }

        return ButtonCommand.NEXT
    }

    @ExperimentalUnsignedTypes
    suspend fun renderLeafHeights(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderLeafStationary() [v0.1]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing Leaf() at various heights")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        commandViews[CommandView.PREV_BUTTON]!!.visible = false

        RenderPalette.returnClick = null

        val leafList = List(8) { Leaf(topHeight = it + 1, position = Point((8 - it) * 100 + 100, (8 - it) * 100 + 100) ) }

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            leafList.reversed().forEachIndexed {leafIdx, leaf ->
                secondContainer.text(text= "Leaf(height=${leaf.topHeight})", color = ForeColors[leafIdx % ForeColors.size], alignment = TextAlignCenter).position(Point(leaf.position.x, leaf.position.y - 30))

                stroke(BackColors[leafIdx % BackColors.size], StrokeInfo(thickness = 3.0)) {

                    for (line in leaf.getLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (listLeaf in leaf.getList() ) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = ForeColors[leafIdx % ForeColors.size]
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

    @ExperimentalUnsignedTypes
    suspend fun renderLeafAddGraft(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderAddGraftLeafStationary() [v0.2]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing firstLeaf.getList().add(secondLeaf) and thirdLeaf.getList().graft(fourthLeaf)")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        commandViews[CommandView.PREV_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val firstStartingPoint = Point(200.0, 200.0)
        val secondStartingPoint = Point(300.0, 300.0)

        val thirdStartingPoint = Point(600.0, 400.0)
        val fourthStartingPoint = Point(800.0, 800.0)

        val firstLeaf = Leaf(topHeight = 5, position = firstStartingPoint)
        val secondLeaf = Leaf(topHeight = 5, position = secondStartingPoint)

        val thirdLeaf = Leaf(topHeight = 5, position = thirdStartingPoint)
        val fourthLeaf = Leaf(topHeight = 5, position = fourthStartingPoint)

        val firstRandLeafIdx = Random.nextInt(firstLeaf.getList().size)
        val thirdRandLeafIdx = Random.nextInt(thirdLeaf.getList().size)

        firstLeaf.getList()[firstRandLeafIdx].add(secondLeaf)
        thirdLeaf.getList()[thirdRandLeafIdx].graft(fourthLeaf)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            secondContainer.text(text= "Leaf(height=${secondLeaf.topHeight}) added to Leaf(height=${firstLeaf.topHeight})", color = ForeColors[1], alignment = TextAlignCenter).position(Point(firstLeaf.position.x, firstLeaf.position.y - 30))

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (line in firstLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }

            for (listLeaf in firstLeaf.getList() ) {
                secondContainer.circle { position(listLeaf.position)
                    radius = 5.0
                    color = ForeColors[1]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                    }
                }
            }

            stroke(BackColors[5], StrokeInfo(thickness = 3.0)) {

                for (line in secondLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }

            for (listLeaf in secondLeaf.getList() ) {
                secondContainer.circle { position(listLeaf.position)
                    radius = 5.0
                    color = ForeColors[5]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                    }
                }
            }

            secondContainer.text(text= "Leaf(height=${fourthLeaf.topHeight}) grafted to Leaf(height=${thirdLeaf.topHeight})", color = ForeColors[2], alignment = TextAlignCenter).position(Point(thirdLeaf.position.x, thirdLeaf.position.y - 30))

            stroke(BackColors[2], StrokeInfo(thickness = 3.0)) {

                for (line in thirdLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }

            for (listLeaf in thirdLeaf.getList() ) {
                secondContainer.circle { position(listLeaf.position)
                    radius = 5.0
                    color = ForeColors[2]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                    }
                }
            }

            stroke(BackColors[6], StrokeInfo(thickness = 3.0)) {

                for (line in fourthLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }

            for (listLeaf in fourthLeaf.getList() ) {
                secondContainer.circle { position(listLeaf.position)
                    radius = 5.0
                    color = ForeColors[6]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                    }
                }
            }
        }

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderLeafPrune(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderPruneLeaf() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing leaf.getList().prune()")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("Leaf pruning removes intersections and children nodes within 10deg apart")
        RenderPalette.returnClick = null

        val startingPoint = Point(612.0, 512.0)

        val positionOffset = Point(-300, -350)

        val leaf = Leaf(topHeight = 8, position = startingPoint)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            secondContainer.text(text= "Leaf() pruned atop Leaf(height=${leaf.topHeight})", color = ForeColors[1], alignment = TextAlignCenter).position(Point(leaf.position.x, leaf.position.y - 30))

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (line in leaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }

            for (listLeaf in leaf.getList() ) {
                secondContainer.circle { position(listLeaf.position)
                    radius = 5.0
                    color = ForeColors[1]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                    }
                }
            }

            val pruneLeaf = leaf.getList().prune()

            stroke(BackColors[2], StrokeInfo(thickness = 3.0)) {

                for (line in pruneLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }

            for (listLeaf in pruneLeaf.getList() ) {
                secondContainer.circle { position(listLeaf.position)
                    radius = 5.0
                    color = ForeColors[2]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                    }
                }
            }

            secondContainer.text(text= "Leaf(height=${leaf.topHeight}) pruned", color = ForeColors[0], alignment = TextAlignCenter).position(Point(leaf.position.x + positionOffset.x, leaf.position.y + positionOffset.y - 30))

            stroke(BackColors[0], StrokeInfo(thickness = 3.0)) {

                for (line in pruneLeaf.getLineList() ) {
                    if (line != null) line(line.first + positionOffset, line.second + positionOffset)
                }
            }

            for (listLeaf in pruneLeaf.getList() ) {
                secondContainer.circle { position(listLeaf.position + positionOffset)
                    radius = 5.0
                    color = ForeColors[0]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                    }
                }
            }
        }

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderLeafAngled(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderLeafAngled() [v0.2]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing Leaf() at various angleFromParents")
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

            val leaf = Leaf(topHeight = 4, angleFromParent = Angle.fromDegrees(it.key), position = it.value )

                when {
                    (idx in 0..3) ->
                        secondContainer.text(text= "Leaf(height=${leaf.topHeight})\nangled ${it.key} degrees", color = ForeColors[idx % ForeColors.size], alignment = TextAlignCenter).position(Point(it.value.x, it.value.y + 30))
                    else ->
                        secondContainer.text(text= "Leaf(height=${leaf.topHeight})\nangled ${it.key} degrees", color = ForeColors[idx % ForeColors.size], alignment = TextAlignCenter).position(Point(it.value.x, it.value.y - 30))
                }

                stroke(ForeColors[idx % ForeColors.size], StrokeInfo(thickness = 3.0)) {

                    for (line in leaf.getLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (listLeaf in leaf.getList() ) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = ForeColors[idx % ForeColors.size]
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

    @ExperimentalUnsignedTypes
    suspend fun renderLeafSpiral(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderLeafSpiral() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing Leaf() with spiraling algorithm")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("gold line shows top leafNode to center, red line shows last leafNode to center")
        RenderPalette.returnClick = null

        val centerPoint = Point(512.0, 512.0)

        val leafHeight = 5

        val leafPoints = leafHeight + 1

        val leafMap = mutableMapOf<Angle, Point>()

        (0 until leafPoints).toList().forEach{ leafIndex ->
            val angleOnCircle = Angle.fromDegrees( 360 / leafPoints * leafIndex ).normalized

            //angleInMap points back to the center of the circle
            leafMap[(Angle.fromDegrees(180) + angleOnCircle).normalized] = ILeaf.getChildPosition(centerPoint, (leafHeight + leafIndex - 2) * NextDistancePx, angleOnCircle)
        }

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            leafMap.forEach {

                val idx = it.key.degrees.toInt() / 45

                val leaf = Leaf(topHeight = leafHeight, topAngle = it.key, angleFromParent = it.key, position = Point(it.value.x, it.value.y))

                when {
                    (idx in 0..3) ->
                        secondContainer.text(text= "Leaf(height=$leafHeight)\nangled ${it.key.degrees.toInt()} degrees", color = ForeColors[idx % ForeColors.size], alignment = TextAlignCenter).position(Point(it.value.x, it.value.y + 30))
                    else ->
                        secondContainer.text(text= "Leaf(height=$leafHeight)\nangled ${it.key.degrees.toInt()} degrees", color = ForeColors[idx % ForeColors.size], alignment = TextAlignCenter).position(Point(it.value.x, it.value.y - 30))
                }

                val finalPoints = mutableListOf<Point>()

                stroke(BackColors[2], StrokeInfo(thickness = 1.0)) {

                    line(it.value, centerPoint)
                }

                stroke(BackColors[idx % BackColors.size], StrokeInfo(thickness = 3.0)) {

                    for (line in leaf.getLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                stroke(ForeColors[idx % ForeColors.size], StrokeInfo(thickness = 3.0)) {

                    val leafList = leaf.getList()
                    val leafListSize = leafList.size

                    leafList.forEachIndexed { leafIndex, listLeaf ->
                        secondContainer.circle { position(listLeaf.position)
                            radius = 5.0
                            color = ForeColors[idx % ForeColors.size]
                            strokeThickness = 3.0
                            onClick {
                                commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                                commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                            }
                        }

                        if (leafIndex == leafListSize - 1) finalPoints.add(listLeaf.position)
                    }
                }
                stroke(BackColors[3], StrokeInfo(thickness = 3.0)) {

                    finalPoints.forEach { finalPoint ->
                        line(finalPoint, centerPoint)
                    }
                }
            }
        }

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderLeafBordering(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderBorderingLeaf() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing bordering with refILeaf")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("(work in progress)")
        RenderPalette.returnClick = null

        val leaf = Leaf(topHeight = 6, position = Point(256.0, 512.0), topAngle = Angle.fromDegrees(280) )

        val firstBorderingLeaf = Leaf(topHeight = 12, position = Point(482.0, 256.0), topAngle = Angle.fromDegrees(220), refILeaf = leaf )
        val secondBorderingLeaf = Leaf(topHeight = 12, position = Point(532.0, 256.0), topAngle = Angle.fromDegrees(350), refILeaf = leaf )

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            secondContainer.text(text= "Leaf(height=${leaf.topHeight})", color = ForeColors[1], alignment = TextAlignCenter).position(Point(650.0, 712.0))
            secondContainer.text(text= "Leaf(height=${firstBorderingLeaf.topHeight}), refILeaf == Leaf(height=${leaf.topHeight})", color = ForeColors[2], alignment = TextAlignRight).position(Point(350.0, 306.0 ))
            secondContainer.text(text= "Leaf(height=${secondBorderingLeaf.topHeight}), refILeaf == Leaf(height=${leaf.topHeight})", color = ForeColors[4], alignment = TextAlignLeft).position(Point(650.0, 306.0 ))

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (line in leaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }

            for (listLeaf in leaf.getList() ) {
                secondContainer.circle { position(listLeaf.position)
                    radius = 5.0
                    color = ForeColors[1]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                    }
                }
            }

            stroke(BackColors[2], StrokeInfo(thickness = 3.0)) {

                for (line in firstBorderingLeaf.getLineList()) {
                    if (line != null) line(line.first, line.second)
                }
            }

            stroke(BackColors[4], StrokeInfo(thickness = 3.0)) {
                for (line in secondBorderingLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }

            for (listLeaf in firstBorderingLeaf.getList() ) {
                secondContainer.circle { position(listLeaf.position)
                    radius = 5.0
                    color = ForeColors[2]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                    }
                }
            }

            for (listLeaf in secondBorderingLeaf.getList() ) {
                secondContainer.circle { position(listLeaf.position)
                    radius = 5.0
                    color = ForeColors[4]
                    strokeThickness = 3.0
                    onClick {
                        commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                        commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                    }
                }
            }
        }
        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }
}