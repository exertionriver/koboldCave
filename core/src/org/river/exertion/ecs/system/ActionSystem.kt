package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import org.river.exertion.ecs.component.action.core.ActionComponent
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.logDebug

class ActionSystem : IteratingSystem(allOf(ActionComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {

        val actionPlan = ActionComponent.getFor(entity)!!.actionPlan
        val entityName = IEntity.getFor(entity)!!.entityName

        logDebug("$entityName action plan", "$actionPlan")

        if ( actionPlan.isNotEmpty() ) {
            ActionComponent.getFor(entity)!!.currentAction = actionPlan.first()
            ActionComponent.getFor(entity)!!.actionPlan.removeFirst()
        }
        val currentAction = ActionComponent.getFor(entity)!!.currentAction

        logDebug("$entityName current action", "$currentAction")
    }

}
