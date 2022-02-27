package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.IActionComponent

class ActionSimpleDecideMoveComponent : IActionComponent, Component {

    override val componentName = "SimpleDecideMove"

    companion object {
        val mapper = mapperFor<ActionSimpleDecideMoveComponent>()
    }
}