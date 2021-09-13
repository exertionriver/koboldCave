package org.river.exertion.koboldQueue.action.roles

import org.river.exertion.koboldQueue.action.actions.Destantiate
import org.river.exertion.koboldQueue.action.actions.Instantiate
import org.river.exertion.koboldQueue.condition.SimpleCondition.Always
import org.river.exertion.koboldQueue.condition.SimpleCondition.Never
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.ActionConditionsMap
import org.river.exertion.koboldQueue.action.IAction
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
interface IInstantiator : IAction {

    override val actions: ActionConditionsMap
        get() = super.actions.plus(
            mapOf(
                Instantiate to listOf(Never), Destantiate to listOf(Always)
            )
        )
}
