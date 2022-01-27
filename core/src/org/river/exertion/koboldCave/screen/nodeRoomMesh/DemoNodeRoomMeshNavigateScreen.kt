package org.river.exertion.koboldCave.screen.nodeRoomMesh

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
import org.river.exertion.assets.MusicAssets
import org.river.exertion.assets.PlayerCharacter
import org.river.exertion.assets.get
import org.river.exertion.assets.load
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextNodeAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNodeLinks
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsAndPath
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh.Companion.renderWallsAndPathLos
import org.river.exertion.koboldCave.screen.RenderPalette
import org.river.exertion.koboldCave.screen.RenderPalette.BackColors
import org.river.exertion.koboldCave.screen.RenderPalette.FadeBackColors
import org.river.exertion.koboldCave.screen.RenderPalette.FadeForeColors
import org.river.exertion.koboldCave.screen.RenderPalette.ForeColors
import space.earlygrey.shapedrawer.JoinType

class DemoNodeRoomMeshNavigateScreen(private val batch: Batch,
                                     private val font: BitmapFont,
                                     private val assets: AssetManager,
                                     private val camera: OrthographicCamera) : KtxScreen {

    val horizOffset = Game.initViewportWidth / 11
    val vertOffset = Game.initViewportHeight / 11
    val labelVert = Point(0F, Game.initViewportHeight * 2 / 32)

    var nodeRoomMesh = NodeRoomMesh(NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f)))

    lateinit var forwardNextNodeAngle : Pair<Node, Angle>
    lateinit var backwardNextNodeAngle : Pair<Node, Angle>
    var leftNextAngle : Angle = 0f
    var rightNextAngle : Angle = 0f

    var forwardNextMoveCost : Float = 0f
    var backwardNextMoveCost : Float = 0f
    var leftNextMoveCost : Float = 0f
    var rightNextMoveCost : Float = 0f

    var shown = false

    var nodeRoomIdx = 0
    var currentRoom = nodeRoomMesh.nodeRooms[nodeRoomIdx]
    var currentNode = currentRoom.getRandomNode()
    var currentAngle = currentRoom.getRandomNextNodeAngle(currentNode)
    val visualRadius = NextDistancePx //* 1.5f

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    val arcSdc = ShapeDrawerConfig(batch, FadeForeColors[1])
    val arcDrawer = arcSdc.getDrawer()

    val arcFadeSdc = ShapeDrawerConfig(batch, FadeBackColors[1])
    val arcFadeDrawer = arcFadeSdc.getDrawer()

    var dirty = true
    override fun render(delta: Float) {

        batch.projectionMatrix = camera.combined
        camera.update()

        batch.use {

            var currentRoom = nodeRoomMesh.nodeRooms[nodeRoomIdx]

            font.drawLabel(it, Point(currentRoom.centroid.position.x, labelVert.y * 2)
                    , "NodeRoom (nodes=${currentRoom.nodes.size} idx=$nodeRoomIdx)\n" +
                        "roomDescription: ${currentRoom.description}\n" +
                        "exits: ${nodeRoomMesh.currentRoomExits} / ${nodeRoomMesh.maxRoomExits}\n" +
                        "current obstacle:${currentNode.attributes.nodeObstacle}\n" +
                        "dst:${currentNode.position.dst(forwardNextNodeAngle.first.position) / 3}, ${currentNode.position.dst(backwardNextNodeAngle.first.position) / 3}, $currentAngle, ${currentAngle.leftAngleBetween(leftNextAngle)}, ${currentAngle.rightAngleBetween(rightNextAngle)}\n" +
                        "move costs:$forwardNextMoveCost, $backwardNextMoveCost, $leftNextMoveCost, $rightNextMoveCost\n" +
                        "elevation:${currentNode.attributes.nodeElevation}, ${forwardNextNodeAngle.first.attributes.nodeElevation}, ${backwardNextNodeAngle.first.attributes.nodeElevation}\n" +
                        "slope: ${nodeRoomMesh.getSlope(currentNode, forwardNextNodeAngle.first)}, ${nodeRoomMesh.getSlope(currentNode, backwardNextNodeAngle.first)}"
                    , ForeColors[nodeRoomIdx % ForeColors.size])

            val renderIdx = 1

            nodeRoomMesh.pastStairs.entries.forEach { stairNode ->
                arcFadeDrawer.arc(stairNode.key.x, stairNode.key.y, 6F, (stairNode.value - 60f).radians(), 120f.radians() )
            }

            nodeRoomMesh.currentStairs.entries.forEach { stairNode ->
                arcDrawer.arc(stairNode.key.x, stairNode.key.y, 6F, (stairNode.value - 60f).radians(), 120f.radians() )
            }

            nodeRoomMesh.currentFloor.values.forEach { floorNode ->
                drawer.filledCircle(floorNode, 0.5F, RenderPalette.FadeForeColors[4 % BackColors.size])
            }

            nodeRoomMesh.pastFloor.values.forEach { floorNode ->
                drawer.filledCircle(floorNode, 0.5F, RenderPalette.FadeBackColors[4 % BackColors.size])
            }

            nodeRoomMesh.pastWall.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.5F, FadeBackColors[renderIdx % BackColors.size])
            }

            nodeRoomMesh.pastWallFade.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.3F, FadeBackColors[renderIdx % BackColors.size])
            }

            nodeRoomMesh.currentWall.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.5F, BackColors[renderIdx % BackColors.size])
            }

            nodeRoomMesh.currentWallFade.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.3F, BackColors[renderIdx % BackColors.size])
            }

            nodeRoomMesh.nodeRooms.forEachIndexed { idx, nodeRoom -> nodeRoom.getExitNodes().forEach { exitNode ->
                drawer.filledCircle(exitNode.position, 4F, FadeBackColors[idx % ForeColors.size])
            } }

            val ego : com.badlogic.gdx.utils.Array<Vector2> = com.badlogic.gdx.utils.Array()

            val bottomArrow = currentNode.position.getPositionByDistanceAndAngle(4f, (currentAngle + 180f).normalizeDeg())
            val topArrow = currentNode.position.getPositionByDistanceAndAngle(6f, currentAngle)
            val tipArrowLeft = topArrow.getPositionByDistanceAndAngle(3f, (currentAngle + 150f).normalizeDeg())
            val tipArrowRight = topArrow.getPositionByDistanceAndAngle(3f, (currentAngle - 150f).normalizeDeg())

            ego.add(bottomArrow, topArrow)
            ego.add(topArrow, tipArrowLeft)
            ego.add(tipArrowLeft, tipArrowRight)
            ego.add(tipArrowRight, topArrow)

            //may have to use another (colored) texture for ego-path
            drawer.path(ego, 1f, JoinType.SMOOTH, true)

            InputHandler.handleInput(camera)

            when {
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE) -> {
                    nodeRoomMesh = NodeRoomMesh(NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f)))

                    nodeRoomIdx = 0
                    currentRoom = nodeRoomMesh.nodeRooms[nodeRoomIdx]
                    currentNode = currentRoom.getRandomNode()
                    currentAngle = currentRoom.getRandomNextNodeAngle(currentNode)

