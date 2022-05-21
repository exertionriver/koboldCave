package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.IComponent

class ActionSimpleDecideMoveComponent : IComponent, Component {

    override val componentName = "SimpleDecideMove"

    companion object {
        val mapper = mapperFor<ActionSimpleDecideMoveComponent>()

        fun has(entity : Entity) : Boolean { return entity.components.firstOrNull{ it is ActionSimpleDecideMoveComponent } != null }
        fun getFor(entity : Entity) : ActionSimpleDecideMoveComponent? = if ( has(entity) ) entity.components.first { it is ActionSimpleDecideMoveComponent } as ActionSimpleDecideMoveComponent else null

    }
}