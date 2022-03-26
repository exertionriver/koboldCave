package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.IActionComponent
import kotlin.reflect.KClass

class ActionIdleComponent : IActionComponent, Component {

    override val componentName = "Idle"

    companion object {
        val mapper = mapperFor<ActionIdleComponent>()
    }
}