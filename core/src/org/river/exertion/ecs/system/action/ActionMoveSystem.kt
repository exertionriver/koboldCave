package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEntityComponent
import org.river.exertion.isEntity
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextNodeAngle
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine.Companion.buildNodeLine
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import org.river.exertion.leftAngleBetween
import org.river.exertion.rightAngleBetween

class ActionMoveSystem : IteratingSystem(allOf(ActionMoveComponent::class).get()) {

    val pathNoise = 0
    val distancePerStep = 1f

    var modForwardPathNoise = pathNoise
    var modBackwardPathNoise = pathNoise
    var modForwardDistancePerStep = distancePerStep
    var modBackwardDistancePerStep = distancePerStep

    override fun processEntity(entity: Entity, deltaTime: Float) {

        if ( //ActionPlexSystem.readyToExecute(entity, ActionMoveComponent.mapper) &&
                ActionFulfillMoveSystem.moveComplete(entity) && entity.isEntity() ) {

            val currentNode = entity[ActionMoveComponent.mapper]!!.currentNode
            val currentAngle = entity[ActionMoveComponent.mapper]!!.currentAngle
            val currentNodeRoom = entity[ActionMoveComponent.mapper]!!.currentNodeRoom

            entity[ActionMoveComponent.mapper]!!.forwardNextNodeAngle = currentNodeRoom.nodeLinks.getNextNodeAngle(currentNodeRoom.nodes.toMutableList(), currentNode, currentAngle)
            entity[ActionMoveComponent.mapper]!!.backwardNextNodeAngle = currentNodeRoom.nodeLinks.getNextNodeAngle(currentNodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
            entity[ActionMoveComponent.mapper]!!.leftNextAngle = currentNodeRoom.nodeLinks.getNextAngle(currentNodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
            entity[ActionMoveComponent.mapper]!!.rightNextAngle = currentNodeRoom.nodeLinks.getNextAngle(currentNodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

            val forwardNextNodeAngle = entity[ActionMoveComponent.mapper]!!.forwardNextNodeAngle
            val backwardNextNodeAngle = entity[ActionMoveComponent.mapper]!!.backwardNextNodeAngle

            println("moving direction: ${entity[ActionMoveComponent.mapper]!!.direction}")

            when (entity[ActionMoveComponent.mapper]!!.direction) {
                ActionMoveComponent.Direction.FORWARD -> {
                    entity[ActionMoveComponent.mapper]!!.stepPath = Pair(currentNode, forwardNextNodeAngle.first).buildNodeLine(noise = modForwardPathNoise, linkDistance = modForwardDistancePerStep)
                    entity[ActionMoveComponent.mapper]!!.forwardStepEasing = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.size
                    entity[ActionMoveComponent.mapper]!!.finalNode = forwardNextNodeAngle.first
                    entity[ActionMoveComponent.mapper]!!.finalAngle = forwardNextNodeAngle.second
                }
                ActionMoveComponent.Direction.BACKWARD -> {

                    if (currentNode != backwardNextNodeAngle.first) {

                        entity[ActionMoveComponent.mapper]!!.stepPath = Pair(currentNode, backwardNextNodeAngle.first).buildNodeLine(noise = modBackwardPathNoise, linkDistance = modBackwardDistancePerStep)
                        entity[ActionMoveComponent.mapper]!!.backwardStepEasing = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.size
                        entity[ActionMoveComponent.mapper]!!.finalNode = backwardNextNodeAngle.first
                        entity[ActionMoveComponent.mapper]!!.finalAngle = entity[ActionMoveComponent.mapper]!!.finalNode.angleBetween(currentNode)
                    }
                }
                ActionMoveComponent.Direction.LEFT -> {
                    entity[ActionMoveComponent.mapper]!!.leftTurnEasing = currentAngle.leftAngleBetween(entity[ActionMoveComponent.mapper]!!.leftNextAngle)
                }
                ActionMoveComponent.Direction.RIGHT -> {
                    entity[ActionMoveComponent.mapper]!!.rightTurnEasing = currentAngle.rightAngleBetween(entity[ActionMoveComponent.mapper]!!.rightNextAngle)
                }

            }

            //randomly select next direction
            entity[ActionMoveComponent.mapper]!!.direction = ProbabilitySelect(mapOf(
                    ActionMoveComponent.Direction.FORWARD to Probability(40f, 0)
                    , ActionMoveComponent.Direction.BACKWARD to Probability(10f, 0)
                    , ActionMoveComponent.Direction.LEFT to Probability(25f, 0)
                    , ActionMoveComponent.Direction.RIGHT to Probability(25f, 0)
            )).getSelectedProbability()!!

            println ("entity ${entity.getEntityComponent().name} moves to ${entity[ActionMoveComponent.mapper]!!.currentNode.position}.")

            entity[ActionMoveComponent.mapper]!!.executed = true
        }
    }


}
