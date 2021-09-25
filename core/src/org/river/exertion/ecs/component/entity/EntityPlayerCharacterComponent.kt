package org.river.exertion.ecs.component.entity

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class EntityPlayerCharacterComponent : Component {

    companion object {
        val mapper = mapperFor<EntityPlayerCharacterComponent>()
    }
}