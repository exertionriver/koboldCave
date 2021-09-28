package org.river.exertion.koboldCave.screen.nodeLine

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.koboldCave.node.nodeMesh.INodeMesh.Companion.setBordering
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine
import org.river.exertion.koboldCave.screen.RenderPalette.BackColors
import org.river.exertion.koboldCave.screen.RenderPalette.ForeColors

class DemoNodeLineBorderingScreen(private val batch: Batch,
                                  private val font: BitmapFont,
                                  private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVertOffset = Point(0F, Game.initViewportHeight / 32)

    val refNodesCases = listOf(
        listOf(Node(position = Point(horizOffset, vertOffset * 2)), Node(position = Point(horizOffset * 10, vertOffset * 3)))
        , listOf(Node(position = Point(horizOffset * 4, vertOffset)), Node(position = Point(horizOffset * 3, vertOffset * 10)))
        , listOf(Node(position = Point(horizOffset, vertOffset * 5)), Node(position = Point(horizOffset * 10, vertOffset * 6)))
        , listOf(Node(position = Point(horizOffset * 6, vertOffset)), Node(position = Point(horizOffset * 5, vertOffset * 10)))
        , listOf(Node(position = Point(horizOffset, vertOffset * 8)), Node(position = Point(horizOffset * 10, vertOffset * 9)))
        , listOf(Node(position = Point(horizOffset * 9, vertOffset)), Node(position = Point(horizOffset * 8, vertOffset * 10)))
    )

    val adjLineNoise = 90

    // build noisy three-line
    val nodeLineCases = refNodesCases.map { nodesCases: List<Node> ->
        NodeLine(firstNode = nodesCases[0], lastNode = nodesCases[1], lineNoise = adjLineNoise) +
            NodeLine(firstNode = nodesCases[0], lastNode = nodesCases[1], lineNoise = adjLineNoise) +
                NodeLine(firstNode = nodesCases[0], lastNode = nodesCases[1], lineNoise = adjLineNoise)
    }

    val borderingNodeLineCases = mutableListOf(
        NodeLine(copyNodeLine = nodeLineCases[0]).apply { this.setBordering(nodeLineCases[1] + nodeLineCases[5]) }
        , NodeLine(copyNodeLine = nodeLineCases[1]).apply { this.setBordering(nodeLineCases[2]) }
        , NodeLine(copyNodeLine = nodeLineCases[2]).apply { this.setBordering(nodeLineCases[3]) }
    ).apply list@ { ->
        this.add(NodeLine(copyNodeLine = nodeLineCases[3]).apply { this.setBordering(this@list[0] + nodeLineCases[4]) } )
        this.add(NodeLine(copyNodeLine = nodeLineCases[4]).apply { this.setBordering(this@list[1] + nodeLineCases[5]) } )
        this.add(NodeLine(copyNodeLine = nodeLineCases[5]).apply { this.setBordering(this@list[2] + nodeLineCases[0]) } )
    }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        batch.use {

            (0..5).forEachIndexed { nodeLineIdx, nodeLine ->
                font.drawLabel(it, refNodesCases[nodeLineIdx][0].position - labelVertOffset, "NodeLine Test Case $nodeLineIdx", ForeColors[nodeLineIdx % ForeColors.size])

                nodeLineCases[nodeLineIdx].getLineList().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,BackColors[nodeLineIdx % BackColors.size], 2F )
                    }
                }

                nodeLineCases[nodeLineIdx].nodes.forEachIndexed { index, listLeaf ->
                    drawer.filledCircle(listLeaf.position, 2F, BackColors[nodeLineIdx % BackColors.size])
                }

                borderingNodeLineCases[nodeLineIdx].getLineList().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first, line.second,ForeColors[nodeLineIdx % ForeColors.size], 2F )
                    }
                }

                borderingNodeLineCases[nodeLineIdx].nodes.forEachIndexed { index, listLeaf ->
                    drawer.filledCircle(listLeaf.position, 2F, ForeColors[nodeLineIdx % ForeColors.size])
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