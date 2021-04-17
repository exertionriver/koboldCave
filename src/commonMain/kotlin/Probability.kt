import com.soywiz.klock.DateTime
import kotlin.random.Random

@ExperimentalUnsignedTypes
class Probability(
    val mean: Double, val range: Int
    , val meanIncreaseSpeed: Double = 0.0, val meanIncreaseAccel: Double = 0.0
    , val rangeIncreaseSpeed: Int = 0, val rangeIncreaseAccel: Int = 0
    ) {

    //to do: exclude mean and range, e.g. for angles
    constructor(mean : Int, range : Int) : this (
        mean = mean.toDouble(), range = range
    )

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

    fun getVariance() = if (range == 0) 0 else ( Random.nextInt((range * 2 + 1)) - range )

    fun getValue() = mean + getVariance()

    override fun toString() = "${Probability::class.simpleName} ($mean, $range) : ${getVariance()}, ${getValue()}"

    companion object {

        val All = Probability(100, 0)
        val Even = Probability(50, 50)
        val None = Probability(0, 0)

        private fun getMod(countElapsed : Int, original : Double, mod : Double) : Double {
            var returnMod = original

            for (idx in 0..countElapsed) returnMod += mod

            return returnMod
        }

        private fun getMod(countElapsed : Int, original : Int, mod : Int) : Int =
            getMod(countElapsed, original.toDouble(), mod.toDouble()).toInt()
    }

}
