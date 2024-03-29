package org.river.exertion.demos.geom.nodeMesh

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.geom.lattice.ArrayLattice
import org.river.exertion.geom.lattice.ILattice.Companion.nodeMesh
import org.river.exertion.geom.leaf.ILeaf.Companion.nodeMesh
import org.river.exertion.geom.leaf.Lace
import org.river.exertion.geom.leaf.Leaf
import org.river.exertion.geom.node.nodeMesh.INodeMesh.Companion.processMesh
import org.river.exertion.geom.node.nodeMesh.NodeMesh
import org.river.exertion.RenderPalette.BackColors
import org.river.exertion.RenderPalette.ForeColors

class DemoNodeMeshOperationsThirdScreen(private val batch: Batch,
                                        private val font: BitmapFont,
                                        private val camera: OrthographicCamera,
                                        private val stage: Stage) : KtxScreen {

    val horizOffset = KoboldCave.initViewportWidth / 11
    val vertOffset = KoboldCave.initViewportHeight / 11
    val labelOffset = Point(- KoboldCave.initViewportWidth / 8, KoboldCave.initViewportHeight / 32)

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
    val secondMesh = Lace(topHeight = meshHeight, position = locations[0], topAngle = 200f ).nodeMesh() +
            Lace(topHeight = meshHeight, position = locations[0], topAngle = 210f ).nodeMesh() +
            Lace(topHeight = meshHeight, position = locations[0], topAngle = 220f ).nodeMesh()
    val thirdMesh = ArrayLattice(topHeight = meshHeight, position = locations[0], topAngle = 330f ).nodeMesh()

    val nodeMeshes : Map<String, NodeMesh> = mapOf(
        "INodeMesh.processMesh()" to (firstMesh + secondMesh + thirdMesh).apply { processMesh() }
        , "absorbed" to (firstMesh + secondMesh + thirdMesh).apply { consolidateNearNodes() }.apply { linkNearNodes() }
        , "orphaned" to (firstMesh + secondMesh + thirdMesh).apply { consolidateNearNodes() }.apply { linkNearNodes() }.apply { nodifyIntersects() }
        , "edges" to (firstMesh + secondMesh + thirdMesh).apply { consolidateNearNodes() }.apply { linkNearNodes() }.apply { nodifyIntersects() }.apply { consolidateNearNodes() }
        , "bordering" to (firstMesh + secondMesh + thirdMesh).apply { consolidateNearNodes() }.apply { linkNearNodes() }.apply { nodifyIntersects() }.apply { consolidateNearNodes() }.apply { consolidateNodeLinks() }
    )

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.setProjectionMatrix(camera.combined)
        camera.update()

        batch.use {
            (0..4).forEach { nodeMeshIdx ->
                font.drawLabel(it, locations[nodeMeshIdx + 1] + labelOffset * 2, "NodeMeshes (height=$meshHeight)\n${nodeMeshes.keys.toList()[nodeMeshIdx]}", ForeColors[nodeMeshIdx % ForeColors.size])

                nodeMeshes.values.toList()[nodeMeshIdx].getLineSet().forEach { line ->
                    if (line != null) {
                        drawer.line(line.first + locations[nodeMeshIdx + 1], line.second + locations[nodeMeshIdx + 1], BackColors[nodeMeshIdx % BackColors.size], 2F )
                    }
                }

            }
            InputHandler.handleInput(camera)
        }

        stage.isDebugAll = true
        stage.draw()
    }

    override fun hide() {
    }

    override fun show() {

        (0..4).forEach { nodeMeshIdx ->
            nodeMeshes.values.toList()[nodeMeshIdx].nodes.forEachIndexed { index, listNode ->
                val node = Actor()
                node.name = "${listNode.description}_${nodeMeshIdx}_${listNode.uuid}"
                node.setPosition(listNode.position.x + locations[nodeMeshIdx + 1].x - 1, listNode.position.y + locations[nodeMeshIdx + 1].y - 1)
                node.setBounds(node.x, node.y, 3f, 3f)
                node.color = ForeColors[nodeMeshIdx % ForeColors.size]
                //drawer.filledCircle(listNode.position + locations[nodeMeshIdx + 1], 2F, ForeColors[nodeMeshIdx % ForeColors.size])

                node.onClick { println("clicked ${node.name} at (${node.x},${node.y})") }
                this.stage.addActor(node)
            }
        }

        Gdx.input.inputProcessor = this.stage

    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
        camera.viewportWidth = width.toFloat()
        camera.viewportHeight = height.toFloat()
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        sdc.disposeShapeDrawerConfig()
    }
}