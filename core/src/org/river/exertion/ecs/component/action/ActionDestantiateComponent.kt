package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.IActionComponent

class ActionDestantiateComponent : IActionComponent, Component {

    override val componentName = "Destantiate"

    companion object {
        val mapper = mapperFor<ActionDestantiateComponent>()
    }
}