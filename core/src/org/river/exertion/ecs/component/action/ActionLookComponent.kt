package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.IActionComponent

class ActionLookComponent : IActionComponent, Component {

    override val componentName = "Look"

    companion object {
        val mapper = mapperFor<ActionLookComponent>()
    }
}