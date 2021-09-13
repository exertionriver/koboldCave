package org.river.exertion.koboldQueue.condition

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.river.exertion.ConditionList
import org.river.exertion.ConditionParamMap
import org.river.exertion.ParamList
import kotlin.time.ExperimentalTime

interface ICondition {

    val conditions: ConditionList
        get() = listOf()

    //call this to evaluate condition
    @ExperimentalUnsignedTypes
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    suspend fun evaluate(condition: Condition, conditionParamList : ParamList? = null) : Boolean? {

        val evalReturn = condition.evaluator(conditionParamList)

   //     GlobalChannel.logInfoChannel.send("eval return @ ${ DateTime.now() } ${condition.description}: $evalReturn")

        return evalReturn
    }

    @ExperimentalUnsignedTypes
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    suspend fun ConditionParamMap.evaluate() : Boolean? {

        // this.forEach { println ("key : ${it.key}, value : ${it.value}, eval: ${Condition.Immediate.evaluate(it.key, it.value)}") }
        // return true
        //}


        return this.map { Condition.Immediate.evaluate(it.key, it.value) }.reduce { result: Boolean?, element ->
            if (element != null) {
                result?.and(element)
            } else false
        }
    }
}
