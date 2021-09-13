package org.river.exertion.ecs.template

import org.river.exertion.koboldQueue.action.ActionPlex
import org.river.exertion.koboldQueue.action.IActionPlex
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.koboldQueue.time.Timer
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
@ExperimentalTime
interface IInstance : IActionPlex {

    fun getInstanceId(): UUID

    fun getInstanceName(): String

    var interrupted : Boolean

    suspend fun perform(timer : Timer, instanceRegister : Register): Timer

    override var actionPlex: ActionPlex

    fun getTemplate() = object {}
}