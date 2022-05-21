package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import org.river.exertion.ecs.component.action.DestantiateActionComponent

class DestantiateActionSystem : IteratingSystem(allOf(DestantiateActionComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {

    }
}
