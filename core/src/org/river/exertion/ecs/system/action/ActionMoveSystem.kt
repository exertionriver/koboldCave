package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import org.river.exertion.assets.PlayerCharacter
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.entity.EntityPlayerCharacter
import org.river.exertion.ecs.component.entity.core.IEntity
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEntityComponent
import org.river.exertion.isEntity
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextNodeAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNodeLink
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine.Companion.buildNodeLine
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import org.river.exertion.leftAngleBetween
import org.river.exertion.rightAngleBetween

class ActionMoveSystem : IteratingSystem(allOf(ActionMoveComponent::class).get()) {

    val pathNoise = 0
    val distancePerStep = .25f

    var modForwardPathNoise = pathNoise
    var modBackwardPathNoise = pathNoise
    var modForwardDistancePerStep = distancePerStep
    var modBackwardDistancePerStep = distancePerStep

    override fun processEntity(entity: Entity, deltaTime: Float) {

        if ( //ActionPlexSystem.readyToExecute(entity, ActionMoveComponent.mapper) &&
                ActionFulfillMoveSystem.moveComplete(entity) && entity.isEntity() ) {

            val currentNode = entity[ActionMoveComponent.mapper]!!.currentNode
            val currentAngle = entity[ActionMoveComponent.mapper]!!.currentAngle
            val nodeRoomMesh = entity[ActionMoveComponent.mapper]!!.nodeRoomMesh

            entity[ActionMoveComponent.mapper]!!.forwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle)
            entity[ActionMoveComponent.mapper]!!.backwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
            entity[ActionMoveComponent.mapper]!!.leftNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
            entity[ActionMoveComponent.mapper]!!.rightNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

            val forwardNextNodeAngle = entity[ActionMoveComponent.mapper]!!.forwardNextNodeAngle
            val backwardNextNodeAngle = entity[ActionMoveComponent.mapper]!!.backwardNextNodeAngle

            entity[ActionMoveComponent.mapper]!!.currentNodeLink = nodeRoomMesh.nodeLinks.getNodeLink(currentNode.uuid, forwardNextNodeAngle.first.uuid)!!

//            println("moving direction: ${entity[ActionMoveComponent.mapper]!!.direction}")

            when (entity[ActionMoveComponent.mapper]!!.direction) {
                ActionMoveComponent.Direction.FORWARD -> {
                    entity[ActionMoveComponent.mapper]!!.stepPath = Pair(currentNode, forwardNextNodeAngle.first).buildNodeLine(noise = modForwardPathNoise, linkDistance = modForwardDistancePerStep)
                    entity[ActionMoveComponent.mapper]!!.forwardStepEasing = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.size
                    entity[ActionMoveComponent.mapper]!!.finalNode = forwardNextNodeAngle.first
                    entity[ActionMoveComponent.mapper]!!.finalAngle = forwardNextNodeAngle.second

//                    entity[ActionMoveComponent.mapper]!!.currentNodeRoom = currentNodeRoom.nodeLinks.getNodeLink(currentNode.uuid, forwardNextNodeAngle.first.uuid)!!
                }
                ActionMoveComponent.Direction.BACKWARD -> {

                    if (currentNode != backwardNextNodeAngle.first) {

                        entity[ActionMoveComponent.mapper]!!.stepPath = Pair(currentNode, backwardNextNodeAngle.first).buildNodeLine(noise = modBackwardPathNoise, linkDistance = modBackwardDistancePerStep)
                        entity[ActionMoveComponent.mapper]!!.backwardStepEasing = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.size
                        entity[ActionMoveComponent.mapper]!!.finalNode = backwardNextNodeAngle.first
                        entity[ActionMoveComponent.mapper]!!.finalAngle = entity[ActionMoveComponent.mapper]!!.finalNode.angleBetween(currentNode)

                        entity[ActionMoveComponent.mapper]!!.currentNodeLink = nodeRoomMesh.nodeLinks.getNodeLink(currentNode.uuid, forwardNextNodeAngle.first.uuid)!!
                    }
                }
                ActionMoveComponent.Direction.LEFT -> {
                    entity[ActionMoveComponent.mapper]!!.leftTurnEasing = currentAngle.leftAngleBetween(entity[ActionMoveComponent.mapper]!!.leftNextAngle)
                }
                ActionMoveComponent.Direction.RIGHT -> {
                    entity[ActionMoveComponent.mapper]!!.rightTurnEasing = currentAngle.rightAngleBetween(entity[ActionMoveComponent.mapper]!!.rightNextAngle)
                }

            }
/*
            //randomly select next direction
            entity[ActionMoveComponent.mapper]!!.direction = ProbabilitySelect(mapOf(
                    ActionMoveComponent.Direction.FORWARD to Probability(75f, 0)
                    , ActionMoveComponent.Direction.BACKWARD to Probability(5f, 0)
                    , ActionMoveComponent.Direction.LEFT to Probability(10f, 0)
                    , ActionMoveComponent.Direction.RIGHT to Probability(10f, 0)
            )).getSelectedProbability()!!
*/
            if (entity.components.any { it is EntityPlayerCharacter } ) entity[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.NONE


                    entity[ActionMoveComponent.mapper]!!.currentNodeRoom = nodeRoomMesh.getNodeRoom(currentNode)

//            println ("entity ${entity.getEntityComponent().name} moves to ${entity[ActionMoveComponent.mapper]!!.currentNode.position}.")

            entity[ActionMoveComponent.mapper]!!.executed = true
        }
    }


}
