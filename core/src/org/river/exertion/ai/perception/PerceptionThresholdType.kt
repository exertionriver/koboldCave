package org.river.exertion.ai.perception

enum class PerceptionThresholdType {

    NOT_PERCEPTIBLE { override val maxMagnitude = 0.1f },
    OTHER_PERCEPTIBLE { override val minMagnitude = 0.1f; override val maxMagnitude = 0.2f },
    ATTRIBUTE_PERCEPTIBLE { override val minMagnitude = 0.2f; override val maxMagnitude = 0.3f },
    NOUMENA_PERCEPTIBLE { override val minMagnitude = 0.3f; override val maxMagnitude = 0.4f },
    FULLY_PERCEPTIBLE { override val minMagnitude = 0.4f }
    ;

    open val minMagnitude : Float = 0.0f
    open val maxMagnitude : Float = 1.0f

    companion object {
        fun byMagnitude(magnitude : Float) = values().filter { magnitude < it.maxMagnitude && magnitude >= it.minMagnitude }.first()
    }
}