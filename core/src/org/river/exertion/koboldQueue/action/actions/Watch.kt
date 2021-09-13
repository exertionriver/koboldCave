package org.river.exertion.koboldQueue.action.actions

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.ParamList
import org.river.exertion.ecs.template.IInstance
import org.river.exertion.ecs.template.Register
import org.river.exertion.koboldQueue.action.Action
import org.river.exertion.koboldQueue.action.ActionType
import org.river.exertion.param
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
object Watch : Action(actionLabel = "watch"
    , momentsToPrepare = 1, momentsToExecute = 3
    , actionType = ActionType.OneTimeExec
    , plexSlotsRequired = 2
    , maxParallel = 1
    , description = fun () : String = WatchParamList().watchDescription()
    , executor = fun (watchParams : ParamList?) : String {
        return if (watchParams == null) WatchParamList().watchDescription()
        else WatchParamList(watchParams).watchDescription()
    }
) {
    class WatchParamList(var kInstance: IInstance?, var register: Register?) {

        constructor(actionParamList: ParamList) : this(
            kInstance = actionParamList.param<IInstance>(0)
            , register = actionParamList.param<Register>(1)
        )

        constructor() : this(kInstance = null, register = null)

        fun watchDescription() : String = "${Watch::class.simpleName} -> " +
            "IInstance named ${kInstanceNameOrT()} watches IDescribable objects " +
            "in Register ${registerNameOrT()}"

        private fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

        private fun registerNameOrT() = register?.getInstanceName() ?: Register::class.simpleName

        @Suppress("UNCHECKED_CAST")
        fun actionParamList() = listOf(kInstance, register) as ParamList
    }

    fun params(lambda: WatchParamList.() -> Unit) = WatchParamList().apply(lambda).actionParamList()
}