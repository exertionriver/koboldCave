package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.math.Vector3
import ktx.ashley.allOf
import ktx.ashley.contains
import ktx.ashley.get
import org.river.exertion.*
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.entity.EntityPlayerCharacter
import org.river.exertion.geom.node.Node.Companion.angleBetween

class ActionFulfillMoveSystem : IntervalIteratingSystem(allOf(ActionMoveComponent::class).get(), 1/120f) {

    val degreesPerAngle = 1.5f
    var modDegreesPerAngle = degreesPerAngle

    override fun processEntity(entity: Entity) {
        entity[ActionMoveComponent.mapper]!!.momentCountdown += interval

        if ( entity.isEntity() && entity[ActionMoveComponent.mapper]!!.momentCountdown > entity[ActionMoveComponent.mapper]!!.moment.milliseconds * interval / 1000) {
            entity[ActionMoveComponent.mapper]!!.momentCountdown = 0f

            val currentPosition = entity[ActionMoveComponent.mapper]!!.currentPosition
            val currentAngle = entity[ActionMoveComponent.mapper]!!.currentAngle
            val stepPathFifthNextNode = entity[ActionMoveComponent.mapper]!!.stepPathFifthNextNode()

            val otherEntities = engine.entities.filter { it != entity && it.contains(ActionMoveComponent.mapper) }

            if (otherEntities.isNotEmpty()) otherEntities.forEach { otherEntity ->
                //entities are too close, five steps ahead
                if ( Pair(stepPathFifthNextNode.position, otherEntity[ActionMoveComponent.mapper]!!.currentPosition).tooClose() ) {
                    entity[ActionMoveComponent.mapper]!!.halt()
                }
            }

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
                entity[ActionMoveComponent.mapper]!!.currentNode.attributes.occupied = true
                entity[ActionMoveComponent.mapper]!!.finalNode.attributes.occupied = true

                val currentStepAngle : Angle

                if (forwardStepEasing == 1) {
                    entity[ActionMoveComponent.mapper]!!.currentPosition = entity[ActionMoveComponent.mapper]!!.finalNode.position

                    currentStepAngle = entity[ActionMoveComponent.mapper]!!.finalAngle
//                    entity[ActionMoveComponent.mapper]!!.currentAngle = currentStepAngle

                    entity[ActionMoveComponent.mapper]!!.currentNode.attributes.occupied = false
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
                entity[ActionMoveComponent.mapper]!!.currentNode.attributes.occupied = true
                entity[ActionMoveComponent.mapper]!!.finalNode.attributes.occupied = true

                val currentStepAngle : Angle

                if (backwardStepEasing == 1) {
                    entity[ActionMoveComponent.mapper]!!.currentPosition = entity[ActionMoveComponent.mapper]!!.finalNode.position

                    currentStepAngle = entity[ActionMoveComponent.mapper]!!.finalAngle
//                    entity[ActionMoveComponent.mapper]!!.currentAngle = currentStepAngle

                    entity[ActionMoveComponent.mapper]!!.currentNode.attributes.occupied = false
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

            if ((currentPosition != entity[ActionMoveComponent.mapper]!!.currentPosition) || (currentAngle != entity[ActionMoveComponent.mapper]!!.currentAngle) )
                MessageManager.getInstance().dispatchMessage(entity[MessageComponent.mapper]!!, ECS_S2D_BRIDGE, entity[ActionMoveComponent.mapper]!!)
        }
    }

    companion object {

        fun Pair<Point, Point>.tooClose() : Boolean {
            return this.first.dst(this.second) < NextDistancePx / 3
        }

    }
}
