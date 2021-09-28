package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.ashley.allOf
import ktx.ashley.contains
import ktx.ashley.get
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.entity.EntityKoboldComponent
import org.river.exertion.ecs.component.entity.core.IEntityComponent
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEntityComponent
import org.river.exertion.getEnvironmentComponent
import org.river.exertion.isEntity
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextAngle
import org.river.exertion.koboldCave.node.NodeLink.Companion.getNextNodeAngle
import kotlin.time.ExperimentalTime

class ActionMoveSystem : IteratingSystem(allOf(ActionMoveComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( ActionPlexSystem.readyToExecute(entity, ActionMoveComponent.mapper) && entity.isEntity() ) {

            val forwardNextNodeAngle = entity.getEntityComponent().currentNodeRoom.nodeLinks.getNextNodeAngle(entity.getEntityComponent().currentNodeRoom.nodes.toMutableList(), entity.getEntityComponent().currentNode, entity.getEntityComponent().currentAngle)
            val backwardNextNodeAngle = entity.getEntityComponent().currentNodeRoom.nodeLinks.getNextNodeAngle(entity.getEntityComponent().currentNodeRoom.nodes.toMutableList(), entity.getEntityComponent().currentNode, entity.getEntityComponent().currentAngle, NodeLink.NextAngle.BACKWARD)
            val leftNextAngle = entity.getEntityComponent().currentNodeRoom.nodeLinks.getNextAngle(entity.getEntityComponent().currentNodeRoom.nodes.toMutableList(), entity.getEntityComponent().currentNode, entity.getEntityComponent().currentAngle, NodeLink.NextAngle.LEFT )
            val rightNextAngle = entity.getEntityComponent().currentNodeRoom.nodeLinks.getNextAngle(entity.getEntityComponent().currentNodeRoom.nodes.toMutableList(), entity.getEntityComponent().currentNode, entity.getEntityComponent().currentAngle, NodeLink.NextAngle.RIGHT )

            entity.getEntityComponent().currentNode = forwardNextNodeAngle.first
            entity.getEntityComponent().currentAngle = forwardNextNodeAngle.second

            println ("entity ${entity.getEntityComponent().name} moves to ${entity.getEntityComponent().currentNode.position}.")

            entity[ActionMoveComponent.mapper]!!.executed = true
        }
    }


}
