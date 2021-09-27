package org.river.exertion.ecs.component.action.core

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.ActionNoneComponent
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.koboldQueue.time.Moment

class ActionPlexComponent(val actionPlexMaxSize : Int = 5, val moment : Moment = Moment(1000)) : Component {

    //in milliseconds
    var countdown = moment.milliseconds

    var slotsInUse = 0

    fun slotsAvailable() = actionPlexMaxSize - slotsInUse

    companion object {
        val mapper = mapperFor<ActionPlexComponent>()
    }
}