package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class ActionReflectComponent(initBase : Boolean = false) : ActionComponent(initBase) {

    companion object {
        val mapper = mapperFor<ActionReflectComponent>()
    }
}