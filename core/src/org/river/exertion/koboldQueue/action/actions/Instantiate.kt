package org.river.exertion.koboldQueue.action.actions

import org.river.exertion.koboldQueue.action.Action
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.koboldQueue.action.roles.IInstantiable
import org.river.exertion.ParamList
import org.river.exertion.ecs.template.Register
import org.river.exertion.param
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
object Instantiate : Action(actionLabel = "instantiate"
    , description = fun () : String = InstantiateParamList().instantiateDescription()
    , executor = fun (instantiateParamList : ParamList?) : String {
        if (instantiateParamList == null) return InstantiateParamList().instantiateDescription()

        InstantiateParamList(instantiateParamList).register!!.addInstance(
            kInstanceName = InstantiateParamList(instantiateParamList).kInstanceName!!
            , instanceTemplate = InstantiateParamList(instantiateParamList).template!!)
        return InstantiateParamList(instantiateParamList).instantiateDescription()
    }
) {
    class InstantiateParamList(var template : IInstantiable?, var kInstanceName : String?, var register : Register?) {

        constructor(actionParamList: ParamList) : this(
            template = actionParamList.param<IInstantiable>(0)
            , kInstanceName = actionParamList.param<String>(1)
            , register = actionParamList.param<Register>(2)
        )

        constructor() : this(template = null, kInstanceName = null, register = null)

        fun instantiateDescription() : String = "${Instantiate::class.simpleName} -> " +
                "Instantiating ${templateNameOrT()} " +
                "as IInstance named ${kInstanceNameOrT()} " +
                "in Register ${registerNameOrT()}"

        private fun templateNameOrT() = template?.getTemplateName() ?: IInstantiable::class.simpleName

        private fun kInstanceNameOrT() = kInstanceName ?: String::class.simpleName

        private fun registerNameOrT() = register?.getInstanceName() ?: Register::class.simpleName

        @Suppress("UNCHECKED_CAST")
        fun actionParamList() = listOf(template, kInstanceName, register) as ParamList
    }

    fun params(lambda: InstantiateParamList.() -> Unit) = InstantiateParamList().apply(lambda).actionParamList()

}