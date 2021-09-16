package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

open class ActionComponent() : Component {

    var base = false

    constructor(initBase : Boolean) : this() {
        base = initBase
    }

}