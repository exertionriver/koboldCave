package org.river.exertion.koboldCave.screen.nodeRoom

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
import org.river.exertion.assets.MusicAssets
import org.river.exertion.assets.PlayerCharacter
import org.river.exertion.assets.get
import org.river.exertion.assets.load
import org.river.exertion.koboldCave.leaf.ILeaf.Companion.NextDistancePx
import org.river.exertion.koboldCave.Line.Companion.getPositionByDistanceAndAngle
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextNodeAngle
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine.Companion.buildNodeLine
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom.Companion.buildWalls
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom.Companion.buildWallsLos
import org.river.exertion.koboldCave.screen.RenderPalette.BackColors
import org.river.exertion.koboldCave.screen.RenderPalette.FadeBackColors
import org.river.exertion.koboldCave.screen.RenderPalette.ForeColors
import space.earlygrey.shapedrawer.JoinType

class DemoNodeRoomRotateNavigateScreen(private val batch: Batch,
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

    var rotation = 30f
    val cameraAngle = 90f

    val pathNoise = 0
    var degreesPerAngle = 8f
    var distancePerStep = 5f

    var leftTurnEasing = 0f //degrees
    var rightTurnEasing = 0f //degrees
    var forwardStepEasing = 0 //steps
    var backwardStepEasing = 0 //steps
    var leftTurnFinalEasing = 0f //degrees
    var rightTurnFinalEasing = 0f //degrees

    var currentPos = currentNode.position
    var stepPath = NodeLine()
    var finalNode = Node()

    val sdc = ShapeDrawerConfig(batch)
    val drawer = sdc.getDrawer()

//    val ego : com.badlogic.gdx.utils.Array<Vector2> = com.badlogic.gdx.utils.Array()

    var dirty = true
    override fun render(delta: Float) {

        batch.projectionMatrix = camera.combined
        camera.update()

        batch.use {

//            val nodeRoomIdx = 1

/*            font.drawLabel(it, Point(nodeRoom.centroid.position.x, labelVert.y), "NodeRoom (nodes=${nodeRoom.nodes.size}, exits=${nodeRoom.getExitNodes().size})\n" +
                    "(circleNoise:${nodeRoom.attributes.circleNoise})\n" +
                    "(angleNoise:${nodeRoom.attributes.angleNoise})\n" +
                    "(heightNoise:${nodeRoom.attributes.heightNoise})", ForeColors[nodeRoomIdx % ForeColors.size])
*/
            if (leftTurnEasing > 0) {
//                println("leftTurnEasing:$leftTurnEasing, currentAngle:$currentAngle")
                val angleLeft = if (leftTurnEasing >= degreesPerAngle) currentAngle + degreesPerAngle else currentAngle + leftTurnEasing
                val angleToRotate = currentAngle.leftAngleBetween(angleLeft)
                camera.rotate(Vector3.Z, angleToRotate)
                currentAngle = angleLeft.normalizeDeg()
                leftTurnEasing = if (leftTurnEasing >= degreesPerAngle) leftTurnEasing - degreesPerAngle else 0f
//                println("leftTurnEasing:$leftTurnEasing, currentAngle:$currentAngle")
            }
            if (rightTurnEasing > 0) {
//                println("rightTurnEasing:$rightTurnEasing, currentAngle:$currentAngle")
                val angleRight = if (rightTurnEasing >= degreesPerAngle) currentAngle - degreesPerAngle else currentAngle - rightTurnEasing
                val angleToRotate = currentAngle.leftAngleBetween(angleRight)
                camera.rotate(Vector3.Z, angleToRotate)
                currentAngle = angleRight.normalizeDeg()
                rightTurnEasing = if (rightTurnEasing >= degreesPerAngle) rightTurnEasing - degreesPerAngle else 0f
//                println("rightTurnEasing:$rightTurnEasing, currentAngle:$currentAngle")
            }
            if ( (forwardStepEasing > 0) && ((leftTurnEasing + rightTurnEasing) == 0f) ) {
                val currentIdx = stepPath.nodes.size - forwardStepEasing
//                println("forwardStepEasing:$forwardStepEasing, stepPath.nodes.size: ${stepPath.nodes.size}, currentIdx: $currentIdx, currentAngle:$currentAngle")

                val currentStep = stepPath.nodes[currentIdx]
                currentPos = currentStep.position

                if (forwardStepEasing == 1) {
                    leftTurnEasing = leftTurnFinalEasing
                    rightTurnEasing = rightTurnFinalEasing
                    leftTurnFinalEasing = 0f
                    rightTurnFinalEasing = 0f
                    currentNode = finalNode
                } else {
                    val nextStep = stepPath.nodes[currentIdx + 1]
                    val currentStepAngle = currentStep.angleBetween(nextStep)

                    if (currentAngle.leftAngleBetween(currentStepAngle) > currentAngle.rightAngleBetween(currentStepAngle) ) {
                        rightTurnEasing = currentAngle.rightAngleBetween(currentStepAngle)
                    } else {
                        leftTurnEasing = currentAngle.leftAngleBetween(currentStepAngle)
                    }
//                    println("currentAngle:$currentAngle, currentStepAngle: $currentStepAngle, leftTurnEasing:$leftTurnEasing, rightTurnEasing: $rightTurnEasing")
                }
                camera.position.set(currentPos.x, currentPos.y, 0f)
                forwardStepEasing--
//                println("forwardStepEasing:$forwardStepEasing, stepPath.nodes.size: ${stepPath.nodes.size}, currentIdx: $currentIdx, currentAngle:$currentAngle")
            }
            if ( (backwardStepEasing > 0) && ((leftTurnEasing + rightTurnEasing) == 0f) ) {
                val currentIdx = stepPath.nodes.size - backwardStepEasing
//                println("backwardStepEasing:$backwardStepEasing, stepPath.nodes.size: ${stepPath.nodes.size}, currentIdx: $currentIdx, currentAngle:$currentAngle")

                val currentStep = stepPath.nodes[currentIdx]
                currentPos = currentStep.position

                if (backwardStepEasing == 1) {
//                    println("leftTurnFinalEasing:$leftTurnFinalEasing, rightTurnFinalEasing:$rightTurnFinalEasing")
//                    leftTurnEasing = rightTurnFinalEasing
//                    rightTurnEasing = leftTurnFinalEasing
                    leftTurnFinalEasing = 0f
                    rightTurnFinalEasing = 0f
                    currentNode = finalNode
                } else {
                    val nextStep = stepPath.nodes[currentIdx + 1]
                    val currentStepAngle = nextStep.angleBetween(currentStep)

                    if (currentAngle.leftAngleBetween(currentStepAngle) > currentAngle.rightAngleBetween(currentStepAngle) ) {
                        rightTurnEasing = currentAngle.rightAngleBetween(currentStepAngle)
                    } else {
                        leftTurnEasing = currentAngle.leftAngleBetween(currentStepAngle)
                    }
//                    println("currentAngle:$currentAngle, currentStepAngle: $currentStepAngle, leftTurnEasing:$leftTurnEasing, rightTurnEasing: $rightTurnEasing")
                }
                camera.position.set(currentPos.x, currentPos.y, 0f)
                backwardStepEasing--
//                println("backwardStepEasing:$backwardStepEasing, stepPath.nodes.size: ${stepPath.nodes.size}, currentIdx: $currentIdx, currentAngle:$currentAngle")

            }


            nodeRoom.pastWall.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.5F, FadeBackColors[1])
            }

            nodeRoom.pastWallFade.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.3F, FadeBackColors[1])
            }

            nodeRoom.currentWall.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.5F, BackColors[1])
            }

            nodeRoom.currentWallFade.values.forEach { wallNode ->
                drawer.filledCircle(wallNode, 0.3F, BackColors[1])
            }

            nodeRoom.getExitNodes().forEach { exitNode ->
                drawer.filledCircle(exitNode.position, 4F, ForeColors[1])
            }

            InputHandler.handleInput(camera)

            when {
                (leftTurnEasing + rightTurnEasing + forwardStepEasing + backwardStepEasing > 0f) -> { /*do nothing*/ }

                Gdx.input.isKeyJustPressed(Input.Keys.R) -> {
 //                   println("rotation:$rotation")

                    camera.rotate(Vector3.Z, rotation)
                    dirty = true
                }
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE) -> {
                    nodeRoom = NodeRoom(height = 3, centerPoint = Point(horizOffset * 5.5f, vertOffset * 5.5f))
                    currentNode = nodeRoom.getRandomNode()
                    camera.position.set(currentNode.position.x, currentNode.position.y, 0f)

                    val newAngle = nodeRoom.getRandomNextNodeAngle(currentNode)
                    val angleToRotate = currentAngle.leftAngleBetween(newAngle)
                    camera.rotate(Vector3.Z, angleToRotate)

                    currentAngle = newAngle
 //                   println("currentAngle:$currentAngle, angleToRotate:$angleToRotate, cameraAngle:$cameraAngle")

//                    println("nodeRoomWallsSize : ${nodeRoom.currentWall.size}")

//                    forwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, currentAngle)
//            println("checking backward nodeAngle:")
//                    backwardNextNodeAngle = nodeRoom.getNextNodeAngle(currentNode, (180f + currentAngle).normalizeDeg())
//            println("checking leftward angle:")
//                    leftNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.LEFT)
//            println("checking rightward angle:")
//                    rightNextAngle = nodeRoom.getNextAngle(currentNode, currentAngle, NodeLink.NextAngle.RIGHT)

//                    println("position: ${currentNode.position}")
//                    println("angle: $currentAngle")
                    dirty = true

                }
                Gdx.input.isKeyJustPressed(Input.Keys.ENTER) -> {
                    nodeRoom.buildWalls()

//                    println("position: ${currentNode.position}")
//                    println("angle: $currentAngle")
                    dirty = true

                }
                Gdx.input.isKeyJustPressed(Input.Keys.UP) -> {
 //                   currentNode = forwardNextNodeAngle.first
 //                   camera.position.set(currentNode.position.x, currentNode.position.y, 0f)

                    val frontAngle = forwardNextNodeAngle.second
//                    val angleToRotate = currentAngle.leftAngleBetween(frontAngle)
//                    camera.rotate(Vector3.Z, angleToRotate)

//                    currentAngle = frontAngle
//                    println("currentAngle:$currentAngle, angleToRotate:$angleToRotate, cameraAngle:$cameraAngle")

                    stepPath = Pair(currentNode, forwardNextNodeAngle.first).buildNodeLine(noise = pathNoise, linkDistance = distancePerStep)
                    forwardStepEasing = stepPath.nodes.size
                    if (currentAngle.leftAngleBetween(frontAngle) > currentAngle.rightAngleBetween(frontAngle) ) {
                        rightTurnFinalEasing = currentAngle.rightAngleBetween(frontAngle)
                    } else {
                        leftTurnFinalEasing = currentAngle.leftAngleBetween(frontAngle)
                    }
                    finalNode = forwardNextNodeAngle.first
//                    nodeRoom.buildWalls(currentNode.position, 30f)

//                    forwardNextNodeAngle = nodeRoom.nodeLinks.getNextNodeAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle)
//            println("checking backward nodeAngle:")
//                    backwardNextNodeAngle = nodeRoom.nodeLinks.getNextNodeAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
//            println("checking leftward angle:")
//                    leftNextAngle = nodeRoom.nodeLinks.getNextAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
//            println("checking rightward angle:")
//                    rightNextAngle = nodeRoom.nodeLinks.getNextAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )
                    dirty = true

                }
                Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> {

                    //if backed into a corner, no 'backward' option
                    if (currentNode != backwardNextNodeAngle.first) {
//                        currentNode = backwardNextNodeAngle.first
//                        camera.position.set(currentNode.position.x, currentNode.position.y, 0f)

                        val backAngle = (180f + backwardNextNodeAngle.second).normalizeDeg()
//                        val angleToRotate = currentAngle.leftAngleBetween(backAngle)
//                        camera.rotate(Vector3.Z, angleToRotate)

//                        currentAngle = backAngle
//                        println("currentAngle:$currentAngle, angleToRotate:$angleToRotate, cameraAngle:$cameraAngle")

                        stepPath = Pair(currentNode, backwardNextNodeAngle.first).buildNodeLine(noise = pathNoise, linkDistance = distancePerStep)
                        backwardStepEasing = stepPath.nodes.size
                        if (currentAngle.leftAngleBetween(backAngle) > currentAngle.rightAngleBetween(backAngle) ) {
                            rightTurnFinalEasing = currentAngle.rightAngleBetween(backAngle)
                        } else {
                            leftTurnFinalEasing = currentAngle.leftAngleBetween(backAngle)
                        }
                        finalNode = backwardNextNodeAngle.first
                        //                    nodeRoom.buildWalls(currentNode.position, 30f)
                        //                println("checking forward nodeAngle:")
//                        forwardNextNodeAngle = nodeRoom.nodeLinks.getNextNodeAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle)
//            println("checking backward nodeAngle:")
//                        backwardNextNodeAngle = nodeRoom.nodeLinks.getNextNodeAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
//            println("checking leftward angle:")
//                        leftNextAngle = nodeRoom.nodeLinks.getNextAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
//            println("checking rightward angle:")
//                        rightNextAngle = nodeRoom.nodeLinks.getNextAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

//                        println("position: ${currentNode.position}")
//                        println("angle: $currentAngle")
                        dirty = true
                    }
                }
                Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> {
//                    camera.position.set(currentNode.position.x, currentNode.position.y, 0f)
                        leftTurnEasing = currentAngle.leftAngleBetween(leftNextAngle)
//                    println("currentAngle:$currentAngle, leftNextAngle:$leftNextAngle, leftTurnEasing:$leftTurnEasing")
//                    val angleToRotate = currentAngle.leftAngleBetween(leftNextAngle)
//                    camera.rotate(Vector3.Z, angleToRotate)

//                    currentAngle = leftNextAngle

                    //                println("checking forward nodeAngle:")

//                    println("position: ${currentNode.position}")
//                    println("angle: $currentAngle")
                    dirty = true

                }
                Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> {
//                    camera.position.set(currentNode.position.x, currentNode.position.y, 0f)
                    rightTurnEasing = currentAngle.rightAngleBetween(rightNextAngle)
 //                   println("currentAngle:$currentAngle, rightNextAngle:$rightNextAngle, rightTurnEasing:$rightTurnEasing")
//                    val angleToRotate = currentAngle.leftAngleBetween(rightNextAngle)
//                    camera.rotate(Vector3.Z, angleToRotate)

//                    currentAngle = rightNextAngle
//                    println("currentAngle:$currentAngle, angleToRotate:$angleToRotate, cameraAngle:$cameraAngle")

                    //                println("checking forward nodeAngle:")
//                    forwardNextNodeAngle = nodeRoom.nodeLinks.getNextNodeAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle)
//            println("checking backward nodeAngle:")
//                    backwardNextNodeAngle = nodeRoom.nodeLinks.getNextNodeAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
//            println("checking leftward angle:")
//                    leftNextAngle = nodeRoom.nodeLinks.getNextAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
//            println("checking rightward angle:")
//                    rightNextAngle = nodeRoom.nodeLinks.getNextAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

//                    println("position: ${currentNode.position}")
//                    println("angle: $currentAngle")
                    dirty = true
                }
            }

            forwardNextNodeAngle = nodeRoom.nodeLinks.getNextNodeAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle)
