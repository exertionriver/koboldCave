package org.river.exertion

import kotlin.random.Random

class Probability(
    val mean: Float, val range: Float
    , val meanIncreaseSpeed: Float = 0F, val meanIncreaseAccel: Float = 0F
    , val rangeIncreaseSpeed: Float = 0F, val rangeIncreaseAccel: Float = 0F
    ) {

    //to do: exclude mean and range, e.g. for angles
    constructor(mean : Int, range : Int) : this (mean = mean.toFloat(), range = range.toFloat() )
    constructor(mean : Int, range : Float) : this (mean = mean.toFloat(), range = range )
    constructor(mean : Float, range : Int) : this (mean = mean, range = range.toFloat() )

    fun plus(plusProbability : Probability?) : Probability {
        if (plusProbability == null) return this

        return Probability(
            this.mean + plusProbability.mean,
            this.range + plusProbability.range,
            this.meanIncreaseSpeed + plusProbability.meanIncreaseSpeed,
            this.meanIncreaseAccel + plusProbability.meanIncreaseAccel,
            this.rangeIncreaseSpeed + plusProbability.rangeIncreaseSpeed,
            this.rangeIncreaseAccel + plusProbability.rangeIncreaseAccel
        )
    }

    @ExperimentalUnsignedTypes
    constructor(sourceProbability: Probability, modProbability: Probability) : this (
        mean = sourceProbability.mean + modProbability.mean
        , range = sourceProbability.range + modProbability.range
        , meanIncreaseSpeed = sourceProbability.meanIncreaseSpeed + modProbability.meanIncreaseAccel
        , meanIncreaseAccel = sourceProbability.meanIncreaseAccel + modProbability.meanIncreaseAccel
        , rangeIncreaseSpeed = sourceProbability.rangeIncreaseSpeed + modProbability.rangeIncreaseSpeed
        , rangeIncreaseAccel = sourceProbability.rangeIncreaseAccel + modProbability.rangeIncreaseAccel
    )

    @ExperimentalUnsignedTypes
    constructor(prevProbability: Probability, modOffset: Int) : this (
        getMod(modOffset, prevProbability.mean, getMod(modOffset, prevProbability.meanIncreaseSpeed, prevProbability.meanIncreaseAccel) )
        , getMod(modOffset, prevProbability.range, getMod(modOffset, prevProbability.rangeIncreaseSpeed, prevProbability.rangeIncreaseAccel) )
        , meanIncreaseSpeed = getMod(modOffset, prevProbability.meanIncreaseSpeed, prevProbability.meanIncreaseAccel)
        , meanIncreaseAccel = prevProbability.meanIncreaseAccel
        , rangeIncreaseSpeed = getMod(modOffset, prevProbability.rangeIncreaseSpeed, prevProbability.rangeIncreaseAccel)
        , rangeIncreaseAccel = prevProbability.rangeIncreaseAccel
    ) //e.g. modOffset = timer.initCurSecondsBetween

    fun getVariance() : Float = if (range == 0F) 0F else ( ( Random.nextFloat() * (range * 2) ) - range )

    fun getValue() : Float = mean + getVariance()

    fun getValueInt() : Int = getValue().toInt()

    override fun toString() = "${Probability::class.simpleName} ($mean, $range) : ${getVariance()}, ${getValue()}"

    companion object {

        val All = Probability(100, 0)
        val Even = Probability(50, 50)
        val None = Probability(0, 0)

        private fun getMod(countElapsed : Int, original : Float, mod : Float) : Float {
            var returnMod = original

            for (idx in 0..countElapsed) returnMod += mod

            return returnMod
        }

    }

}
