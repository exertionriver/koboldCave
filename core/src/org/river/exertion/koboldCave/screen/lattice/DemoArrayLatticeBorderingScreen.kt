package org.river.exertion.koboldCave.screen.lattice

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.koboldCave.lattice.ArrayLattice
import org.river.exertion.koboldCave.lattice.ILattice.Companion.nodeMesh
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.nodeMesh
import org.river.exertion.koboldCave.leaf.Lace
import org.river.exertion.koboldCave.Line.Companion.borderLines
import org.river.exertion.koboldCave.node.nodeMesh.INodeMesh.Companion.setBordering
import org.river.exertion.koboldCave.node.nodeMesh.NodeMesh
import org.river.exertion.koboldCave.screen.RenderPalette

class DemoArrayLatticeBorderingScreen(private val batch: Batch,
                                      private val font: BitmapFont,
                                      private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 6
    val vertOffset = Game.initViewportHeight / 6
    val labelVertOffset = Point(0f, Game.initViewportHeight / 32)
    val leafHorizOffset = Point (150f, 0f)

    val vertPoints = listOf(Point(0f, vertOffset), Point(0f, vertOffset + 50f), Point(0f, vertOffset + 100f))
    val horizPoints = listOf(Point(horizOffset, 0f), Point(horizOffset + 50f, 0f), Point(horizOffset + 100f, 0f))

    val secondRowOffset = Point(0f, vertOffset * 2)
    val thirdRowOffset = Point(0f, vertOffset * 4)
    val secondColOffset = Point(horizOffset * 3, 0f)

    val caseTopHeight = 6
    val refLacesCases = listOf(
        Lace(topHeight = caseTopHeight, position = horizPoints[2] + vertPoints[1], topAngle = 215F).nodeMesh()
        , Lace(topHeight = caseTopHeight, position = horizPoints[2] + vertPoints[1] + secondRowOffset, topAngle = 215F).nodeMesh()
        , Lace(topHeight = caseTopHeight, position = horizPoints[2] + vertPoints[1] + thirdRowOffset, topAngle = 215F).nodeMesh()
        , Lace(topHeight = caseTopHeight, position = horizPoints[2] + vertPoints[1] + secondColOffset, topAngle = 215F).nodeMesh()
        , Lace(topHeight = caseTopHeight, position = horizPoints[2] + vertPoints[1] + secondColOffset + secondRowOffset, topAngle = 215F).nodeMesh()
        , Lace(topHeight = caseTopHeight, position = horizPoints[2] + vertPoints[1] + secondColOffset + thirdRowOffset, topAngle = 215F).nodeMesh()
    )

    val topHeight = 6
    val borderingCases = mutableListOf(
        ArrayLattice(topHeight = topHeight, position = refLacesCases[0].nodes[0].position - leafHorizOffset, topAngle = 315F ).nodeMesh()
        , ArrayLattice(topHeight = topHeight, position = refLacesCases[1].nodes[0].position - leafHorizOffset, topAngle = 315F ).nodeMesh()
        , ArrayLattice(topHeight = topHeight, position = refLacesCases[2].nodes[0].position - leafHorizOffset, topAngle = 315F ).nodeMesh()
        , ArrayLattice(topHeight = topHeight, position = refLacesCases[3].nodes[0].position - leafHorizOffset, topAngle = 315F ).nodeMesh()
        , ArrayLattice(topHeight = topHeight, position = refLacesCases[4].nodes[0].position - leafHorizOffset, topAngle = 315F ).nodeMesh()
        , ArrayLattice(topHeight = topHeight, position = refLacesCases[5].nodes[0].position - leafHorizOffset, topAngle = 315F ).nodeMesh()
    )

    val originalMesh = borderingCases.map { latticeCase -> NodeMesh(copyNodeMesh = latticeCase) }

    val borderingMesh = borderingCases.mapIndexed { idx : Int, latticeCase -> latticeCase.setBordering(refLacesCases[idx]) }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

     //   println("leaf.size: ${leaf.getList().size}, prunedLeaf.size: ${prunedLeaf.size}")

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        batch.use { batch ->

            (0..5).forEach { idx ->
                font.drawLabel(batch, refLacesCases[idx].nodes[0].position + labelVertOffset * 2
                    , "Test Case $idx", RenderPalette.BackColors[idx % RenderPalette.BackColors.size])
                font.drawLabel(batch, originalMesh[idx].nodes[0].position + labelVertOffset * 2
                    , "ArrayLattice (height=$topHeight)", RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size])

                originalMesh[idx].getLineList().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,
                            RenderPalette.BackColors[idx % RenderPalette.BackColors.size], 2F )
                    }
                }

                originalMesh[idx].nodes.forEach { listLeaf ->
                    drawer.filledCircle(listLeaf.position, 2F, RenderPalette.BackColors[idx % RenderPalette.BackColors.size])
                }

                for (line in refLacesCases[idx].getLineList() ) {
                    if (line != null) {
                        drawer.line(line.first, line.second,
                            RenderPalette.BackColors[idx % RenderPalette.BackColors.size], 2F )

                        val minBorderLines = line.borderLines((NextDistancePx * 0.2).toInt())

                        for (minBorderLine in minBorderLines) {
                            drawer.line(minBorderLine.first, minBorderLine.second,
                                RenderPalette.BackColors[idx % RenderPalette.BackColors.size], 2F )
                        }
                    }
                }

                for (listPoint in refLacesCases[idx].nodes ) {
                    drawer.filledCircle(listPoint.position, 2F, RenderPalette.BackColors[idx % RenderPalette.BackColors.size])
                }

                for (line in borderingMesh[idx].getLineList()) {
                    if (line != null) {
                        drawer.line(line.first, line.second,
                            RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size], 2F )
                    }
                }

                for (listLeaf in borderingMesh[idx].nodes) {
                    drawer.filledCircle(listLeaf.position, 2F, RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size])
                }
            }

            InputHandler.handleInput(camera)
        }

    }

    override fun hide() {
    }

    override fun show() {

    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
    }

    override fun dispose() {
        sdc.disposeShapeDrawerConfig()
    }
}