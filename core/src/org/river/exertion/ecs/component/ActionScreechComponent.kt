package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class ActionScreechComponent(initBase : Boolean = false) : ActionComponent(initBase)  {

    companion object {
        val mapper = mapperFor<ActionScreechComponent>()
    }

}