package org.river.exertion.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector3
import org.river.exertion.Angle
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextNodeAngle
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine.Companion.buildNodeLine
import org.river.exertion.leftAngleBetween
import org.river.exertion.normalizeDeg
import org.river.exertion.rightAngleBetween

class Navigation(val batch : Batch, val camera : OrthographicCamera, val initNode : Node, val initAngle : Angle) {

    var currentNode = initNode
    var currentAngle = initAngle

    var forwardNextNodeAngle : Pair<Node, Angle> = Pair(initNode, initAngle)
    var backwardNextNodeAngle : Pair<Node, Angle> = Pair(initNode, initAngle)
    var leftNextAngle : Angle = 0f
    var rightNextAngle : Angle = 0f

    val pathNoise = 0
    val degreesPerAngle = 12f
    val distancePerStep = 12f

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

    fun navigate() {
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

    }

    fun handleInput() {

        when {
            (leftTurnEasing + rightTurnEasing + forwardStepEasing + backwardStepEasing > 0f) -> { /*do nothing*/ }

            Gdx.input.isKeyJustPressed(Input.Keys.UP) -> {

                stepPath = Pair(currentNode, forwardNextNodeAngle.first).buildNodeLine(noise = modForwardPathNoise, linkDistance = modForwardDistancePerStep)
                forwardStepEasing = stepPath.nodes.size
                finalNode = forwardNextNodeAngle.first
                finalAngle = forwardNextNodeAngle.second
            }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> {

                if (currentNode != backwardNextNodeAngle.first) {

                    stepPath = Pair(currentNode, backwardNextNodeAngle.first).buildNodeLine(noise = modBackwardPathNoise, linkDistance = modBackwardDistancePerStep)
                    backwardStepEasing = stepPath.nodes.size
                    finalNode = backwardNextNodeAngle.first
                    finalAngle = finalNode.angleBetween(currentNode)
                }
            }
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> {
                leftTurnEasing = currentAngle.leftAngleBetween(leftNextAngle)

            }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> {
                rightTurnEasing = currentAngle.rightAngleBetween(rightNextAngle)

            }
        }

//                    nodeRoom.buildWalls(currentNode.position, 30f)

        //todo: move cost into nodeRoom, add elevation cost
//            println("current challenge:${currentNode.attributes.nodeObstacle.getChallenge()}, forward challenge:${forwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()}, backward challenge:${backwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()}" )
        modForwardPathNoise = ( currentNode.attributes.nodeObstacle.getChallenge() + forwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 4
        modBackwardPathNoise = ( currentNode.attributes.nodeObstacle.getChallenge() + backwardNextNodeAngle.first.attributes.nodeObstacle.getChallenge()) / 4

    }

}