package org.river.exertion.koboldQueue.condition

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.ConditionDescription
import org.river.exertion.ConditionEvaluator
import org.river.exertion.ParamList
import kotlin.time.ExperimentalTime

open class Condition(val conditionLabel: String, val description : ConditionDescription, val evaluator : ConditionEvaluator) {

    //update constructor
    constructor(copyCondition : Condition
                , updConditionLabel : String = copyCondition.conditionLabel
                , updDescription : ConditionDescription = copyCondition.description
                , updEvaluator : ConditionEvaluator = copyCondition.evaluator
    ) : this (
        conditionLabel = updConditionLabel
        , description = updDescription
        , evaluator = updEvaluator
    )

    object Immediate : ICondition {
        @ExperimentalCoroutinesApi
        @ExperimentalTime
        @ExperimentalUnsignedTypes
        override suspend fun evaluate(condition: Condition, conditionParamList : ParamList?) : Boolean? {
            return super.evaluate(condition, conditionParamList)
        }
    }

    override fun toString() = "${Condition::class.simpleName}($conditionLabel, $description, evaluator())"

    override fun equals(other: Any?): Boolean {
        return this.conditionLabel == (other as Condition).conditionLabel
    }

    override fun hashCode(): Int {
        var result = conditionLabel.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + evaluator.hashCode()
        return result
    }

    companion object {
        val ConditionNone = Condition(conditionLabel = "none", description = fun() : String = "none", evaluator = fun(_: ParamList?) : Boolean = false)

    }
}
