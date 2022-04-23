package org.river.exertion.ai.symbol

import org.river.exertion.ai.condition.ConditionInstance
import org.river.exertion.ai.condition.ConditionInstance.Companion.symbolType
import java.util.concurrent.locks.Condition

enum class SymbolMagnetism {

    ATTRACT,
    REPEL,
    STABILIZE_NEAR,
    STABILIZE_MID,
    STABILIZE_FAR,
    NONE
}