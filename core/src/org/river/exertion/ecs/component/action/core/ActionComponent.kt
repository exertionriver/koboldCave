package org.river.exertion.ecs.component.action.core

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.MomentComponent
import org.river.exertion.ecs.component.action.ActionMoveComponent

class ActionComponent : IComponent, Component {

    override val componentName = "Action"

    var actionPlan = mutableListOf<IAction>()
    var currentAction : IAction = NoneActionComponent
    var actionLog = mutableListOf<IAction>()

    companion object {
        val mapper = mapperFor<ActionComponent>()

        fun currentActionReady(entity: Entity, actionType : ActionType) =
                MomentComponent.getFor(entity)?.ready() == true &&
                getFor(entity)?.currentAction?.actionType == actionType

        fun currentActionCompleted(entity : Entity) {
            getFor(entity)!!.actionLog.add(getFor(entity)!!.currentAction)
            getFor(entity)!!.currentAction = NoneActionComponent
        }

        fun has(entity : Entity) : Boolean { return entity.components.firstOrNull{ it is ActionComponent } != null }
        fun getFor(entity : Entity) : ActionComponent? = if ( has(entity) ) entity.components.first { it is ActionComponent } as ActionComponent else null
    }
}