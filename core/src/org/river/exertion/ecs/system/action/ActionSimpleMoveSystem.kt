package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEntityComponent
import org.river.exertion.isEntity
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextNodeAngle

class ActionSimpleMoveSystem : IteratingSystem(allOf(ActionSimpleMoveComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( ActionPlexSystem.readyToExecute(entity, ActionSimpleMoveComponent.mapper) && entity.isEntity() ) {

            val currentNodeRoom = entity[ActionSimpleMoveComponent.mapper]!!.currentNodeRoom
            val currentNode = entity[ActionSimpleMoveComponent.mapper]!!.currentNode
            val currentAngle = entity[ActionSimpleMoveComponent.mapper]!!.currentAngle

            val forwardNextNodeAngle = currentNodeRoom.nodeLinks.getNextNodeAngle(currentNodeRoom.nodes.toMutableList(), currentNode, currentAngle)
 //           val backwardNextNodeAngle = currentNodeRoom.nodeLinks.getNextNodeAngle(currentNodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
 //           val leftNextAngle = currentNodeRoom.nodeLinks.getNextAngle(currentNodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
 //           val rightNextAngle = currentNodeRoom.nodeLinks.getNextAngle(currentNodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

            entity[ActionSimpleMoveComponent.mapper]!!.currentNode = forwardNextNodeAngle.first
            entity[ActionSimpleMoveComponent.mapper]!!.currentPosition = forwardNextNodeAngle.first.position
            entity[ActionSimpleMoveComponent.mapper]!!.currentAngle = forwardNextNodeAngle.second

            println ("entity ${entity.getEntityComponent().name} moves to ${entity[ActionSimpleMoveComponent.mapper]!!.currentNode.position}.")

            entity[ActionSimpleMoveComponent.mapper]!!.executed = true
        }
    }


}
