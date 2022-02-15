package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.contains
import ktx.ashley.get
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.entity.EntityPlayerCharacter
import org.river.exertion.isEntity
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.Node.Companion.angleBetween
import org.river.exertion.koboldCave.node.NodeAttributes
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
                //ActionFulfillMoveSystem.moveComplete(entity) &&
                entity.isEntity() ) {

            val currentPosition = entity[ActionMoveComponent.mapper]!!.currentPosition
            val currentNode = entity[ActionMoveComponent.mapper]!!.currentNode
            val currentAngle = entity[ActionMoveComponent.mapper]!!.currentAngle
            val nodeRoomMesh = entity[ActionMoveComponent.mapper]!!.nodeRoomMesh

            entity[ActionMoveComponent.mapper]!!.forwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys, currentNode, currentAngle)
            entity[ActionMoveComponent.mapper]!!.backwardNextNodeAngle = nodeRoomMesh.nodeLinks.getNextNodeAngle(nodeRoomMesh.nodesMap.keys, currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
            entity[ActionMoveComponent.mapper]!!.leftNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys, currentNode, currentAngle, NodeLink.NextAngle.LEFT )
            entity[ActionMoveComponent.mapper]!!.rightNextAngle = nodeRoomMesh.nodeLinks.getNextAngle(nodeRoomMesh.nodesMap.keys, currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

            val forwardNextNodeAngle = entity[ActionMoveComponent.mapper]!!.forwardNextNodeAngle
            val backwardNextNodeAngle = entity[ActionMoveComponent.mapper]!!.backwardNextNodeAngle

            entity[ActionMoveComponent.mapper]!!.currentNodeLink = nodeRoomMesh.nodeLinks.getNodeLink(currentNode.uuid, forwardNextNodeAngle.first.uuid)!!

            nodeRoomMesh.nodesMap.keys.filter { it.uuid == currentNode.uuid }.firstOrNull()?.attributes?.renderState = NodeAttributes.RenderState.RENDERED
            nodeRoomMesh.nodesMap.keys.filter { it.uuid == forwardNextNodeAngle.first.uuid }.firstOrNull()?.attributes?.renderState = NodeAttributes.RenderState.RENDERED

//            println("moving direction: ${entity[ActionMoveComponent.mapper]!!.direction}")


            when (entity[ActionMoveComponent.mapper]!!.direction) {
                ActionMoveComponent.Direction.FORWARD -> {
                    if (!entity[ActionMoveComponent.mapper]!!.moveComplete() ) {
                        if (entity[ActionMoveComponent.mapper]!!.backwardStepEasing > 0) {
//                            println("halt backward movement")
                            entity[ActionMoveComponent.mapper]!!.halt()
                        }
                    } else { //initiate movement to next node
                        //from current Node
                        if (entity[ActionMoveComponent.mapper]!!.currentPosition == entity[ActionMoveComponent.mapper]!!.currentNode.position) {
 //                           println("initiate forward from current node")
                            entity[ActionMoveComponent.mapper]!!.stepPath = Pair(currentNode, forwardNextNodeAngle.first).buildNodeLine(noise = modForwardPathNoise, linkDistance = modForwardDistancePerStep)
                            entity[ActionMoveComponent.mapper]!!.forwardStepEasing = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.size
                            entity[ActionMoveComponent.mapper]!!.finalNode = forwardNextNodeAngle.first
                            entity[ActionMoveComponent.mapper]!!.finalAngle = forwardNextNodeAngle.second

                            entity[ActionMoveComponent.mapper]!!.beganByMoving = ActionMoveComponent.Direction.FORWARD
                        } else { //initiate movement from position between nodes
//                            println("initiate forward from current position")
                            if (entity[ActionMoveComponent.mapper]!!.beganByMoving == ActionMoveComponent.Direction.BACKWARD) {
                                //we began by moving backwards
//                                println("we began by moving backward")
                                entity[ActionMoveComponent.mapper]!!.stepPath = Pair(Node(position = currentPosition), currentNode).buildNodeLine(noise = modForwardPathNoise, linkDistance = modForwardDistancePerStep)
                                entity[ActionMoveComponent.mapper]!!.forwardStepEasing = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.size
                                entity[ActionMoveComponent.mapper]!!.finalNode = currentNode
                                entity[ActionMoveComponent.mapper]!!.finalAngle = currentAngle
                            } else {
                                //we began by moving forwards
//                                println("we began by moving forward")
                                entity[ActionMoveComponent.mapper]!!.stepPath = Pair(Node(position=currentPosition), forwardNextNodeAngle.first).buildNodeLine(noise = modForwardPathNoise, linkDistance = modForwardDistancePerStep)
                                entity[ActionMoveComponent.mapper]!!.forwardStepEasing = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.size
                                entity[ActionMoveComponent.mapper]!!.finalNode = forwardNextNodeAngle.first
                                entity[ActionMoveComponent.mapper]!!.finalAngle = forwardNextNodeAngle.second
                            }
                        }
                    }

//                    entity[ActionMoveComponent.mapper]!!.currentNodeRoom = currentNodeRoom.nodeLinks.getNodeLink(currentNode.uuid, forwardNextNodeAngle.first.uuid)!!
                }
                ActionMoveComponent.Direction.BACKWARD -> {
                    if (!entity[ActionMoveComponent.mapper]!!.moveComplete() ) {
                        if (entity[ActionMoveComponent.mapper]!!.forwardStepEasing > 0) {
//                            println("halt forward movement")
                            entity[ActionMoveComponent.mapper]!!.halt()
                        }
                    } else { //initiate movement to next node
                        //initiate movement from currentNode
                        if (entity[ActionMoveComponent.mapper]!!.currentPosition == entity[ActionMoveComponent.mapper]!!.currentNode.position) {
                            //back to the wall
                            if (currentNode != backwardNextNodeAngle.first) {
      //                          println("initiate backward from current node")
                                entity[ActionMoveComponent.mapper]!!.stepPath = Pair(currentNode, backwardNextNodeAngle.first).buildNodeLine(noise = modBackwardPathNoise, linkDistance = modBackwardDistancePerStep)
                                entity[ActionMoveComponent.mapper]!!.backwardStepEasing = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.size
                                entity[ActionMoveComponent.mapper]!!.finalNode = backwardNextNodeAngle.first
                                entity[ActionMoveComponent.mapper]!!.finalAngle = entity[ActionMoveComponent.mapper]!!.finalNode.angleBetween(currentNode)

                                entity[ActionMoveComponent.mapper]!!.beganByMoving = ActionMoveComponent.Direction.BACKWARD
                                entity[ActionMoveComponent.mapper]!!.currentNodeLink = nodeRoomMesh.nodeLinks.getNodeLink(currentNode.uuid, forwardNextNodeAngle.first.uuid)!!
                            }
                        } else { //initiate movement from position between nodes
//                            println("initiate backward from current position")
                            //we began by moving forwards
                            if (entity[ActionMoveComponent.mapper]!!.beganByMoving == ActionMoveComponent.Direction.FORWARD) {
  //                              println("we began by moving forward")
                                entity[ActionMoveComponent.mapper]!!.stepPath = Pair(Node(position = currentPosition), currentNode).buildNodeLine(noise = modBackwardPathNoise, linkDistance = modBackwardDistancePerStep)
                                entity[ActionMoveComponent.mapper]!!.backwardStepEasing = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.size
                                entity[ActionMoveComponent.mapper]!!.finalNode = currentNode
                                entity[ActionMoveComponent.mapper]!!.finalAngle = currentAngle
                            } else { //we began by moving backwards
    //                            println("we began by moving backward")
                                entity[ActionMoveComponent.mapper]!!.stepPath = Pair(Node(position = currentPosition), backwardNextNodeAngle.first).buildNodeLine(noise = modBackwardPathNoise, linkDistance = modBackwardDistancePerStep)
                                entity[ActionMoveComponent.mapper]!!.backwardStepEasing = entity[ActionMoveComponent.mapper]!!.stepPath.nodes.size
                                entity[ActionMoveComponent.mapper]!!.finalNode = backwardNextNodeAngle.first
                                entity[ActionMoveComponent.mapper]!!.finalAngle = entity[ActionMoveComponent.mapper]!!.finalNode.angleBetween(currentNode)
                            }
                        }
                    }
                }
                ActionMoveComponent.Direction.LEFT -> {
                    if (!entity[ActionMoveComponent.mapper]!!.moveComplete() ) {
                        if (entity[ActionMoveComponent.mapper]!!.rightTurnEasing > 0) {
                            entity[ActionMoveComponent.mapper]!!.rightTurnEasing = 0f
                        }
                    } else { // initiate turn
                        entity[ActionMoveComponent.mapper]!!.leftTurnEasing = currentAngle.leftAngleBetween(entity[ActionMoveComponent.mapper]!!.leftNextAngle)
                    }
                }
                ActionMoveComponent.Direction.RIGHT -> {
                    if (!entity[ActionMoveComponent.mapper]!!.moveComplete() ) {
                        if ( entity[ActionMoveComponent.mapper]!!.leftTurnEasing > 0) {
                            entity[ActionMoveComponent.mapper]!!.leftTurnEasing = 0f
                        }
                    } else { // initiate turn
                        entity[ActionMoveComponent.mapper]!!.rightTurnEasing = currentAngle.rightAngleBetween(entity[ActionMoveComponent.mapper]!!.rightNextAngle)
                    }
                }
            }

            entity[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.NONE
            entity[ActionMoveComponent.mapper]!!.currentNodeRoom = nodeRoomMesh.getNodeRoom(currentNode)

//            println ("entity ${entity.getEntityComponent().name} moves to ${entity[ActionMoveComponent.mapper]!!.currentNode.position}.")

            entity[ActionMoveComponent.mapper]!!.executed = true
        }
    }

}
