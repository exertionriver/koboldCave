package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.IActionComponent

class ActionReflectComponent : IActionComponent, Component {

    override val componentName = "Reflect"

    companion object {
        val mapper = mapperFor<ActionReflectComponent>()
    }
}