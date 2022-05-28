package org.river.exertion.ai.internalSymbol.core


enum class SymbolMagnetism {

    REPEL_LIMINAL {override fun targetPosition() = 1f},
    STABILIZE_PERCEPTUAL {override fun targetPosition() = .8f},
    STABILIZE_SOCIAL {override fun targetPosition() = .6f},
    STABILIZE_FAMILIAR {override fun targetPosition() = .4f},
    STABILIZE_INTIMATE {override fun targetPosition() = .2f},
    STABILIZE_POSSESSION {override fun targetPosition() = .1f},
    STABILIZE_HANDLING {override fun targetPosition() = .05f},
    ATTRACT_CONSUME {override fun targetPosition() = -1f},
    NONE
    ;
    open fun targetPosition() = 0f
}