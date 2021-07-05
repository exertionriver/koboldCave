package render

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.line
import lattice.ArrayLattice
import lattice.ILattice.Companion.getLateralLineList
import lattice.RoundedLattice

object RenderLattice {

    @ExperimentalUnsignedTypes
    suspend fun renderLattice(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 0
        val funSize = 2

        while ( (funIdx >= 0) && (funIdx < funSize) ) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderArrayLatticeHeights(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                1 -> if ( renderRoundedLatticeHeights(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
//                2 -> if ( renderArrayLatticeBordering(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
//                3 -> if ( renderRoundedLatticeBordering(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
            }
        }

        return if (funIdx > 0) ButtonCommand.NEXT else ButtonCommand.PREV
    }

    @ExperimentalUnsignedTypes
    suspend fun renderArrayLatticeHeights(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderArrayLattice() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing ArrayLattice() at various heights")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        commandViews[CommandView.PREV_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val latticeList = List(8) { ArrayLattice(topHeight = (it * 2) + 1, topAngle = Angle.fromDegrees(250), position = Point((8 - it) * 100 + 100, (8 - it) * 100 + 100) ) }

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            latticeList.reversed().forEachIndexed { latticeIdx, lattice ->
                secondContainer.text(text= "ArrayLattice(height=${lattice.topHeight})", color = RenderPalette.ForeColors[latticeIdx % RenderPalette.ForeColors.size], alignment = RenderPalette.TextAlignCenter).position(Point(lattice.position.x, lattice.position.y - 30))

                stroke(RenderPalette.BackColors[latticeIdx % RenderPalette.BackColors.size], StrokeInfo(thickness = 3.0)) {

                    for (line in lattice.getLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }

                    for (line in lattice.getList().getLateralLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (listLeaf in lattice.getList() ) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = RenderPalette.ForeColors[latticeIdx % RenderPalette.ForeColors.size]
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
    suspend fun renderRoundedLatticeHeights(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderRoundedLattice() [v0.3]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing ReoundedLattice() at various heights")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = false
        commandViews[CommandView.PREV_BUTTON]!!.visible = true

        RenderPalette.returnClick = null

        val latticeList = List(8) { RoundedLattice(topHeight = (it * 2) + 1, topAngle = Angle.fromDegrees(250), position = Point((8 - it) * 100 + 100, (8 - it) * 100 + 100) ) }

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            latticeList.reversed().forEachIndexed { latticeIdx, lattice ->
                secondContainer.text(text= "RoundedLattice(height=${lattice.topHeight})", color = RenderPalette.ForeColors[latticeIdx % RenderPalette.ForeColors.size], alignment = RenderPalette.TextAlignCenter).position(Point(lattice.position.x, lattice.position.y - 30))

                stroke(RenderPalette.BackColors[latticeIdx % RenderPalette.BackColors.size], StrokeInfo(thickness = 3.0)) {

                    for (line in lattice.getLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }

                    for (line in lattice.getList().getLateralLineList() ) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (listLeaf in lattice.getList() ) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = RenderPalette.ForeColors[latticeIdx % RenderPalette.ForeColors.size]
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