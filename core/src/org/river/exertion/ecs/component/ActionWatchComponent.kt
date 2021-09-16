package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class ActionWatchComponent(initBase : Boolean = false) : ActionComponent(initBase)  {

    companion object {
        val mapper = mapperFor<ActionWatchComponent>()
    }

}