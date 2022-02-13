package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector3
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.*
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.entity.core.IEntity
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextNodeAngle
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine

class ActionFulfillMoveSystem : IntervalIteratingSystem(allOf(ActionMoveComponent::class).get(), 1/120f) {

    val degreesPerAngle = 1.5f
    var modDegreesPerAngle = degreesPerAngle

    override fun processEntity(entity: Entity) {
        entity[ActionMoveComponent.mapper]!!.momentCountdown += interval

        if ( entity.isEntity() && entity[ActionMoveComponent.mapper]!!.momentCountdown > entity[ActionMoveComponent.mapper]!!.moment.milliseconds * interval / 1000) {
            entity[ActionMoveComponent.mapper]!!.momentCountdown = 0f

            val currentPosition = entity[ActionMoveComponent.mapper]!!.currentPosition
            val currentAngle = entity[ActionMoveComponent.mapper]!!.currentAngle

            val leftTurnEasing = entity[ActionMoveComponent.mapper]!!.leftTurnEasing
            val rightTurnEasing = entity[ActionMoveComponent.mapper]!!.rightTurnEasing
            val forwardStepEasing = entity[ActionMoveComponent.mapper]!!.forwardStepEasing
            val backwardStepEasing = entity[ActionMoveComponent.mapper]!!.backwardStepEasing

            val camera = entity[ActionMoveComponent.mapper]!!.camera

            if (leftTurnEasing > 0) {
//                println("leftTurnEasing:$leftTurnEasing, currentAngle:$currentAngle")
                val angleLeft = if (leftTurnEasing >= modDegreesPerAngle) currentAngle + modDegreesPerAngle else currentAngle + leftTurnEasing
                val angleToRotate = currentAngle.leftAngleBetween(angleLeft)
                camera?.rotate(Vector3.Z, angleToRotate)
                entity[ActionMoveComponent.mapper]!!.currentAngle = angleLeft.normalizeDeg()
                entity[ActionMoveComponent.mapper]!!.leftTurnEasing = if (leftTurnEasing >= modDegreesPerAngle) leftTurnEasing - modDegreesPerAngle else 0f
                //               println("leftTurnEasing:$leftTurnEasing, currentAngle:$currentAngle")
            }
            if (rightTurnEasing > 0) {
//                println("rightTurnEasing:$rightTurnEasing, currentAngle:$currentAngle")
                val angleRight = if (rightTurnEasing >= modDegreesPerAngle) currentAngle - modDegreesPerAngle else currentAngle - rightTurnEasing
                val angleToRotate = currentAngle.leftAngleBetween(angleRight)
                camera?.rotate(Vector3.Z, angleToRotate)
                entity[ActionMoveComponent.mapper]!!.currentAngle = angleRight.normalizeDeg()
                entity[ActionMoveComponent.mapper]!!.rightTurnEasing = if (rightTurnEasing >= modDegreesPerAngle) rightTurnEasing - modDegreesPerAngle else 0f
                //               println("rightTurnEasing:$rightTurnEasing, currentAngle:$currentAngle")
            }
            if ( (forwardStepEasing > 0) && ((leftTurnEasing + rightTurnEasing) == 0f) ) {
                val currentIdx = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.size - forwardStepEasing
//                println("forwardStepEasing:$forwardStepEasing, stepPath.nodes.size: ${stepPath.nodes.size}, currentIdx: $currentIdx, currentAngle:$currentAngle")

                val currentStepAngle : Angle

                if (forwardStepEasing == 1) {
                    entity[ActionMoveComponent.mapper]!!.currentPosition = entity[ActionMoveComponent.mapper]!!.finalNode.position

                    currentStepAngle = entity[ActionMoveComponent.mapper]!!.finalAngle
//                    entity[ActionMoveComponent.mapper]!!.currentAngle = currentStepAngle

                    entity[ActionMoveComponent.mapper]!!.currentNode = entity[ActionMoveComponent.mapper]!!.finalNode

                } else {
                    val currentStep = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.first { it.uuid == entity[ActionMoveComponent.mapper]!!.stepPath.nodeOrder[currentIdx] }
                    val nextStep = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.first { it.uuid == entity[ActionMoveComponent.mapper]!!.stepPath.nodeOrder[currentIdx + 1] }
                    entity[ActionMoveComponent.mapper]!!.currentPosition = currentStep.position

                    currentStepAngle = currentStep.angleBetween(nextStep)
//                    entity[ActionMoveComponent.mapper]!!.currentAngle = currentStepAngle

//                    println("currentAngle:$currentAngle, currentStepAngle: $currentStepAngle, leftTurnEasing:$leftTurnEasing, rightTurnEasing: $rightTurnEasing")
                }

                if (currentAngle.leftAngleBetween(currentStepAngle) > currentAngle.rightAngleBetween(currentStepAngle) ) {
                    entity[ActionMoveComponent.mapper]!!.rightTurnEasing = currentAngle.rightAngleBetween(currentStepAngle)
                } else {
                    entity[ActionMoveComponent.mapper]!!.leftTurnEasing = currentAngle.leftAngleBetween(currentStepAngle)
                }

                camera?.position?.lerp(Vector3(currentPosition.x, currentPosition.y, 0f), 0.5f)
                entity[ActionMoveComponent.mapper]!!.forwardStepEasing--
//                println("forwardStepEasing:$forwardStepEasing, stepPath.nodes.size: ${stepPath.nodes.size}, currentIdx: $currentIdx, currentAngle:$currentAngle")
            }
            if ( (backwardStepEasing > 0) && ((leftTurnEasing + rightTurnEasing) == 0f) ) {
                val currentIdx = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.size - backwardStepEasing
                //               println("backwardStepEasing:$backwardStepEasing, stepPath.nodes.size: ${stepPath.nodes.size}, currentIdx: $currentIdx, currentAngle:$currentAngle")

                val currentStepAngle : Angle

                if (backwardStepEasing == 1) {
                    entity[ActionMoveComponent.mapper]!!.currentPosition = entity[ActionMoveComponent.mapper]!!.finalNode.position

                    currentStepAngle = entity[ActionMoveComponent.mapper]!!.finalAngle
//                    entity[ActionMoveComponent.mapper]!!.currentAngle = currentStepAngle

                    entity[ActionMoveComponent.mapper]!!.currentNode = entity[ActionMoveComponent.mapper]!!.finalNode
                } else {
                    val currentStep = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.first { it.uuid == entity[ActionMoveComponent.mapper]!!.stepPath.nodeOrder[currentIdx] }
                    val nextStep = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.first { it.uuid == entity[ActionMoveComponent.mapper]!!.stepPath.nodeOrder[currentIdx + 1] }
                    entity[ActionMoveComponent.mapper]!!.currentPosition = currentStep.position

                    currentStepAngle = nextStep.angleBetween(currentStep)
//                    entity[ActionMoveComponent.mapper]!!.currentAngle = currentStepAngle

//                    println("currentAngle:$currentAngle, currentStepAngle: $currentStepAngle, leftTurnEasing:$leftTurnEasing, rightTurnEasing: $rightTurnEasing")
                }

                if (currentAngle.leftAngleBetween(currentStepAngle) > currentAngle.rightAngleBetween(currentStepAngle) ) {
                    entity[ActionMoveComponent.mapper]!!.rightTurnEasing = currentAngle.rightAngleBetween(currentStepAngle)
                } else {
                    entity[ActionMoveComponent.mapper]!!.leftTurnEasing = currentAngle.leftAngleBetween(currentStepAngle)
                }

                camera?.position?.lerp(Vector3(currentPosition.x, currentPosition.y, 0f), 0.5f)
                entity[ActionMoveComponent.mapper]!!.backwardStepEasing--
//                println("backwardStepEasing:$backwardStepEasing, stepPath.nodes.size: ${stepPath.nodes.size}, currentIdx: $currentIdx, currentAngle:$currentAngle")
            }
        }
    }

    companion object {
        fun moveComplete(entity : Entity) : Boolean {
            return (entity[ActionMoveComponent.mapper]!!.leftTurnEasing +
                 entity[ActionMoveComponent.mapper]!!.rightTurnEasing +
                 entity[ActionMoveComponent.mapper]!!.forwardStepEasing +
                 entity[ActionMoveComponent.mapper]!!.backwardStepEasing) < 1f
        }
 }
}
