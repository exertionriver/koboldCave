package org.river.exertion.koboldQueue.action.roles

import org.river.exertion.koboldQueue.action.IAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
interface IObservable : IAction {

    fun getDescription() : String
}
