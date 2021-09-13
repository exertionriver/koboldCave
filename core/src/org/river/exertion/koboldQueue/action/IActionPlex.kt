package org.river.exertion.koboldQueue.action

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.koboldQueue.time.Moment
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
interface IActionPlex {

    var actionPlex : ActionPlex

    fun getMoment() : Moment

    fun getMaxPlexSize() : Int
}

