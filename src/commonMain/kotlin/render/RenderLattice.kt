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
import lattice.ILattice.Companion.nodeMesh
import lattice.RoundedLattice
import leaf.ILeaf
import leaf.ILeaf.Companion.nodeMesh
import leaf.Lace
import leaf.Leaf
import leaf.Line.Companion.borderLines
import node.INodeMesh
import node.INodeMesh.Companion.getBorderingMesh
import node.Node
import node.NodeLink
import node.NodeMesh

object RenderLattice {

    @ExperimentalUnsignedTypes
    suspend fun renderLattice(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        var funIdx = 3
        val funSize = 4

        while ( (funIdx >= 0) && (funIdx < funSize) ) {
//            println ("funMapIdx : $funIdx")
            commandViews[CommandView.NODE_UUID_TEXT].setText(CommandView.NODE_UUID_TEXT.label())
            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(CommandView.NODE_DESCRIPTION_TEXT.label())

            when (funIdx) {
                0 -> if ( renderArrayLatticeHeights(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                1 -> if ( renderRoundedLatticeHeights(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                2 -> if ( renderArrayLatticeBordering(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
                3 -> if ( renderRoundedLatticeBordering(renderContainer, commandViews) == ButtonCommand.NEXT ) funIdx++ else funIdx--
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
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing RoundedLattice() at various heights")
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
    @ExperimentalUnsignedTypes
    suspend fun renderArrayLatticeBordering(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderArrayLatticeBordering() [v0.4]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing getBorderingMesh()")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("Original ArrayLattice NodeMesh (bg) and bordering ArrayLattice NodeMesh (fg) shown")
        RenderPalette.returnClick = null

        val caseTopHeight = 5
        val refNodeMeshCases = listOf(
            Lace(topHeight = caseTopHeight, position = Point(300, 150), topAngle = Angle.fromDegrees(225) ).getList().nodeMesh()
            , Lace(topHeight = caseTopHeight, position = Point(300, 450), topAngle = Angle.fromDegrees(225) ).getList().nodeMesh()
            , Lace(topHeight = caseTopHeight, position = Point(300, 750), topAngle = Angle.fromDegrees(225) ).getList().nodeMesh()
            , Lace(topHeight = caseTopHeight, position = Point(800, 150), topAngle = Angle.fromDegrees(225) ).getList().nodeMesh()
            , Lace(topHeight = caseTopHeight, position = Point(800, 450), topAngle = Angle.fromDegrees(225) ).getList().nodeMesh()
            , Lace(topHeight = caseTopHeight, position = Point(800, 750), topAngle = Angle.fromDegrees(225) ).getList().nodeMesh()
        )

        val latticeTopHeight = 7
        val borderingArrayLatticeCases = listOf(
            ArrayLattice(topHeight = latticeTopHeight, position = Point(100, 150), topAngle = Angle.fromDegrees(315) ).getList().nodeMesh()
            , ArrayLattice(topHeight = latticeTopHeight, position = Point(100, 450), topAngle = Angle.fromDegrees(315) ).getList().nodeMesh()
            , ArrayLattice(topHeight = latticeTopHeight, position = Point(100, 750), topAngle = Angle.fromDegrees(315) ).getList().nodeMesh()
            , ArrayLattice(topHeight = latticeTopHeight, position = Point(600, 150), topAngle = Angle.fromDegrees(315) ).getList().nodeMesh()
            , ArrayLattice(topHeight = latticeTopHeight, position = Point(600, 450), topAngle = Angle.fromDegrees(315) ).getList().nodeMesh()
            , ArrayLattice(topHeight = latticeTopHeight, position = Point(600, 750), topAngle = Angle.fromDegrees(315) ).getList().nodeMesh()
        )

        val textOffsetPosition = Point(0, -30)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            (0..5).forEach { idx ->
                secondContainer.text(text= "Test Lace $idx (height=$caseTopHeight)", color = RenderPalette.ForeColors[idx % RenderPalette.BackColors.size], alignment = RenderPalette.TextAlignCenter).position(refNodeMeshCases[idx].nodes[0].position + textOffsetPosition)
                secondContainer.text(text= "ArrayLattice(height=$latticeTopHeight)", color = RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size], alignment = RenderPalette.TextAlignCenter).position(borderingArrayLatticeCases[idx].nodes[0].position + textOffsetPosition)

                stroke(RenderPalette.BackColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in borderingArrayLatticeCases[idx].getNodeLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (listLeaf in borderingArrayLatticeCases[idx].nodes) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = RenderPalette.BackColors[idx]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                        }
                    }
                }

                stroke(RenderPalette.BackColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in refNodeMeshCases[idx].getNodeLineList() ) {
                        if (line != null) {
                            line(line.first, line.second)

/*                            val minBorderLines = line.borderLines((ILeaf.NextDistancePx * 0.2).toInt())

                            for (minBorderLine in minBorderLines) {
                                line(minBorderLine.first, minBorderLine.second)
                            }
  */                      }
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
                        }
                    }
                }

                val borderingMesh = borderingArrayLatticeCases[idx].getBorderingMesh(refNodeMeshCases[idx])

                stroke(RenderPalette.ForeColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in borderingMesh.getNodeLineList()) {
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
    suspend fun renderRoundedLatticeBordering(renderContainer : Container, commandViews: Map<CommandView, View>) : ButtonCommand {

        commandViews[CommandView.LABEL_TEXT].setText("renderRoundedLatticeBordering() [v0.4]")
        commandViews[CommandView.DESCRIPTION_TEXT].setText("testing getBorderingMesh()")
        commandViews[CommandView.COMMENT_TEXT]!!.visible = true
        commandViews[CommandView.COMMENT_TEXT].setText("Original RoundedLattice NodeMesh (bg) and bordering RoundedLattice NodeMesh (fg) shown")
        RenderPalette.returnClick = null

        val caseTopHeight = 3
        val refNodeMeshCases = listOf(
            Leaf(topHeight = caseTopHeight, position = Point(300, 150), topAngle = Angle.fromDegrees(225) ).getList().nodeMesh()
            , Leaf(topHeight = caseTopHeight, position = Point(300, 450), topAngle = Angle.fromDegrees(225) ).getList().nodeMesh()
            , Leaf(topHeight = caseTopHeight, position = Point(300, 750), topAngle = Angle.fromDegrees(225) ).getList().nodeMesh()
            , Leaf(topHeight = caseTopHeight, position = Point(800, 150), topAngle = Angle.fromDegrees(225) ).getList().nodeMesh()
            , Leaf(topHeight = caseTopHeight, position = Point(800, 450), topAngle = Angle.fromDegrees(225) ).getList().nodeMesh()
            , Leaf(topHeight = caseTopHeight, position = Point(800, 750), topAngle = Angle.fromDegrees(225) ).getList().nodeMesh()
        )

        val latticeTopHeight = 7
        val borderingArrayLatticeCases = listOf(
            RoundedLattice(topHeight = latticeTopHeight, position = Point(100, 150), topAngle = Angle.fromDegrees(315) ).getList().nodeMesh()
            , RoundedLattice(topHeight = latticeTopHeight, position = Point(100, 450), topAngle = Angle.fromDegrees(315) ).getList().nodeMesh()
            , RoundedLattice(topHeight = latticeTopHeight, position = Point(100, 750), topAngle = Angle.fromDegrees(315) ).getList().nodeMesh()
            , RoundedLattice(topHeight = latticeTopHeight, position = Point(600, 150), topAngle = Angle.fromDegrees(315) ).getList().nodeMesh()
            , RoundedLattice(topHeight = latticeTopHeight, position = Point(600, 450), topAngle = Angle.fromDegrees(315) ).getList().nodeMesh()
            , RoundedLattice(topHeight = latticeTopHeight, position = Point(600, 750), topAngle = Angle.fromDegrees(315) ).getList().nodeMesh()
        )

        val textOffsetPosition = Point(0, -30)

        val secondContainer = renderContainer.container()
        secondContainer.graphics {

            (0..5).forEach { idx ->
                secondContainer.text(text= "Test Leaf $idx (height=$caseTopHeight)", color = RenderPalette.ForeColors[idx % RenderPalette.BackColors.size], alignment = RenderPalette.TextAlignCenter).position(refNodeMeshCases[idx].nodes[0].position + textOffsetPosition)
                secondContainer.text(text= "RoundedLattice(height=$latticeTopHeight)", color = RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size], alignment = RenderPalette.TextAlignCenter).position(borderingArrayLatticeCases[idx].nodes[0].position + textOffsetPosition)

                stroke(RenderPalette.BackColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in borderingArrayLatticeCases[idx].getNodeLineList()) {
                        if (line != null) line(line.first, line.second)
                    }
                }

                for (listLeaf in borderingArrayLatticeCases[idx].nodes) {
                    secondContainer.circle { position(listLeaf.position)
                        radius = 5.0
                        color = RenderPalette.BackColors[idx]
                        strokeThickness = 3.0
                        onClick {
                            commandViews[CommandView.NODE_UUID_TEXT].setText(listLeaf.uuid.toString())
                            commandViews[CommandView.NODE_DESCRIPTION_TEXT].setText(listLeaf.description)
                        }
                    }
                }

                stroke(RenderPalette.BackColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in refNodeMeshCases[idx].getNodeLineList() ) {
                        if (line != null) {
                            line(line.first, line.second)

/*                            val minBorderLines = line.borderLines((ILeaf.NextDistancePx * 0.2).toInt())

                            for (minBorderLine in minBorderLines) {
                                line(minBorderLine.first, minBorderLine.second)
                            }
  */                      }
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
                        }
                    }
                }

                val borderingMesh = borderingArrayLatticeCases[idx].getBorderingMesh(refNodeMeshCases[idx])

                stroke(RenderPalette.ForeColors[idx], StrokeInfo(thickness = 3.0)) {

                    for (line in borderingMesh.getNodeLineList()) {
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