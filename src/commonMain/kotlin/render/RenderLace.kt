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
import leaf.Lace
import kotlin.random.Random

object RenderLace {

    @ExperimentalUnsignedTypes
    suspend fun renderLace(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 0
        val funSize = 3

        while ( (funIdx >= 0) && (funIdx < funSize) ) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderLaceHeights(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                1 -> if ( renderLaceAngled(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                2 -> if ( renderLaceSpiral(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
//                3 -> if ( renderLaceBordering(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
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

        while (RenderPalette.returnClick == null) { delay(TimeSpan(100.0)) }

        secondContainer.removeChildren()

        return RenderPalette.returnClick as ButtonCommand
    }
}