package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.ashley.mapperFor
import org.river.exertion.koboldQueue.action.Action
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
class ActionPlexSlotComponent : Component {

    var action : Action = Action.ActionNone

    fun inUse() = action != Action.ActionNone

    companion object {
        val mapper = mapperFor<ActionPlexSlotComponent>()
    }
}