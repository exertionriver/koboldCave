package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.IComponent

class ActionWatchComponent : IComponent, Component {

    override val componentName = "Watch"

    companion object {
        val mapper = mapperFor<ActionWatchComponent>()
    }

}