package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class PlayerCharacterComponent : Component {
    companion object {
        val mapper = mapperFor<PlayerCharacterComponent>()
    }
}