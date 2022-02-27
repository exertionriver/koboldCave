package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.ecs.component.action.*
import org.river.exertion.geom.node.NodeLink.Companion.getNextNodeAngle

class ActionSimpleMoveSystem : IteratingSystem(allOf(ActionSimpleMoveComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( MomentComponent.has(entity) && entity[MomentComponent.mapper]!!.ready()) {
//            entity[MomentComponent.mapper]!!.reset(this.javaClass.name)

            val currentNodeRoom = entity[ActionSimpleMoveComponent.mapper]!!.currentNodeRoom
            val currentNode = entity[ActionSimpleMoveComponent.mapper]!!.currentNode
            val currentAngle = entity[ActionSimpleMoveComponent.mapper]!!.currentAngle

            val forwardNextNodeAngle = currentNodeRoom.nodeLinks.getNextNodeAngle(currentNodeRoom.nodes, currentNode, currentAngle)
 //           val backwardNextNodeAngle = currentNodeRoom.nodeLinks.getNextNodeAngle(currentNodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.BACKWARD)
 //           val leftNextAngle = currentNodeRoom.nodeLinks.getNextAngle(currentNodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.LEFT )
 //           val rightNextAngle = currentNodeRoom.nodeLinks.getNextAngle(currentNodeRoom.nodes.toMutableList(), currentNode, currentAngle, NodeLink.NextAngle.RIGHT )

            entity[ActionSimpleMoveComponent.mapper]!!.currentNode = forwardNextNodeAngle.first
            entity[ActionSimpleMoveComponent.mapper]!!.currentPosition = forwardNextNodeAngle.first.position
            entity[ActionSimpleMoveComponent.mapper]!!.currentAngle = forwardNextNodeAngle.second

//            println ("entity ${entity.getEntityComponent().name} moves to ${entity[ActionSimpleMoveComponent.mapper]!!.currentNode.position}.")
        }
    }


}
