package org.river.exertion.ai.internalSymbol.core


enum class SymbolTargetPosition {

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
    open fun atTargetPosition(position : Float) : Boolean = (position * 100).toInt() == (targetPosition() * 100).toInt()
    open fun ltTargetPosition(position : Float) : Boolean = (position * 100).toInt() < (targetPosition() * 100).toInt()
    open fun gtTargetPosition(position : Float) : Boolean = (position * 100).toInt() > (targetPosition() * 100).toInt()

    companion object {
        fun atTargetPosition(refPosition : Float, checkPosition : Float) : Boolean = (refPosition * 100).toInt() == (checkPosition * 100).toInt()
        fun ltTargetPosition(refPosition : Float, checkPosition : Float) : Boolean = (refPosition * 100).toInt() < (checkPosition * 100).toInt()
        fun gtTargetPosition(refPosition : Float, checkPosition : Float) : Boolean = (refPosition * 100).toInt() > (checkPosition * 100).toInt()
    }
}