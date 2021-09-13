package org.river.exertion

import org.river.exertion.koboldQueue.action.Action
import org.river.exertion.koboldQueue.action.actions.Destantiate
import org.river.exertion.koboldQueue.action.actions.Instantiate
import com.badlogic.gdx.graphics.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import org.river.exertion.ecs.template.IInstance
import org.river.exertion.koboldQueue.condition.Condition
import render.RenderActionPlex
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
suspend fun instantiate(lambda: Instantiate.InstantiateParamList.() -> Unit) = Action.Immediate.execute( action = Instantiate, actionParamList = Instantiate.InstantiateParamList()
    .apply(lambda).actionParamList() )

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
suspend fun destantiate(lambda: Destantiate.DestantiateParamList.() -> Unit) = Action.Immediate.execute( action = Destantiate, actionParamList = Destantiate.DestantiateParamList()
    .apply(lambda).actionParamList() )

inline fun <reified T> ParamList.param(index : Int) : T = if (this[index] is T) this[index] as T else throw IllegalArgumentException(this.toString())

@ExperimentalUnsignedTypes
@ExperimentalCoroutinesApi
@ExperimentalTime
inline fun <reified T: Any> ParamList.fparam(index : Int) : T {
    return when {
        (this[index] is Flow<*>) -> {
            lateinit var waitVar : T

        //    while (!launch(RenderActionPlex.getCoroutineContext()) { (this[index] as Flow<*>).collect { value -> waitVar = value as T} }.isCompleted) { /*wait for flow*/ }

            waitVar
        }
        else -> if (this[index] is T) this[index] as T else throw IllegalArgumentException(this.toString())
    }
}

typealias ParamList = List<Any>

@ExperimentalTime
@ExperimentalUnsignedTypes
typealias ActionConditionsMap = Map<Action, ConditionList?>

typealias ActionDescription = () -> String
typealias ActionExecutor = (actionParams : ParamList?) -> String?
typealias ConditionDescription = () -> String
typealias ConditionEvaluator = (conditionParams : ParamList?) -> Boolean?

typealias ConditionList = List<Condition>
typealias ConditionParamMap = Map<Condition, ParamList?>

@ExperimentalUnsignedTypes
@ExperimentalTime
typealias RegisterEntries = MutableMap<IInstance, Job>