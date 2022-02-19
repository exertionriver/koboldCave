package org.river.exertion.demos.geom.nodeRoom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.geom.node.nodeMesh.NodeRoom
import org.river.exertion.geom.node.nodeMesh.NodeRoomAttributes
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsAndPath
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.render
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.renderWallsAndPath
import org.river.exertion.RenderPalette.BackColors
import org.river.exertion.RenderPalette.ForeColors

class DemoNodeRoomHeightScreen(private val batch: Batch,
                               private val font: BitmapFont,
                               private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    val attributes = NodeRoomAttributes()
    var toggleWalls = false
    var rebuildNodeRooms = false
    var rerenderPathsAndWalls = false
    var minHeight = 2
    var centerPointList = List(5) { idx -> Point((3 - idx) * horizOffset * 3f + horizOffset * 2.5f, idx * vertOffset * 2 + vertOffset * 2) }

    var nodeRoomList = List(5) { nodeRoomIdx -> NodeRoom(attributes.apply { this.geomHeight = nodeRoomIdx + minHeight }, centerPointList[nodeRoomIdx]) }

    val nodeRoomMeshList = MutableList(5) { nodeRoomMeshIdx -> NodeRoomMesh(nodeRoomList[nodeRoomMeshIdx]) }

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.projectionMatrix = camera.combined
        camera.update()

        InputHandler.handleInput(camera)
        Gdx.input.inputProcessor = InputProcessorHandler(camera, nodeRoomList.reduce { allRooms, nodeRoom -> nodeRoom + allRooms }.nodes)

        when {
            Gdx.input.isKeyJustPressed(Input.Keys.T) -> { if (attributes.circleNoise < 100) attributes.circleNoise += 10; rebuildNodeRooms = true }
            Gdx.input.isKeyJustPressed(Input.Keys.Y) -> { if (attributes.angleNoise < 100) attributes.angleNoise += 10; rebuildNodeRooms = true }
            Gdx.input.isKeyJustPressed(Input.Keys.U) -> { if (attributes.heightNoise < 100) attributes.heightNoise += 10; rebuildNodeRooms = true }
            Gdx.input.isKeyJustPressed(Input.Keys.G) -> { if (attributes.circleNoise > 0) attributes.circleNoise -= 10; rebuildNodeRooms = true }
            Gdx.input.isKeyJustPressed(Input.Keys.H) -> { if (attributes.angleNoise > 0) attributes.angleNoise -= 10; rebuildNodeRooms = true }
            Gdx.input.isKeyJustPressed(Input.Keys.J) -> { if (attributes.heightNoise > 0) attributes.heightNoise -= 10; rebuildNodeRooms = true }
            Gdx.input.isKeyJustPressed(Input.Keys.EQUALS) -> { if (minHeight < 3) minHeight++; rebuildNodeRooms = true }
            Gdx.input.isKeyJustPressed(Input.Keys.MINUS) -> { if (minHeight > 0) minHeight--; rebuildNodeRooms = true }
            Gdx.input.isKeyJustPressed(Input.Keys.N) -> { if (attributes.geomType.ordinal == NodeRoomAttributes.GeomType.values().size - 1) attributes.geomType = NodeRoomAttributes.GeomType.values()[0] else attributes.geomType = NodeRoomAttributes.GeomType.values()[attributes.geomType.ordinal+1]; rebuildNodeRooms = true }
            Gdx.input.isKeyJustPressed(Input.Keys.M) -> { if (attributes.geomStyle.ordinal == NodeRoomAttributes.GeomStyle.values().size - 1) attributes.geomStyle = NodeRoomAttributes.GeomStyle.values()[0] else attributes.geomStyle = NodeRoomAttributes.GeomStyle.values()[attributes.geomStyle.ordinal+1]; rebuildNodeRooms = true }
            Gdx.input.isKeyJustPressed(Input.Keys.B) -> { toggleWalls = !toggleWalls; rerenderPathsAndWalls = true }
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE) -> { rebuildNodeRooms = true }

            Gdx.input.isKeyJustPressed(Input.Keys.I) -> { if (attributes.pathThickness < .5) attributes.pathThickness += 0.05f ; rerenderPathsAndWalls = true }
            Gdx.input.isKeyJustPressed(Input.Keys.O) -> { if (attributes.pathThickness > .1) attributes.pathThickness -= 0.05f ; rerenderPathsAndWalls = true }
            Gdx.input.isKeyJustPressed(Input.Keys.K) -> { if (attributes.centerToEdgeThicknessVariance < .5) attributes.pathThickness += 0.05f ; rerenderPathsAndWalls = true }
            Gdx.input.isKeyJustPressed(Input.Keys.L) -> { if (attributes.centerToEdgeThicknessVariance > .1) attributes.pathThickness -= 0.05f ; rerenderPathsAndWalls = true }
        }

        if (rebuildNodeRooms) {
            nodeRoomList = List(5) { nodeRoomIdx -> NodeRoom(attributes.apply { this.geomHeight = nodeRoomIdx + minHeight }, centerPointList[nodeRoomIdx]) }
            rerenderPathsAndWalls = true
            rebuildNodeRooms = false
        }

        batch.use {

            nodeRoomList.reversed().forEachIndexed { nodeRoomIdx, nodeRoom ->
                font.drawLabel(it, Point(3f * nodeRoomIdx * horizOffset, labelVert.y), "NodeRoom (${nodeRoom.attributes.geomType}, ${nodeRoom.attributes.geomStyle})\n" +
                        "(@=${nodeRoom.centroid.position.x}, ${nodeRoom.centroid.position.y} height=${nodeRoom.attributes.geomHeight})\n" +
                        "(nodes=${nodeRoom.nodes.size}, exits=${nodeRoom.getExitNodes().size})\n" +
                        "(circleNoise:${nodeRoom.attributes.circleNoise})\n" +
                        "(angleNoise:${nodeRoom.attributes.angleNoise})\n" +
                        "(heightNoise:${nodeRoom.attributes.heightNoise})", ForeColors[nodeRoomIdx % ForeColors.size])

                if (toggleWalls) {
                    if (rerenderPathsAndWalls) {
                        nodeRoomList.forEachIndexed { idx, nodeRoom ->
                            nodeRoom.attributes.pathThickness = attributes.pathThickness
                            nodeRoom.attributes.centerToEdgeThicknessVariance = attributes.centerToEdgeThicknessVariance
                            nodeRoomMeshList[idx] = NodeRoomMesh(nodeRoom)
                            nodeRoomMeshList[idx].buildWallsAndPath()
                            nodeRoomMeshList[idx].renderWallsAndPath()
                        }
                        rerenderPathsAndWalls = false
                    }

                    nodeRoomMeshList.forEach { it.render(batch) }
                } else {
                    nodeRoom.getLineSet().forEach { line ->
                        drawer.line(line.first, line.second,BackColors[nodeRoomIdx % BackColors.size], 2F )
                    }

                    nodeRoom.nodes.forEach { node ->
                        drawer.filledCircle(node.position, 2F, ForeColors[nodeRoomIdx % ForeColors.size])
                    }

                    drawer.filledCircle(nodeRoom.centroid.position, 6F, ForeColors[nodeRoomIdx % ForeColors.size])

                    nodeRoom.getExitNodes().forEachIndexed { index, exitNode ->
                        drawer.filledCircle(exitNode.position, 4F, ForeColors[nodeRoomIdx % ForeColors.size])
                    }
                }
            }

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