package org.river.exertion

import kotlin.math.pow
import kotlin.random.Random

class ProbabilitySelect<T>(val probabilities : Map<T, Probability>) {

    private val totalPossibility = probabilities.values.sumOf { it.getValue().toDouble() }.toFloat()

    val selectedProbability = Random.nextInt(totalPossibility.toInt())

    fun getSelectedProbability() : T? {

        var accumulator = 0.0

        for(probability in probabilities) {
            accumulator += probability.value.getValue()
            if (accumulator >= selectedProbability) return probability.key
        }

        return null
    }

    override fun toString() = "org.river.exertion.ProbabilitySelect(${totalPossibility}, ${selectedProbability}) : ${getSelectedProbability()}"

    companion object {
        fun <T> psAccumulating(entries : List<T>) : ProbabilitySelect<T> {

            val pSelects = entries.map {
                it to Probability(2F.pow(entries.size - 1 - entries.indexOf(it)), 0F)
            }.toMap()

            return ProbabilitySelect(pSelects)
        }
    }
}
