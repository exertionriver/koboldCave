package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.msg.MessageManager
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.ecs.component.action.*
import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ecs.component.MomentComponent
import org.river.exertion.ecs.entity.IEntity

class ActionSimpleDecideMoveSystem : IteratingSystem(allOf(ActionSimpleDecideMoveComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( MomentComponent.has(entity) ) { //&& entity[MomentComponent.mapper]!!.ready()) {
//            entity[MomentComponent.mapper]!!.reset(this.javaClass.name)

            if (entity[ActionMoveComponent.mapper]!!.moveComplete() )
                if ( entity[ActionMoveComponent.mapper]!!.currentPosition == entity[ActionMoveComponent.mapper]!!.currentNode.position ) {
                    entity[ActionMoveComponent.mapper]!!.direction = randomNodeDirectionExcluding(entity[ActionMoveComponent.mapper]!!.direction)
//                    println("$entity node-selecting direction ${entity[ActionMoveComponent.mapper]!!.direction}")
                } else {
                    entity[ActionMoveComponent.mapper]!!.direction = randomPositionDirectionExcluding(entity[ActionMoveComponent.mapper]!!.direction)
  //                  println("$entity position-selecting direction ${entity[ActionMoveComponent.mapper]!!.direction}")
                }

//            if (entity[ActionMoveComponent.mapper]!!.direction != ActionMoveComponent.Direction.NONE)
                MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity)!!, MessageChannel.PLAN_BRIDGE.id(), "move to ${entity[ActionMoveComponent.mapper]!!.direction}")
        }
    }

    companion object {
        fun randomNodeDirectionExcluding(directionToExclude : ActionMoveComponent.Direction = ActionMoveComponent.Direction.NONE) : ActionMoveComponent.Direction {

            val directions = mutableMapOf(
                    ActionMoveComponent.Direction.FORWARD to Probability(75f, 0)
                    , ActionMoveComponent.Direction.BACKWARD to Probability(5f, 0)
                    , ActionMoveComponent.Direction.LEFT to Probability(10f, 0)
                    , ActionMoveComponent.Direction.RIGHT to Probability(10f, 0)
            )

            directions.remove(directionToExclude)

            return ProbabilitySelect(directions).getSelectedProbability()!!
        }

        fun randomPositionDirectionExcluding(directionToExclude : ActionMoveComponent.Direction = ActionMoveComponent.Direction.NONE) : ActionMoveComponent.Direction {

            val directions = mutableMapOf(
                    ActionMoveComponent.Direction.NONE to Probability(50f, 0)
                    , ActionMoveComponent.Direction.FORWARD to Probability(25f, 0)
                    , ActionMoveComponent.Direction.BACKWARD to Probability(25f, 0)
            )

            directions.remove(directionToExclude)

            return ProbabilitySelect(directions).getSelectedProbability()!!
        }
    }
}
