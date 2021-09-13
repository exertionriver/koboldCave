package org.river.exertion.koboldQueue.action.actions

import org.river.exertion.koboldQueue.action.Action
import org.river.exertion.koboldQueue.action.ActionPriority
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import org.river.exertion.koboldQueue.action.roles.IObservable
import org.river.exertion.ParamList
import org.river.exertion.RegisterEntries
import org.river.exertion.ecs.template.IInstance
import org.river.exertion.ecs.template.Register
import org.river.exertion.param
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
object Look : Action(actionLabel = "look"
    , actionPriority = ActionPriority.LowSecond
    , description = fun () : String = LookParamList().lookDescription()
    , executor = fun (lookParams : ParamList?) : String {
        if (lookParams == null) return LookParamList().lookDescription()

        val lookObjects = LookParamList(lookParams).register!!.entries
            .filterKeys { (it is IObservable) && (it != LookParamList(lookParams).kInstance!!) } as RegisterEntries

        return if (!lookObjects.isNullOrEmpty() )
            LookParamList(lookParams).lookDescription().plus(": " +
                    lookObjects.map{ (it.key as IObservable).getDescription() }
                        .reduce{ lookResult : String, element -> lookResult.plus(" $element") })
        else
            LookParamList(lookParams).lookDescription()
        }
    ) {
        class LookParamList(var kInstance: IInstance?, var register: Register?) {

            constructor(actionParamList: ParamList) : this(
                kInstance = actionParamList.param<IInstance>(0)
                , register = actionParamList.param<Register>(1)
            )

            constructor() : this(kInstance = null, register = null)

            fun lookDescription() : String = "${Look::class.simpleName} -> " +
                "IInstance named ${kInstanceNameOrT()} looks at IDescribable objects " +
                "in Register ${registerOrT()}"

            private fun kInstanceNameOrT() = kInstance?.getInstanceName() ?: IInstance::class.simpleName

            private fun registerOrT() = register?.getInstanceName() ?: Register::class.simpleName

            @Suppress("UNCHECKED_CAST")
            fun actionParamList() = listOf(kInstance, register) as ParamList
        }

    fun params(lambda: LookParamList.() -> Unit) = LookParamList().apply(lambda).actionParamList()
}