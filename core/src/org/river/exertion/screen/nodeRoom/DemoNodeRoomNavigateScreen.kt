package org.river.exertion.screen.nodeRoom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.addNodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNodeLinks
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom.Companion.buildWalls
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom.Companion.buildWallsLos
import org.river.exertion.screen.RenderPalette.BackColors
import org.river.exertion.screen.RenderPalette.FadeBackColors
import org.river.exertion.screen.RenderPalette.ForeColors

class DemoNodeRoomNavigateScreen(private val batch: Batch,
                                 private val font: BitmapFont,
                                 private val assets: AssetManager,
                                 private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    var nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f))

    lateinit var forwardNextNodeAngle : Pair<Node, Angle>
    lateinit var backwardNextNodeAngle : Pair<Node, Angle>
    var leftNextAngle : Angle = 0f
    var rightNextAngle : Angle = 0f
    var currentNode = nodeRoom.getRandomNode()
    var currentAngle = nodeRoom.getRandomNextNodeAngle(currentNode)
    val visualRadius = NextDistancePx * 1.5f

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    override fun render(delta: Float) {

        batch.projectionMatrix = camera.combined
        camera.update()

        batch.use {

            val nodeRoomIdx = 1

            font.drawLabel(it, Point(nodeRoom.centroid.position.x, labelVert.y), "NodeRoom (nodes=${nodeRoom.nodes.size}, exits=${nodeRoom.getExitNodes().size})\n" +
                    "(circleNoise:${nodeRoom.attributes.circleNoise})\n" +
                    "(angleNoise:${nodeRoom.attributes.angleNoise})\n" +
                    "(heightNoise:${nodeRoom.attributes.heightNoise})", ForeColors[nodeRoomIdx % ForeColors.size])

            nodeRoom.pastWall.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.5F, FadeBackColors[nodeRoomIdx % BackColors.size])
            }

            nodeRoom.pastWallFade.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.3F, FadeBackColors[nodeRoomIdx % BackColors.size])
            }

            nodeRoom.currentWall.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.5F, BackColors[nodeRoomIdx % BackColors.size])
            }

            nodeRoom.currentWallFade.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.3F, BackColors[nodeRoomIdx % BackColors.size])
            }

            nodeRoom.getExitNodes().forEachIndexed { index, exitNode ->
                drawer.filledCircle(exitNode.position, 4F, ForeColors[nodeRoomIdx % ForeColors.size])
            }

/*            nodeRoom.nodes.forEachIndexed { index, exitNode ->
                drawer.filledCircle(exitNode.position, 1F, ForeColors[nodeRoomIdx + 1 % ForeColors.size])
            }
*/
            val ego : com.badlogic.gdx.utils.Array<Vector2> = com.badlogic.gdx.utils.Array()

            ego.add(currentNode.position, currentNode.position.getPositionByDistanceAndAngle(5f, currentAngle))

            drawer.path(ego, 1f, true)

            InputHandler.handleInput(camera)

            when {
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE) -> {
                    nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f))
                    currentNode = nodeRoom.getRandomNode()
//                    nodeRoom.buildWalls(currentNode.position, 30f)
                    nodeRoom.buildWallsLos(currentNode.position, currentAngle, visualRadius)

                    println("nodeRoomWallsSize : ${nodeRoom.currentWall.size}")

                    forwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, currentAngle)
//            println("checking backward nodeAngle:")
                    backwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, (180f + currentAngle).normalizeDeg())
//            println("checking leftward angle:")
                    leftNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.LEFT)
