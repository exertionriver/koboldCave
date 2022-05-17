package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import org.river.exertion.ecs.component.action.ActionDestantiateComponent
import org.river.exertion.ecs.component.action.core.ActionComponent

class ActionSystem : IteratingSystem(allOf(ActionComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {



    }
}
