package org.river.exertion.koboldCave.screen.leaf

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.nodeMesh
import org.river.exertion.koboldCave.leaf.Leaf
import org.river.exertion.koboldCave.Line.Companion.borderLines
import org.river.exertion.koboldCave.node.nodeMesh.INodeMesh.Companion.setBordering
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.nodeMesh.NodeMesh
import org.river.exertion.koboldCave.screen.RenderPalette

class DemoLeafBorderingScreen(private val batch: Batch,
                              private val font: BitmapFont,
                              private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 6
    val vertOffset = Game.initViewportHeight / 6
    val labelVertOffset = Point(0f, Game.initViewportHeight / 32)
    val leafHorizOffset = Point (50f, 0f)

    val vertPoints = listOf(Point(0f, vertOffset), Point(0f, vertOffset + 50f), Point(0f, vertOffset + 100f))
    val horizPoints = listOf(Point(horizOffset, 0f), Point(horizOffset + 50f, 0f), Point(horizOffset + 100f, 0f))

    val secondRowOffset = Point(0f, vertOffset * 2)
    val thirdRowOffset = Point(0f, vertOffset * 4)
    val secondColOffset = Point(horizOffset * 3, 0f)

    val refNodesCases = listOf(
        listOf(Node(position = horizPoints[1] + vertPoints[0]), Node(position = horizPoints[2] + vertPoints[1]), Node(position = horizPoints[0] + vertPoints[2]))
        , listOf(Node(position = horizPoints[0] + vertPoints[0] + secondRowOffset), Node(position = horizPoints[2] + vertPoints[1] + secondRowOffset), Node(position = horizPoints[1] + vertPoints[2] + secondRowOffset))
        , listOf(Node(position = horizPoints[2] + vertPoints[0] + thirdRowOffset), Node(position = horizPoints[1] + vertPoints[1] + thirdRowOffset), Node(position = horizPoints[0] + vertPoints[2] + thirdRowOffset))
        , listOf(Node(position = horizPoints[2] + vertPoints[0] + secondColOffset), Node(position = horizPoints[0] + vertPoints[1] + secondColOffset), Node(position = horizPoints[1] + vertPoints[2] + secondColOffset))
        , listOf(Node(position = horizPoints[0] + vertPoints[0] + secondColOffset + secondRowOffset), Node(position = horizPoints[1] + vertPoints[1] + secondColOffset + secondRowOffset), Node(position = horizPoints[2] + vertPoints[2] + secondColOffset + secondRowOffset))
        , listOf(Node(position = horizPoints[1] + vertPoints[0] + secondColOffset + thirdRowOffset), Node(position = horizPoints[0] + vertPoints[1] + secondColOffset + thirdRowOffset), Node(position = horizPoints[2] + vertPoints[2] + secondColOffset + thirdRowOffset))
    )

    val refNodeLinksCases = refNodesCases.map { nodesCases: List<Node> ->
        listOf(NodeLink(firstNodeUuid = nodesCases[0].uuid, secondNodeUuid = nodesCases[1].uuid), NodeLink(firstNodeUuid = nodesCases[1].uuid, secondNodeUuid = nodesCases[2].uuid))
    }

    val refNodeMeshCases = refNodesCases.mapIndexed { idx : Int, nodesCases: List<Node> ->
        NodeMesh( nodes = nodesCases.toMutableList(), nodeLinks = refNodeLinksCases[idx].toMutableList())
    }

    val topHeight = 6
    val borderingCases = mutableListOf(
        Leaf(topHeight = topHeight, position = refNodesCases[0][1].position + leafHorizOffset, topAngle = 180F ).nodeMesh()
        , Leaf(topHeight = topHeight, position = refNodesCases[1][1].position + leafHorizOffset, topAngle = 180F ).nodeMesh()
        , Leaf(topHeight = topHeight, position = refNodesCases[2][1].position + leafHorizOffset, topAngle = 180F ).nodeMesh()
        , Leaf(topHeight = topHeight, position = refNodesCases[3][1].position + leafHorizOffset, topAngle = 180F ).nodeMesh()
        , Leaf(topHeight = topHeight, position = refNodesCases[4][1].position + leafHorizOffset, topAngle = 180F ).nodeMesh()
        , Leaf(topHeight = topHeight, position = refNodesCases[5][1].position + leafHorizOffset, topAngle = 180F ).nodeMesh()
    )

    val originalMesh = borderingCases.map { leafCase -> NodeMesh(leafCase) }

    val borderingMesh = borderingCases.mapIndexed { idx,  leafCase -> leafCase.setBordering(refNodeMeshCases[idx]) }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

     //   println("leaf.size: ${leaf.getList().size}, prunedLeaf.size: ${prunedLeaf.size}")

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        batch.use { batch ->

            (0..5).forEach { idx ->
                font.drawLabel(batch, refNodeMeshCases[idx].nodes[0].position - labelVertOffset
                    , "Test Case $idx", RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size])
                font.drawLabel(batch, originalMesh[idx].nodes[0].position + leafHorizOffset
                    , "Leaf(height=$topHeight)", RenderPalette.ForeColors[idx % RenderPalette.ForeColors.size])

                originalMesh[idx].getLineList().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,
                            RenderPalette.BackColors[idx % RenderPalette.BackColors.size], 2F )
                    }
                }

                originalMesh[idx].nodes.forEach { listLeaf ->
                    drawer.filledCircle(listLeaf.position, 2F, RenderPalette.BackColors[idx % RenderPalette.BackColors.size])
                }

                for (line in refNodeMeshCases[idx].getLineList() ) {
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

                for (listPoint in refNodeMeshCases[idx].nodes ) {
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