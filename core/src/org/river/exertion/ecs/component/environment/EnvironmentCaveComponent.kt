package org.river.exertion.ecs.component.environment

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class EnvironmentCaveComponent : Component {
    companion object {
        val mapper = mapperFor<EnvironmentCaveComponent>()
    }
}