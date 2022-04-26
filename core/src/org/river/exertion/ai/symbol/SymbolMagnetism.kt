package org.river.exertion.ai.symbol


enum class SymbolMagnetism {

    ATTRACT,
    REPEL {override fun targetPosition() = 1f},
    STABILIZE_NEAR {override fun targetPosition() = .25f},
    STABILIZE_MID {override fun targetPosition() = .5f},
    STABILIZE_FAR {override fun targetPosition() = .75f},
    NONE
    ;
    open fun targetPosition() = 0f
}