//            println("checking rightward angle:")
                    rightNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.RIGHT)

                    println("position: ${currentNode.position}")
                    println("angle: $currentAngle")
                }
                Gdx.input.isKeyJustPressed(Input.Keys.ENTER) -> {
                    nodeRoom.buildWalls()

                    println("position: ${currentNode.position}")
                    println("angle: $currentAngle")
                }
                Gdx.input.isKeyJustPressed(Input.Keys.UP) -> {
                    currentNode = forwardNextNodeAngle.first

                    println ("inactivedExitNodes: ${nodeRoom.inactiveExitNodesInRange(currentNode)}")
                    println ("activatedExitNodes: ${nodeRoom.activatedExitNodes}")
                    println ("child nodes: ${currentNode.getNodeChildren(nodeRoom.nodes, nodeRoom.nodeLinks)} nodelinks: ${nodeRoom.nodeLinks.getNodeLinks(currentNode.uuid)}")

                    currentAngle = forwardNextNodeAngle.second
//                    nodeRoom.buildWalls(currentNode.position, 30f)
                    nodeRoom.buildWallsLos(currentNode.position, currentAngle, visualRadius)

                    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, currentAngle)
                    //                println("checking backward nodeAngle:")
                    backwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, (180f + currentAngle).normalizeDeg())
                    //                println("checking leftward angle:")
                    leftNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.LEFT )
                    //                println("checking rightward angle:")
                    rightNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

                    println("position: ${currentNode.position}")
                    println("angle: $currentAngle")
                    println("currentWall size:${nodeRoom.currentWall.size}")
                    println("currentWallFade size:${nodeRoom.currentWallFade.size}")
                    println("pastWall size:${nodeRoom.pastWall.size}")
                    println("pastWallFade size:${nodeRoom.pastWallFade.size}")
                }
                Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> {
                    currentNode = backwardNextNodeAngle.first

                    println ("inactivedExitNodes: ${nodeRoom.inactiveExitNodesInRange(currentNode)}")
                    println ("activatedExitNodes: ${nodeRoom.activatedExitNodes}")

                    currentAngle = (180f + backwardNextNodeAngle.second).normalizeDeg()
                    //                    nodeRoom.buildWalls(currentNode.position, 30f)
                    nodeRoom.buildWallsLos(currentNode.position, currentAngle, visualRadius)

                    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, currentAngle)
                    //                println("checking backward nodeAngle:")
                    backwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, (180f + currentAngle).normalizeDeg())
                    //                println("checking leftward angle:")
                    leftNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.LEFT )
                    //                println("checking rightward angle:")
                    rightNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

                    println("position: ${currentNode.position}")
                    println("angle: $currentAngle")
                }
                Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> {
                    currentAngle = leftNextAngle
                    nodeRoom.buildWallsLos(currentNode.position, currentAngle, visualRadius)

                    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, currentAngle)
                    //                println("checking backward nodeAngle:")
                    backwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, (180f + currentAngle).normalizeDeg())
                    //                println("checking leftward angle:")
                    leftNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.LEFT )
                    //                println("checking rightward angle:")
                    rightNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

                    println("position: ${currentNode.position}")
                    println("angle: $currentAngle")
                }
                Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> {
                    currentAngle = rightNextAngle
                    nodeRoom.buildWallsLos(currentNode.position, currentAngle, visualRadius)

                    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, currentAngle)
                    //                println("checking backward nodeAngle:")
                    backwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, (180f + currentAngle).normalizeDeg())
                    //                println("checking leftward angle:")
                    leftNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.LEFT )
                    //                println("checking rightward angle:")
                    rightNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

                    println("position: ${currentNode.position}")
                    println("angle: $currentAngle")
                }
            }
        }
    }

    override fun hide() {
    }

    override fun show() {
        //                    nodeRoom.buildWalls(currentNode.position, 30f)
        nodeRoom.buildWallsLos(currentNode.position, currentAngle, visualRadius)
//        nodeRoom.buildWalls()
        println ("exitNodes: ${nodeRoom.inactiveExitNodesInRange(currentNode)}")

        //            println("checking forward nodeAngle:")
        forwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, currentAngle)
//            println("checking backward nodeAngle:")
        backwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, (180f + currentAngle).normalizeDeg())
//            println("checking leftward angle:")
        leftNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.LEFT )
//            println("checking rightward angle:")
        rightNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

        // start the playback of the background music when the screen is shown
/*        MusicAssets.values().forEach { assets.load(it) }
        assets.finishLoading()
        println("done!")
        assets[MusicAssets.NavajoNight].apply { isLooping = true }.play()
*/    }

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