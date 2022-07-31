package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import org.river.exertion.ai.noumena.core.NoumenonInstance
import org.river.exertion.ecs.component.action.core.IComponent

class IdentityComponent(val noumenonInstance : NoumenonInstance) : IComponent, Component {

    override val componentName = "Identity"

    companion object {
        val mapper = mapperFor<IdentityComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is IdentityComponent } != null
        fun getFor(entity : Entity) : IdentityComponent? = if (has(entity)) entity.components.first { it is IdentityComponent } as IdentityComponent else null
    }
}