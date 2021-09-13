package org.river.exertion.koboldQueue.action.roles

import org.river.exertion.koboldQueue.action.actions.Idle
import org.river.exertion.koboldQueue.action.IAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.koboldQueue.action.actions.Screech
import org.river.exertion.ActionConditionsMap
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
interface IIdlor : IAction {

    override val actions: ActionConditionsMap
        get() = super.actions.plus(
            mapOf(
                Idle to null
                , Screech to null
            )
        )

}


