package org.river.exertion.koboldQueue.condition

import org.river.exertion.ParamList
import org.river.exertion.koboldQueue.condition.Condition.Companion.ConditionNone

class StateCondition(val condition : Condition, val conditionParamList: ParamList? = null) {

    //update constructor
    constructor(copyStateCondition : StateCondition
                , updCondition : Condition = copyStateCondition.condition
                , updConditionParamList : ParamList? = copyStateCondition.conditionParamList
    ) : this (
        condition = updCondition
        , conditionParamList = updConditionParamList
    )

    override fun toString() = "${StateCondition::class.simpleName}($condition, $conditionParamList)"

    companion object {
        val StateConditionNone = StateCondition(ConditionNone)
    }
}