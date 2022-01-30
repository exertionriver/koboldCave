package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import org.river.exertion.ecs.component.action.ActionDestantiateComponent

class ActionDestantiateSystem : IteratingSystem(allOf(ActionDestantiateComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {

    }
}
