package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.ecs.component.action.IdleActionComponent
import org.river.exertion.ecs.component.MomentComponent
import org.river.exertion.ecs.component.action.core.ActionComponent
import org.river.exertion.ecs.component.action.core.ActionType
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.ecs.system.ActionSystem
import org.river.exertion.logDebug

class IdleActionSystem : IteratingSystem(allOf(IdleActionComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( ActionComponent.currentActionReady(entity, ActionType.IDLE) ) {

            logDebug ("entity ${IEntity.getFor(entity)!!.entityName}", "putters around for a bit..")

            ActionComponent.currentActionCompleted(entity)
        }
    }
}
