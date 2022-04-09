package org.river.exertion.ai.internalFacet

enum class FacetMagnitudeType {

    BARELY { override val maxMagnitude = 0.1f; override fun description() = "barely" },
    SLIGHTLY { override val minMagnitude = 0.1f; override val maxMagnitude = 0.2f; override fun description() = "slightly" },
    TOLERABLY { override val minMagnitude = 0.2f; override val maxMagnitude = 0.3f; override fun description() = "tolerably" },
    MODERATELY { override val minMagnitude = 0.3f; override val maxMagnitude = 0.4f; override fun description() = "moderately" },
    EVENLY { override val minMagnitude = 0.4f; override val maxMagnitude = 0.5f; override fun description() = "evenly" },
    KEENLY { override val minMagnitude = 0.5f; override val maxMagnitude = 0.6f; override fun description() = "keenly" },
    INTENSELY { override val minMagnitude = 0.6f; override val maxMagnitude = 0.7f; override fun description() = "intensely" },
    UTTERLY { override val minMagnitude = 0.7f; override val maxMagnitude = 0.8f; override fun description() = "utterly" },
    EXTREMELY { override val minMagnitude = 0.8f; override val maxMagnitude = 0.9f; override fun description() = "extremely" },
    EXCRUTIATINGLY { override val minMagnitude = 0.9f; override fun description() = "excrutiatingly" }
    ;

    open val minMagnitude : Float = 0.0f
    open val maxMagnitude : Float = 100.0f
    open fun description() : String = "not sure"

    companion object {
        fun byMagnitude(magnitude : Float) = values().filter { magnitude < it.maxMagnitude && magnitude >= it.minMagnitude }.first()
    }
}