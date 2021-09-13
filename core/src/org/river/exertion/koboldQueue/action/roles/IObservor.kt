package org.river.exertion.koboldQueue.action.roles

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.koboldQueue.action.actions.Look
import org.river.exertion.koboldQueue.action.actions.Reflect
import org.river.exertion.koboldQueue.action.actions.Watch
import org.river.exertion.ActionConditionsMap
import org.river.exertion.koboldQueue.action.IAction
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
interface IObservor : IAction {

    override val actions : ActionConditionsMap
        get() = super.actions.plus(
            mapOf(
                Look to null
                , Watch to null
                , Reflect to null
            )
        )
}