//            println("checking backward nodeAngle:")
            backwardNextNodeAngle = nodeRoom.nodeLinks.getNextNodeAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
//            println("checking leftward angle:")
            leftNextAngle = nodeRoom.nodeLinks.getNextAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
//            println("checking rightward angle:")
            rightNextAngle = nodeRoom.nodeLinks.getNextAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

//            nodeRoom.buildWallsLos(currentPos, currentAngle, visualRadius)

            if (dirty) {
                nodeRoom.buildWallsLos(currentPos, currentAngle, visualRadius)

                dirty = false
            }

            PlayerCharacter.render(batch, currentPos, currentAngle)

//            val egoAngle = 90f
/*            ego.clear()

            val bottomArrow = currentPos.getPositionByDistanceAndAngle(4f, (currentAngle + 180f).normalizeDeg())
            val topArrow = currentPos.getPositionByDistanceAndAngle(6f, currentAngle)
            val tipArrowLeft = topArrow.getPositionByDistanceAndAngle(3f, (currentAngle + 150f).normalizeDeg())
            val tipArrowRight = topArrow.getPositionByDistanceAndAngle(3f, (currentAngle - 150f).normalizeDeg())

            ego.add(bottomArrow, topArrow)
            ego.add(topArrow, tipArrowLeft)
            ego.add(tipArrowLeft, tipArrowRight)
            ego.add(tipArrowRight, topArrow)

            //may have to use another (colored) texture for ego-path
            drawer.path(ego, 1f, JoinType.SMOOTH, true)
*/

        }
    }

    override fun hide() {
    }

    override fun show() {
        //                    nodeRoom.buildWalls(currentNode.position, 30f)
        camera.position.set(currentNode.position.x, currentNode.position.y, 0f)
        camera.zoom = .2f

        val angleToRotate = cameraAngle.leftAngleBetween(currentAngle)
        camera.rotate(Vector3.Z, angleToRotate)
//        println("currentAngle:$currentAngle, angleToRotate:$angleToRotate, cameraAngle:$cameraAngle")

//        nodeRoom.buildWallsLos(currentNode.position, currentAngle, visualRadius)
        //        nodeRoom.buildWalls()

        //            println("checking forward nodeAngle:")
//        forwardNextNodeAngle = nodeRoom.nodeLinks.getNextNodeAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle)
        //            println("checking backward nodeAngle:")
//        backwardNextNodeAngle = nodeRoom.nodeLinks.getNextNodeAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
        //            println("checking leftward angle:")
//        leftNextAngle = nodeRoom.nodeLinks.getNextAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
        //            println("checking rightward angle:")
//        rightNextAngle = nodeRoom.nodeLinks.getNextAngle(nodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

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
    }
}