//                    nodeRoom.buildWalls(currentNode.position, 30f)
//                    nodeRoomMesh.buildWallsLos(currentNode.position, currentAngle, visualRadius)

                    println("nodeRoomWallsSize : ${currentRoom.currentWall.size}")

                    forwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle)
//            println("checking backward nodeAngle:")
                    backwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
//            println("checking leftward angle:")
                    leftNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
//            println("checking rightward angle:")
                    rightNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

//                    nodeRoomMesh.buildFloorsLos(currentNode, forwardNextNodeAngle.first, currentAngle, visualRadius)

                    forwardNextMoveCost = currentNode.position.dst(forwardNextNodeAngle.first.position) / 3 + currentNode.position.dst(forwardNextNodeAngle.first.position) * ( currentNode.attributes.nodeObstacle.getChallenge() + forwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 200
                    backwardNextMoveCost = currentNode.position.dst(backwardNextNodeAngle.first.position) / 3 + currentNode.position.dst(backwardNextNodeAngle.first.position) * ( currentNode.attributes.nodeObstacle.getChallenge() + backwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 200
                    leftNextMoveCost = currentAngle.leftAngleBetween(leftNextAngle) / 60 + currentAngle.leftAngleBetween(leftNextAngle) / 120 * currentNode.attributes.nodeObstacle.getChallenge() / 100
                    rightNextMoveCost = currentAngle.rightAngleBetween(rightNextAngle) / 60 + currentAngle.rightAngleBetween(rightNextAngle) / 120 * currentNode.attributes.nodeObstacle.getChallenge() / 100

                    println("position: ${currentNode.position}")
                    println("angle: $currentAngle")

                    dirty = true
                }
                Gdx.input.isKeyJustPressed(Input.Keys.ENTER) -> {
//                    nodeRoomMesh.buildWalls()

                    println("position: ${currentNode.position}")
                    println("angle: $currentAngle")
                    dirty = true
                }
                Gdx.input.isKeyJustPressed(Input.Keys.UP) -> {
                    currentNode = forwardNextNodeAngle.first
                    currentAngle = forwardNextNodeAngle.second

                    nodeRoomIdx = nodeRoomMesh.getCurrentRoomIdx(currentNode)

                    nodeRoomMesh.inactiveExitNodesInRange(currentNode).forEach { nodeRoomMesh.activateExitNode( nodeRoomIdx, it ) }

                    println ("inactivedExitNodes: ${currentRoom.inactiveExitNodesInRange(currentNode)}")
                    println ("activatedExitNodes: ${currentRoom.activatedExitNodes}")
                    println ("child nodes: ${currentNode.getNodeChildren(currentRoom.nodes, currentRoom.nodeLinks)} nodelinks: ${currentRoom.nodeLinks.getNodeLinks(currentNode.uuid)}")

//                    nodeRoom.buildWalls(currentNode.position, 30f)
//                    nodeRoomMesh.buildWallsLos(currentNode.position, currentAngle, visualRadius)

                    forwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle)
//            println("checking backward nodeAngle:")
                    backwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
//            println("checking leftward angle:")
                    leftNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
//            println("checking rightward angle:")
                    rightNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

//                    nodeRoomMesh.buildFloorsLos(currentNode, forwardNextNodeAngle.first, currentAngle, visualRadius)

                    forwardNextMoveCost = currentNode.position.dst(forwardNextNodeAngle.first.position) / 3 + currentNode.position.dst(forwardNextNodeAngle.first.position) * ( currentNode.attributes.nodeObstacle.getChallenge() + forwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 200
                    backwardNextMoveCost = currentNode.position.dst(backwardNextNodeAngle.first.position) / 3 + currentNode.position.dst(backwardNextNodeAngle.first.position) * ( currentNode.attributes.nodeObstacle.getChallenge() + backwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 200
                    leftNextMoveCost = currentAngle.leftAngleBetween(leftNextAngle) / 60 + currentAngle.leftAngleBetween(leftNextAngle) / 120 * currentNode.attributes.nodeObstacle.getChallenge() / 100
                    rightNextMoveCost = currentAngle.rightAngleBetween(rightNextAngle) / 60 + currentAngle.rightAngleBetween(rightNextAngle) / 120 * currentNode.attributes.nodeObstacle.getChallenge() / 100

                    println("position: ${currentNode.position}")
                    println("angle: $currentAngle")
                    println("currentWall size:${currentRoom.currentWall.size}")
                    println("currentWallFade size:${currentRoom.currentWallFade.size}")
                    println("pastWall size:${currentRoom.pastWall.size}")
                    println("pastWallFade size:${currentRoom.pastWallFade.size}")

                    dirty = true
                }
                Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> {

                    //if backed into a corner, no 'backward' option
                    if (currentNode != backwardNextNodeAngle.first) {
                        currentNode = backwardNextNodeAngle.first
                        nodeRoomIdx = nodeRoomMesh.getCurrentRoomIdx(currentNode)

                        nodeRoomMesh.inactiveExitNodesInRange(currentNode).forEach { nodeRoomMesh.activateExitNode( nodeRoomIdx, it ) }

                        println ("inactivedExitNodes: ${currentRoom.inactiveExitNodesInRange(currentNode)}")
                        println ("activatedExitNodes: ${currentRoom.activatedExitNodes}")

                        currentAngle = (180f + backwardNextNodeAngle.second).normalizeDeg()
                        //                    nodeRoom.buildWalls(currentNode.position, 30f)
  //                      nodeRoomMesh.buildWallsLos(currentNode.position, currentAngle, visualRadius)

                        //                println("checking forward nodeAngle:")
                        forwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle)
//            println("checking backward nodeAngle:")
                        backwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
//            println("checking leftward angle:")
                        leftNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
//            println("checking rightward angle:")
                        rightNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

    //                    nodeRoomMesh.buildFloorsLos(currentNode, forwardNextNodeAngle.first, currentAngle, visualRadius)

                        forwardNextMoveCost = currentNode.position.dst(forwardNextNodeAngle.first.position) / 3 + currentNode.position.dst(forwardNextNodeAngle.first.position) * ( currentNode.attributes.nodeObstacle.getChallenge() + forwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 200
                        backwardNextMoveCost = currentNode.position.dst(backwardNextNodeAngle.first.position) / 3 + currentNode.position.dst(backwardNextNodeAngle.first.position) * ( currentNode.attributes.nodeObstacle.getChallenge() + backwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 200
                        leftNextMoveCost = currentAngle.leftAngleBetween(leftNextAngle) / 60 + currentAngle.leftAngleBetween(leftNextAngle) / 120 * currentNode.attributes.nodeObstacle.getChallenge() / 100
                        rightNextMoveCost = currentAngle.rightAngleBetween(rightNextAngle) / 60 + currentAngle.rightAngleBetween(rightNextAngle) / 120 * currentNode.attributes.nodeObstacle.getChallenge() / 100

                        println("position: ${currentNode.position}")
                        println("angle: $currentAngle")
                    }

                    dirty = true
                }
                Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> {
                    currentAngle = leftNextAngle
          //          nodeRoomMesh.buildWallsLos(currentNode.position, currentAngle, visualRadius)

                    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle)
//            println("checking backward nodeAngle:")
                    backwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
//            println("checking leftward angle:")
                    leftNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
//            println("checking rightward angle:")
                    rightNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

            //        nodeRoomMesh.buildFloorsLos(currentNode, forwardNextNodeAngle.first, currentAngle, visualRadius)

                    forwardNextMoveCost = currentNode.position.dst(forwardNextNodeAngle.first.position) / 3 + currentNode.position.dst(forwardNextNodeAngle.first.position) * ( currentNode.attributes.nodeObstacle.getChallenge() + forwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 200
                    backwardNextMoveCost = currentNode.position.dst(backwardNextNodeAngle.first.position) / 3 + currentNode.position.dst(backwardNextNodeAngle.first.position) * ( currentNode.attributes.nodeObstacle.getChallenge() + backwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 200
                    leftNextMoveCost = currentAngle.leftAngleBetween(leftNextAngle) / 60 + currentAngle.leftAngleBetween(leftNextAngle) / 120 * currentNode.attributes.nodeObstacle.getChallenge() / 100
                    rightNextMoveCost = currentAngle.rightAngleBetween(rightNextAngle) / 60 + currentAngle.rightAngleBetween(rightNextAngle) / 120 * currentNode.attributes.nodeObstacle.getChallenge() / 100

                    println("position: ${currentNode.position}")
                    println("angle: $currentAngle")

                    dirty = true
                }
                Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> {
                    currentAngle = rightNextAngle
//                    nodeRoomMesh.buildWallsLos(currentNode.position, currentAngle, visualRadius)

                    //                println("checking forward nodeAngle:")
                    forwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle)
//            println("checking backward nodeAngle:")
                    backwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
//            println("checking leftward angle:")
                    leftNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
//            println("checking rightward angle:")
                    rightNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

  //                  nodeRoomMesh.buildFloorsLos(currentNode, forwardNextNodeAngle.first, currentAngle, visualRadius)

                    forwardNextMoveCost = currentNode.position.dst(forwardNextNodeAngle.first.position) / 3 + currentNode.position.dst(forwardNextNodeAngle.first.position) * ( currentNode.attributes.nodeObstacle.getChallenge() + forwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 200
                    backwardNextMoveCost = currentNode.position.dst(backwardNextNodeAngle.first.position) / 3 + currentNode.position.dst(backwardNextNodeAngle.first.position) * ( currentNode.attributes.nodeObstacle.getChallenge() + backwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 200
                    leftNextMoveCost = currentAngle.leftAngleBetween(leftNextAngle) / 60 + currentAngle.leftAngleBetween(leftNextAngle) / 120 * currentNode.attributes.nodeObstacle.getChallenge() / 100
                    rightNextMoveCost = currentAngle.rightAngleBetween(rightNextAngle) / 60 + currentAngle.rightAngleBetween(rightNextAngle) / 120 * currentNode.attributes.nodeObstacle.getChallenge() / 100

                    println("position: ${currentNode.position}")
                    println("angle: $currentAngle")

                    dirty = true
                }
                Gdx.input.isKeyJustPressed(Input.Keys.X) -> {
               //     currentRoom.inactiveExitNodesInRange(currentNode).forEach { currentRoom.activateExitNode( it ) }

                    println ("inactivedExitNodes: ${currentRoom.inactiveExitNodesInRange(currentNode)}")
                    println ("activatedExitNodes: ${currentRoom.activatedExitNodes}")

                    println ("nodeRoomMesh.size = ${nodeRoomMesh.nodeRooms.size}")

                    dirty = true
                }
            }

            if (dirty) {
                nodeRoomMesh.buildWallsAndPath()
                nodeRoomMesh.renderWallsAndPathLos(currentNode.position, currentAngle, visualRadius)

                dirty = false
            }

            PlayerCharacter.render(batch, currentNode.position, currentAngle)
        }
    }

    override fun hide() {
    }

    override fun show() {
        if (!shown) { //not sure why this is needed -- show() appears to get called twice when screen loaded

            //                    nodeRoom.buildWalls(currentNode.position, 30f)
            currentRoom.inactiveExitNodesInRange(currentNode).forEach { nodeRoomMesh.activateExitNode( nodeRoomIdx, it ) }

            nodeRoomMesh.buildWallsAndPath()
            nodeRoomMesh.renderWallsAndPathLos(currentNode.position, currentAngle, visualRadius)
    //        nodeRoom.buildWalls()
            println ("exitNodes: ${currentRoom.inactiveExitNodesInRange(currentNode)}")

            //            println("checking forward nodeAngle:")
            forwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle)
    //            println("checking backward nodeAngle:")
            backwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
    //            println("checking leftward angle:")
            leftNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
    //            println("checking rightward angle:")
            rightNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

            //todo: move cost into nodeRoom, add elevation cost
            forwardNextMoveCost = currentNode.position.dst(forwardNextNodeAngle.first.position) / 3 + currentNode.position.dst(forwardNextNodeAngle.first.position) * ( currentNode.attributes.nodeObstacle.getChallenge() + forwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 200
            backwardNextMoveCost = currentNode.position.dst(backwardNextNodeAngle.first.position) / 3 + currentNode.position.dst(backwardNextNodeAngle.first.position) * ( currentNode.attributes.nodeObstacle.getChallenge() + backwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 200
            leftNextMoveCost = currentAngle.leftAngleBetween(leftNextAngle) / 60 + currentAngle.leftAngleBetween(leftNextAngle) / 120 * currentNode.attributes.nodeObstacle.getChallenge() / 100
            rightNextMoveCost = currentAngle.rightAngleBetween(rightNextAngle) / 60 + currentAngle.rightAngleBetween(rightNextAngle) / 120 * currentNode.attributes.nodeObstacle.getChallenge() / 100
        }

        // start the playback of the background music when the screen is shown
        MusicAssets.values().forEach { assets.load(it) }
        assets.finishLoading()
        println("done!")
        assets[MusicAssets.NavajoNight].apply { isLooping = true }.play()
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
        arcSdc.disposeShapeDrawerConfig()
        arcFadeSdc.disposeShapeDrawerConfig()
    }
}