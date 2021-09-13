package org.river.exertion.koboldQueue.action.actions

import org.river.exertion.koboldQueue.action.Action
import org.river.exertion.koboldQueue.action.ActionPriority
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.ParamList
import org.river.exertion.ecs.template.IInstance
import org.river.exertion.param
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
object Reflect : Action(actionLabel = "reflect"
    , actionPriority = ActionPriority.LowSecond
    , description = fun () : String = ReflectParamList().reflectDescription()
    , executor = fun (reflectParams : ParamList?) : String {
        return if (reflectParams == null) ReflectParamList().reflectDescription()
        else ReflectParamList(reflectParams).reflectDescription()
    }
) {
    class ReflectParamList(var kInstance: IInstance?) {

        constructor(actionParamList: ParamList) : this(
            kInstance = actionParamList.param<IInstance>(0)
        )

        constructor(nullConstructor: Nothing? = null) : this(kInstance = null)

        fun reflectDescription() : String = "${Reflect::class.simpleName} -> " +
            "IInstance named ${kInstanceNameOrT()} reflects upon the situation"

        private fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

        @Suppress("UNCHECKED_CAST")
        fun actionParamList() = listOf(kInstance) as ParamList
    }

    fun params(lambda: ReflectParamList.() -> Unit) = ReflectParamList().apply(lambda).actionParamList()
}