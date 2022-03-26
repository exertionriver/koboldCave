package org.river.exertion.ai.attributes

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect

interface IAttributable <T:Any> {

    val tag : String
    var minValue : T
    var maxValue : T

    var values : MutableList<AttributeValue<T>>

    fun getValueByOrder(order : Int) : T? = values.first { it.order == order }.value

    fun <T:Any>getDescriptionByValue(value : T) : String = values.first { it.value!! as Comparable<T> == value}.description

    fun getDescriptionByOrder(order : Int) = values.first { it.order == order}.description

    fun getMinMaxFilteredValues() : List<AttributeValue<T>> = values.filter { it.value!! as Comparable<T> <= maxValue }.filter { it.value!! as Comparable<T> >= minValue }

    fun getDescriptions() : List<String> = getMinMaxFilteredValues().sortedBy { it.order }.map { it.description }

    fun getRandomAttributeValue() : AttributeValue<T> = ProbabilitySelect( getMinMaxFilteredValues().map { it }.associateWith { Probability(100f / values.size, 0f) } ).getSelectedProbability()!!

    fun getRandomValue() : T = getRandomAttributeValue().value!!
}