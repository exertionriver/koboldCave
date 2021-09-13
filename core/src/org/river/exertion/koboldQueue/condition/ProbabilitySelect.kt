package org.river.exertion.koboldQueue.condition

import kotlin.math.pow
import kotlin.random.Random

@ExperimentalUnsignedTypes
class ProbabilitySelect<T>(val probabilities : Map<T, Probability>) {

    private val totalPossibility = probabilities.values.sumOf { it.getValue() }

    val selectedProbability = Random.nextInt(totalPossibility.toInt())

    fun getSelectedProbability() : T? {

        var accumulator = 0.0

        for(probabilty in probabilities) {
            accumulator += probabilty.value.getValue()
            if (accumulator >= selectedProbability) return probabilty.key
        }

        return null
    }

    override fun toString() = "conditions.ProbabilitySelect(${totalPossibility}, ${selectedProbability}) : ${getSelectedProbability()}"

    companion object {
        fun <T> psAccumulating(entries : List<T>) : ProbabilitySelect<T> {

            val pSelects = entries.map {
                it to Probability(2.0.pow(entries.size - 1 - entries.indexOf(it)), 0)
            }.toMap()

            return ProbabilitySelect(pSelects)
        }
    }
}
