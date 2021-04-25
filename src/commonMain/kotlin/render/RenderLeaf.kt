package render

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.input.mouse
import com.soywiz.korge.input.onClick
import com.soywiz.korge.resources.resourceBitmap
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korim.color.scale
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korio.resources.ResourcesContainer
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.circle
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
import render.RenderPalette.TextAlignLeft
import render.RenderPalette.TextAlignRight
import render.RenderPalette.TextColor
import render.RenderPalette.TextSize
import kotlin.random.Random

object RenderLeaf {

    @ExperimentalUnsignedTypes
    suspend fun renderLeaf(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 0
        val funSize = 6

        while (funIdx < funSize) {
            println ("funMapIdx : $funIdx")

            when (funIdx) {
                0 -> if ( renderLeafStationary(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++
                1 -> if ( renderAddGraftLeafStationary(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                2 -> if ( renderPruneLeaf(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                3 -> if ( renderLeafAngled(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                4 -> if ( renderLeafCircle(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                5 -> if ( renderBorderingLeaf(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
            }
        }

        return ButtonCommand.NEXT
    }

    @ExperimentalUnsignedTypes
    suspend fun renderLeafStationary(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LOADING_TEXT]!!.visible = true

        commandViews[CommandView.LABEL_TEXT].setText("renderLeafStationary() [v0.1]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing Leaf() at various heights")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        commandViews[CommandView.PREV_CLICKABLE]!!.visible = false

        RenderPalette.returnClick = null

        val startingPoint = Point(512.0, 512.0)

        val leaf = Leaf(topHeight = 4, position = startingPoint)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (line in leaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(ForeColors[1], StrokeInfo(thickness = 3.0)) {

                for (listLeaf in leaf.getList() ) {
                    circle(listLeaf.position, radius = 5.0)
                }
            }
        }

        commandViews[CommandView.LOADING_TEXT]!!.visible = false

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderAddGraftLeafStationary(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LOADING_TEXT]!!.visible = true

        commandViews[CommandView.LABEL_TEXT].setText("renderAddGraftLeafStationary() [v0.2]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing firstLeaf.getList().add(secondLeaf) and firstLeaf.getList().add(secondLeaf)")
        commandViews[CommandView.PREV_CLICKABLE]!!.visible = true

        RenderPalette.returnClick = null

        val firstStartingPoint = Point(64.0, 64.0)
        val secondStartingPoint = Point(256.0, 256.0)

        val thirdStartingPoint = Point(512.0, 512.0)
        val fourthStartingPoint = Point(768.0, 768.0)

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

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (line in firstLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(ForeColors[1], StrokeInfo(thickness = 3.0)) {

                for (leaf in firstLeaf.getList() ) {
                    circle(leaf.position, radius = 5.0)
                }
            }

            stroke(BackColors[5], StrokeInfo(thickness = 3.0)) {

                for (line in secondLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(ForeColors[5], StrokeInfo(thickness = 3.0)) {

                for (leaf in secondLeaf.getList() ) {
                    circle(leaf.position, radius = 5.0)
                }
            }

            stroke(BackColors[2], StrokeInfo(thickness = 3.0)) {

                for (line in thirdLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(ForeColors[2], StrokeInfo(thickness = 3.0)) {

                for (leaf in thirdLeaf.getList() ) {
                    circle(leaf.position, radius = 5.0)
                }
            }

            stroke(BackColors[6], StrokeInfo(thickness = 3.0)) {

                for (line in fourthLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(ForeColors[6], StrokeInfo(thickness = 3.0)) {

                for (leaf in fourthLeaf.getList() ) {
                    circle(leaf.position, radius = 5.0)
                }
            }
        }

        commandViews[CommandView.LOADING_TEXT]!!.visible = false

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderPruneLeaf(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LOADING_TEXT]!!.visible = true

        commandViews[CommandView.LABEL_TEXT].setText("renderPruneLeaf() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing leaf.getList().prune()")
        RenderPalette.returnClick = null

        val startingPoint = Point(512.0, 512.0)

        val positionOffset = Point(-100, -300)

        val leaf = Leaf(topHeight = 8, position = startingPoint)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (line in leaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(ForeColors[1], StrokeInfo(thickness = 3.0)) {

                for (listLeaf in leaf.getList() ) {
                    circle(listLeaf.position, radius = 5.0)
                }
            }

            val pruneLeaf = leaf.getList().prune()

            stroke(BackColors[0], StrokeInfo(thickness = 3.0)) {

                for (line in pruneLeaf.getLineList() ) {
                    if (line != null) line(line.first + positionOffset, line.second + positionOffset)
                }
            }
            stroke(ForeColors[0], StrokeInfo(thickness = 3.0)) {

                for (listLeaf in pruneLeaf.getList() ) {
                    circle(listLeaf.position + positionOffset, radius = 5.0)
                }
            }
        }

        commandViews[CommandView.LOADING_TEXT]!!.visible = false

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderLeafAngled(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LOADING_TEXT]!!.visible = true

        commandViews[CommandView.LABEL_TEXT].setText("renderLeafAngled() [v0.2]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing Leaf() at various angleFromParents")
        RenderPalette.returnClick = null

        val startingPoint = Point(512.0, 974.0)

        val startingMap = mapOf(
            90 to startingPoint
            , 45 to Point(512 - 462 * sin(Angle.fromDegrees(45)), 512 + 462 * sin(Angle.fromDegrees(45)) )
            , 0 to Point(50.0, 512.0)
            , 315 to Point(512 - 462 * sin(Angle.fromDegrees(45)), 512 - 462 * sin(Angle.fromDegrees(45)) )
            , 270 to Point(512.0, 50.0)
            , 225 to Point(512 + 462 * sin(Angle.fromDegrees(45)), 512 - 462 * sin(Angle.fromDegrees(45)) )
            , 180 to Point(974.0, 512.0)
            , 135 to Point(512 + 462 * sin(Angle.fromDegrees(45)), 512 + 462 * sin(Angle.fromDegrees(45)) )
        )

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            startingMap.forEach {

            val leaf = Leaf(topHeight = 4, angleFromParent = Angle.fromDegrees(it.key), position = it.value )

//                println ("tree: ${Angle.fromDegrees(it.key)}, ${it.value}")


                stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                    for (line in leaf.getLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                stroke(ForeColors[1], StrokeInfo(thickness = 3.0)) {

                    for (listLeaf in leaf.getList() ) {
                        circle(listLeaf.position, radius = 5.0)
                    }
                }
            }
        }

        commandViews[CommandView.LOADING_TEXT]!!.visible = false

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderLeafCircle(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LOADING_TEXT]!!.visible = true

        commandViews[CommandView.LABEL_TEXT].setText("renderLeafCircle() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing Leaf() with circling algorithm")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        RenderPalette.returnClick = null

        val centerPoint = Point(512.0, 512.0)

        val leafHeight = 5

        val leafPoints = leafHeight + 1

        val leafMap = mutableMapOf<Angle, Point>()

        (0 until leafPoints).toList().forEach{ leafIndex ->
            val angleOnCircle = Angle.fromDegrees( 360 / leafPoints * leafIndex ).normalized

            //angleInMap points back to the center of the circle
            leafMap[(Angle.fromDegrees(180) + angleOnCircle).normalized] = ILeaf.getChildPosition(centerPoint, (leafHeight - 2) * NextDistancePx, angleOnCircle)
        }

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            leafMap.forEach {

                val leaf = Leaf(topHeight = leafHeight, topAngle = it.key, angleFromParent = it.key, position = it.value )

    //                println ("tree: ${it.key.degrees}, ${it.value}")

                val finalPoints = mutableListOf<Point>()


                stroke(BackColors[2], StrokeInfo(thickness = 1.0)) {

                    line(it.value, centerPoint)
                }
                
                stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                    for (line in leaf.getLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                stroke(ForeColors[1], StrokeInfo(thickness = 3.0)) {

                    val leafList = leaf.getList()
                    val leafListSize = leafList.size

                    leafList.forEachIndexed { leafIndex, listLeaf ->
                        circle(listLeaf.position, radius = 5.0)

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
        commandViews[CommandView.LOADING_TEXT]!!.visible = false

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }

    @ExperimentalUnsignedTypes
    suspend fun renderBorderingLeaf(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LOADING_TEXT]!!.visible = true

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

            stroke(BackColors[1], StrokeInfo(thickness = 3.0)) {

                for (line in leaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(ForeColors[1], StrokeInfo(thickness = 3.0)) {

                for (listLeaf in leaf.getList() ) {
                    circle(listLeaf.position, radius = 5.0)
                }
            }
            stroke(BackColors[2], StrokeInfo(thickness = 3.0)) {

                for (line in firstBorderingLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
                for (line in secondBorderingLeaf.getLineList() ) {
                    if (line != null) line(line.first, line.second)
                }
            }
            stroke(ForeColors[2], StrokeInfo(thickness = 3.0)) {

                for (listLeaf in firstBorderingLeaf.getList() ) {
                    circle(listLeaf.position, radius = 5.0)
                }
                for (listLeaf in secondBorderingLeaf.getList() ) {
                    circle(listLeaf.position, radius = 5.0)
                }
            }
        }
        commandViews[CommandView.LOADING_TEXT]!!.visible = false

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }
}