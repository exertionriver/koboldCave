package org.river.exertion.koboldQueue.condition

import org.river.exertion.koboldQueue.condition.SimpleCondition.Always
import org.river.exertion.koboldQueue.condition.SimpleCondition.Eq
import org.river.exertion.koboldQueue.condition.SimpleCondition.Gt
import org.river.exertion.koboldQueue.condition.SimpleCondition.Gte
import org.river.exertion.koboldQueue.condition.SimpleCondition.Lt
import org.river.exertion.koboldQueue.condition.SimpleCondition.Lte
import org.river.exertion.koboldQueue.condition.SimpleCondition.Neq
import org.river.exertion.koboldQueue.condition.SimpleCondition.Never
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.ConditionList
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalUnsignedTypes
@ExperimentalTime
interface ISimpleCondition : ICondition {

    override val conditions : ConditionList
        get() = super.conditions.plus(
            listOf(
                Always, Never, Gt, Gte, Lt, Lte, Eq, Neq
            )
        )

}