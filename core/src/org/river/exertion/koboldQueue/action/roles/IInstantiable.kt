package org.river.exertion.koboldQueue.action.roles

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.ecs.template.IInstance
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
interface IInstantiable {

    fun getTemplateName() : String

    fun getInstance(kInstanceName : String) : IInstance

}
