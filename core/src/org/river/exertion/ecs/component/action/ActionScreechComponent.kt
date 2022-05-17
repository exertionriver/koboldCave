package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.IComponent

class ActionScreechComponent : IComponent, Component {

    override val componentName = "Screech"

    companion object {
        val mapper = mapperFor<ActionScreechComponent>()
    }
}