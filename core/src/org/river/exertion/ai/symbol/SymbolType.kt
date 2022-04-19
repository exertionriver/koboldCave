package org.river.exertion.ai.symbol

import org.river.exertion.ai.condition.ConditionInstance
import org.river.exertion.ai.condition.ConditionInstance.Companion.symbolType
import java.util.concurrent.locks.Condition

enum class SymbolType : ReferentType {

    MY_LIFE { override fun tag() = "my life"},
    GOOD_HEALTH { override fun tag() = "good health"; fun resolveTo(conditionInstance: ConditionInstance) = conditionInstance.symbolType()},
    UNKNOWN_HEALTH { override fun tag() = "unknown health"; fun resolveTo(conditionInstance: ConditionInstance) = conditionInstance.symbolType()},
    BEST_THING { override fun tag() = "best thing"},
    THREAT { override fun tag() = "threat" },
    OPPORTUNITY { override fun tag() = "opportunity" },
    ANNOYANCE { override fun tag() = "annoyance" },
    KINSHIP { override fun tag() = "kinship" },

    CONTINUED_SUCCESS { override fun tag() = "continued failure" },
    CONTINUED_FAILURE { override fun tag() = "continued success" },

    SHINY_THING { override fun tag() = "shiny thing" },
    SHINY_THING_OBTAINED { override fun tag() = "shiny thing obtained" },

    HUNGER { override fun tag() = "hunger" },
    FOOD { override fun tag() = "food" },
    FOOD_CONSUMED { override fun tag() = "food consumed" },

    NOUMENON_BETRAYAL { override fun tag() = "noumenon betrayal" },
    NONE { override fun tag() = "none" }
    ;
    abstract fun tag() : String
    open fun resolveTo(param : Any) : SymbolType = NONE

    fun isSymbolPresent(param : Any) = this == this.resolveTo(param)
}