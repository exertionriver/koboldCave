package org.river.exertion

import com.badlogic.ashley.core.Entity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.river.exertion.ecs.component.entity.core.IEntity
import org.river.exertion.ecs.component.environment.core.IEnvironment
import org.river.exertion.koboldQueue.condition.Condition
import kotlin.time.ExperimentalTime

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


typealias ActionDescription = () -> String
typealias ActionExecutor = (actionParams : ParamList?) -> String?
typealias ConditionDescription = () -> String
typealias ConditionEvaluator = (conditionParams : ParamList?) -> Boolean?

typealias ConditionList = List<Condition>
typealias ConditionParamMap = Map<Condition, ParamList?>

fun Entity.isEntity() = this.components.any { it is IEntity }
fun Entity.getEntityComponent() = this.components.first { it is IEntity } as IEntity

fun Entity.isEnvironment() = this.components.any { it is IEnvironment }
fun Entity.getEnvironmentComponent() = this.components.first { it is IEnvironment } as IEnvironment


