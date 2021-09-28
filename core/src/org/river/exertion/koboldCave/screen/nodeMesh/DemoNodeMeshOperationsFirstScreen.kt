package org.river.exertion.koboldCave.screen.nodeMesh

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.koboldCave.lattice.ArrayLattice
import org.river.exertion.koboldCave.lattice.ILattice.Companion.nodeMesh
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.nodeMesh
import org.river.exertion.koboldCave.leaf.Lace
import org.river.exertion.koboldCave.leaf.Leaf
import org.river.exertion.koboldCave.node.nodeMesh.NodeMesh
import org.river.exertion.koboldCave.screen.RenderPalette.BackColors
import org.river.exertion.koboldCave.screen.RenderPalette.ForeColors

class DemoNodeMeshOperationsFirstScreen(private val batch: Batch,
                                        private val font: BitmapFont,
                                        private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelOffset = Point(- Game.initViewportWidth / 8, Game.initViewportHeight / 32)

    val locations = listOf(
        Point(0f,0f)
        , Point(horizOffset * 5.5f, vertOffset * 5.5f)
        , Point(horizOffset * 2.5f, vertOffset * 8.5f)
        , Point(horizOffset * 8.5f, vertOffset * 8.5f)
        , Point(horizOffset * 2.5f, vertOffset * 2.5f)
        , Point(horizOffset * 8.5f, vertOffset * 2.5f)
    )

    val meshHeight = 6

    val firstMesh = Leaf(topHeight = meshHeight, position = locations[0], topAngle = 90f ).nodeMesh()
    val secondMesh = Lace(topHeight = meshHeight, position = locations[0], topAngle = 210f ).nodeMesh() +
            Lace(topHeight = meshHeight, position = locations[0], topAngle = 210f ).nodeMesh() +
            Lace(topHeight = meshHeight, position = locations[0], topAngle = 210f ).nodeMesh()
    val thirdMesh = ArrayLattice(topHeight = meshHeight, position = locations[0], topAngle = 330f ).nodeMesh()

    val nodeMeshes : Map<String, NodeMesh> = mapOf(
        "original\nINodeMesh.consolidateStackedNodes()" to firstMesh + secondMesh + thirdMesh
        , "linked\nINodeMesh.linkNearNodes()" to (firstMesh + secondMesh + thirdMesh).apply { this.linkNearNodes() }
        , "consolidated\nINodeMesh.consolidateNearNodes()" to (firstMesh + secondMesh + thirdMesh).apply { this.consolidateNearNodes() }
        , "nodified\nINodeMesh.nodifyIntersects()" to (firstMesh + secondMesh + thirdMesh).apply { this.nodifyIntersects() }
        , "nodelinks consolidated\nINodeMesh.consolidateNodeLinks()" to (firstMesh + secondMesh + thirdMesh).apply { this.consolidateNodeLinks() }
    )

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        batch.use {

            (0..4).forEach { nodeMeshIdx ->
                font.drawLabel(it, locations[nodeMeshIdx + 1] + labelOffset * 2, "NodeMeshes (height=$meshHeight)\n${nodeMeshes.keys.toList()[nodeMeshIdx]}", ForeColors[nodeMeshIdx % ForeColors.size])

                nodeMeshes.values.toList()[nodeMeshIdx].getLineList().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first + locations[nodeMeshIdx + 1], line.second + locations[nodeMeshIdx + 1], BackColors[nodeMeshIdx % BackColors.size], 2F )
                    }
                }

                nodeMeshes.values.toList()[nodeMeshIdx].nodes.forEachIndexed { index, listLeaf ->
                    drawer.filledCircle(listLeaf.position + locations[nodeMeshIdx + 1], 2F, ForeColors[nodeMeshIdx % ForeColors.size])
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