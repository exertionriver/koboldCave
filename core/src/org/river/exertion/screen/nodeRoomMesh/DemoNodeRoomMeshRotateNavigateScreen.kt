package org.river.exertion.screen.nodeRoomMesh

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import ktx.app.KtxScreen
import ktx.graphics.use
import org.river.exertion.*
import org.river.exertion.assets.*
import org.river.exertion.koboldCave.Line.Companion.angleBetween
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.addNodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextNodeAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNodeLinks
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine.Companion.buildNodeLine
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh.Companion.buildFloorsLos
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsLos
import org.river.exertion.screen.Render
import org.river.exertion.screen.RenderPalette.FadeBackColors
import org.river.exertion.screen.RenderPalette.FadeForeColors
import org.river.exertion.screen.render
import space.earlygrey.shapedrawer.JoinType

class DemoNodeRoomMeshRotateNavigateScreen(private val batch: Batch,
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

    var nodeRoomIdx = 0
    var currentRoom = nodeRoomMesh.nodeRooms[nodeRoomIdx]
    var currentNode = currentRoom.getRandomNode()
    var currentAngle = currentRoom.getRandomNextNodeAngle(currentNode)
    val visualRadius = NextDistancePx * 1.5f

    val nav = Navigation(batch, camera, currentNode, currentAngle)

    var rotation = 30f
    val cameraAngle = 90f

    val pathNoise = 0
    val degreesPerAngle = 5f
    val distancePerStep = 5f

    var modForwardPathNoise = pathNoise
    var modBackwardPathNoise = pathNoise
    var modDegreesPerAngle = degreesPerAngle
    var modForwardDistancePerStep = distancePerStep
    var modBackwardDistancePerStep = distancePerStep

    var forwardNextMoveCost : Float = 0f
    var backwardNextMoveCost : Float = 0f
    var leftNextMoveCost : Float = 0f
    var rightNextMoveCost : Float = 0f

    var leftTurnEasing = 0f //degrees
    var rightTurnEasing = 0f //degrees
    var forwardStepEasing = 0 //steps
    var backwardStepEasing = 0 //steps

    var currentPos = currentNode.position
    var stepPath = NodeLine()
    var finalNode = Node()
    var finalAngle : Angle = 0.0f

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

    val arcSdc = ShapeDrawerConfig(batch, FadeForeColors[1])
    val arcDrawer = arcSdc.getDrawer()

    val arcFadeSdc = ShapeDrawerConfig(batch, FadeBackColors[1])
    val arcFadeDrawer = arcFadeSdc.getDrawer()

    var dirty = true
    override fun render(delta: Float) {

        println ("delta:$delta, rps:${1f/delta}")

        batch.projectionMatrix = camera.combined
        camera.update()

        batch.use {

//            var currentRoom = nodeRoomMesh.nodeRooms[nodeRoomIdx]

/*            font.drawLabel(it, Point(currentRoom.centroid.position.x, labelVert.y * 2)
                    , "NodeRoom (nodes=${currentRoom.nodes.size} idx=$nodeRoomIdx)\n" +
                        "roomDescription: ${currentRoom.description}\n" +
                        "exits: ${nodeRoomMesh.currentRoomExits} / ${nodeRoomMesh.maxRoomExits}\n" +
                        "current obstacle:${currentNode.attributes.nodeObstacle}\n" +
                        "dst:${currentNode.position.dst(forwardNextNodeAngle.first.position) / 3}, ${currentNode.position.dst(backwardNextNodeAngle.first.position) / 3}, $currentAngle, ${currentAngle.leftAngleBetween(leftNextAngle)}, ${currentAngle.rightAngleBetween(rightNextAngle)}\n" +
                        "move costs:$forwardNextMoveCost, $backwardNextMoveCost, $leftNextMoveCost, $rightNextMoveCost\n" +
                        "elevation:${currentNode.attributes.nodeElevation}, ${forwardNextNodeAngle.first.attributes.nodeElevation}, ${backwardNextNodeAngle.first.attributes.nodeElevation}\n" +
                        "slope: ${nodeRoomMesh.getSlope(currentNode, forwardNextNodeAngle.first)}, ${nodeRoomMesh.getSlope(currentNode, backwardNextNodeAngle.first)}"
                    , ForeColors[nodeRoomIdx % ForeColors.size])
*/
//            val renderIdx = 1

            nodeRoomMesh.render(batch)

  //          nav.navigate()


            if (leftTurnEasing > 0) {
//                println("leftTurnEasing:$leftTurnEasing, currentAngle:$currentAngle")
                val angleLeft = if (leftTurnEasing >= modDegreesPerAngle) currentAngle + modDegreesPerAngle else currentAngle + leftTurnEasing
                val angleToRotate = currentAngle.leftAngleBetween(angleLeft)
                camera.rotate(Vector3.Z, angleToRotate)
                currentAngle = angleLeft.normalizeDeg()
                leftTurnEasing = if (leftTurnEasing >= modDegreesPerAngle) leftTurnEasing - modDegreesPerAngle else 0f
 //               println("leftTurnEasing:$leftTurnEasing, currentAngle:$currentAngle")
            }
            if (rightTurnEasing > 0) {
//                println("rightTurnEasing:$rightTurnEasing, currentAngle:$currentAngle")
                val angleRight = if (rightTurnEasing >= modDegreesPerAngle) currentAngle - modDegreesPerAngle else currentAngle - rightTurnEasing
                val angleToRotate = currentAngle.leftAngleBetween(angleRight)
                camera.rotate(Vector3.Z, angleToRotate)
                currentAngle = angleRight.normalizeDeg()
                rightTurnEasing = if (rightTurnEasing >= modDegreesPerAngle) rightTurnEasing - modDegreesPerAngle else 0f
 //               println("rightTurnEasing:$rightTurnEasing, currentAngle:$currentAngle")
            }
            if ( (forwardStepEasing > 0) && ((leftTurnEasing + rightTurnEasing) == 0f) ) {
                val currentIdx = stepPath.nodes.size - forwardStepEasing
//                println("forwardStepEasing:$forwardStepEasing, stepPath.nodes.size: ${stepPath.nodes.size}, currentIdx: $currentIdx, currentAngle:$currentAngle")

                val currentStepAngle : Angle

                if (forwardStepEasing == 1) {
                    currentPos = finalNode.position

                    currentStepAngle = finalAngle

                    currentNode = finalNode
                } else {
                    val currentStep = stepPath.nodes[currentIdx]
                    val nextStep = stepPath.nodes[currentIdx + 1]
                    currentPos = currentStep.position

                    currentStepAngle = currentStep.angleBetween(nextStep)

//                    println("currentAngle:$currentAngle, currentStepAngle: $currentStepAngle, leftTurnEasing:$leftTurnEasing, rightTurnEasing: $rightTurnEasing")
                }

                if (currentAngle.leftAngleBetween(currentStepAngle) > currentAngle.rightAngleBetween(currentStepAngle) ) {
                    rightTurnEasing = currentAngle.rightAngleBetween(currentStepAngle)
                } else {
                    leftTurnEasing = currentAngle.leftAngleBetween(currentStepAngle)
                }

                camera.position.lerp(Vector3(currentPos.x, currentPos.y, 0f), 0.5f)
                forwardStepEasing--
//                println("forwardStepEasing:$forwardStepEasing, stepPath.nodes.size: ${stepPath.nodes.size}, currentIdx: $currentIdx, currentAngle:$currentAngle")
            }
            if ( (backwardStepEasing > 0) && ((leftTurnEasing + rightTurnEasing) == 0f) ) {
                val currentIdx = stepPath.nodes.size - backwardStepEasing
 //               println("backwardStepEasing:$backwardStepEasing, stepPath.nodes.size: ${stepPath.nodes.size}, currentIdx: $currentIdx, currentAngle:$currentAngle")

                val currentStepAngle : Angle

                if (backwardStepEasing == 1) {
                    currentPos = finalNode.position

                    currentStepAngle = finalAngle

                    currentNode = finalNode
                } else {
                    val currentStep = stepPath.nodes[currentIdx]
                    val nextStep = stepPath.nodes[currentIdx + 1]
                    currentPos = currentStep.position

                    currentStepAngle = nextStep.angleBetween(currentStep)

//                    println("currentAngle:$currentAngle, currentStepAngle: $currentStepAngle, leftTurnEasing:$leftTurnEasing, rightTurnEasing: $rightTurnEasing")
                }

                if (currentAngle.leftAngleBetween(currentStepAngle) > currentAngle.rightAngleBetween(currentStepAngle) ) {
                    rightTurnEasing = currentAngle.rightAngleBetween(currentStepAngle)
                } else {
                    leftTurnEasing = currentAngle.leftAngleBetween(currentStepAngle)
                }

                camera.position.lerp(Vector3(currentPos.x, currentPos.y, 0f), 0.5f)
                backwardStepEasing--
//                println("backwardStepEasing:$backwardStepEasing, stepPath.nodes.size: ${stepPath.nodes.size}, currentIdx: $currentIdx, currentAngle:$currentAngle")

            }


//            nodeRoomMesh.nodeRooms.forEachIndexed { idx, nodeRoom -> nodeRoom.getExitNodes().forEachIndexed { index, exitNode ->
//                drawer.filledCircle(exitNode.position, 4F, FadeBackColors[idx % ForeColors.size])
//            } }

            InputHandler.handleInput(camera)

            when {
                (nav.leftTurnEasing + nav.rightTurnEasing + nav.forwardStepEasing + nav.backwardStepEasing > 0f) -> { /*do nothing*/ }

                Gdx.input.isKeyJustPressed(Input.Keys.UP) -> {

                    stepPath = Pair(currentNode, forwardNextNodeAngle.first).buildNodeLine(noise = modForwardPathNoise, linkDistance = modForwardDistancePerStep)
                    forwardStepEasing = stepPath.nodes.size
                    finalNode = forwardNextNodeAngle.first
                    finalAngle = forwardNextNodeAngle.second
                    dirty = true
                }
                Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> {

                    if (currentNode != backwardNextNodeAngle.first) {

                        stepPath = Pair(currentNode, backwardNextNodeAngle.first).buildNodeLine(noise = modBackwardPathNoise, linkDistance = modBackwardDistancePerStep)
                        backwardStepEasing = stepPath.nodes.size
                        finalNode = backwardNextNodeAngle.first
                        finalAngle = finalNode.angleBetween(currentNode)
                        dirty = true

                    }
                }
                Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> {
                    leftTurnEasing = currentAngle.leftAngleBetween(leftNextAngle)
                    dirty = true

                }
                Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> {
                    rightTurnEasing = currentAngle.rightAngleBetween(rightNextAngle)
                    dirty = true
                }
            }

     //       nav.handleInput()

            nodeRoomIdx = nodeRoomMesh.getCurrentRoomIdx(currentNode)

            nodeRoomMesh.inactiveExitNodesInRange(currentNode).forEach { nodeRoomMesh.activateExitNode( nodeRoomIdx, it ) }

/*
//                    nodeRoom.buildWalls(currentNode.position, 30f)
*/
            forwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle)
//            println("checking backward nodeAngle:")
            backwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
//            println("checking leftward angle:")
            leftNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
//            println("checking rightward angle:")
            rightNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )
/*

            //todo: move cost into nodeRoom, add elevation cost
//            println("current challenge:${currentNode.attributes.nodeObstacle.getChallenge()}, forward challenge:${forwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()}, backward challenge:${backwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()}" )
            modForwardPathNoise = ( currentNode.attributes.nodeObstacle.getChallenge() + forwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 4
            modBackwardPathNoise = ( currentNode.attributes.nodeObstacle.getChallenge() + backwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 4
*/
//            modDegreesPerAngle = degreesPerAngle - currentNode.attributes.nodeObstacle.getChallenge() / 20

//            println("current elevation:${currentNode.attributes.nodeElevation.getHeight()}, forward elevation:${forwardNextNodeAngle.first.attributes.nodeElevation.getHeight()}, backward elevation:${backwardNextNodeAngle.first.attributes.nodeElevation.getHeight()}" )
//            modBackwardDistancePerStep = distancePerStep - abs(max( currentNode.attributes.nodeElevation.getHeight(), backwardNextNodeAngle.first.attributes.nodeElevation.getHeight() ) - min( currentNode.attributes.nodeElevation.getHeight(), backwardNextNodeAngle.first.attributes.nodeElevation.getHeight() ) / 4).toInt()
//            modForwardDistancePerStep = distancePerStep - abs(max( currentNode.attributes.nodeElevation.getHeight(), forwardNextNodeAngle.first.attributes.nodeElevation.getHeight() ) - min( currentNode.attributes.nodeElevation.getHeight(), forwardNextNodeAngle.first.attributes.nodeElevation.getHeight() ) / 4).toInt()

//            println("modForwardPathNoise:$modForwardPathNoise, modBackwardPathNoise:$modBackwardPathNoise, modDegreesPerAngle:$modDegreesPerAngle, modForwardDistancePerStep:$modForwardDistancePerStep, modBackwardDistancePerStep:$modBackwardDistancePerStep")
    if (dirty) {
        nodeRoomMesh.buildWallsLos(currentPos, currentAngle, visualRadius)
        nodeRoomMesh.buildFloorsLos(currentNode, forwardNextNodeAngle.first, currentAngle, visualRadius)

        dirty = false
    }

            PlayerCharacter.render(batch, currentPos, currentAngle)
        }
    }

    override fun hide() {
    }

    override fun show() {

        Render.initRender(camera, currentNode, currentAngle)
        //                    nodeRoom.buildWalls(currentNode.position, 30f)
   //     currentRoom.inactiveExitNodesInRange(currentNode).forEach { nodeRoomMesh.activateExitNode( nodeRoomIdx, it ) }

        // start the playback of the background music when the screen is shown
        MusicAssets.values().forEach { assets.load(it) }
        assets.finishLoading()
//        println("done!")
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