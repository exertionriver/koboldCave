package org.river.exertion.ai.attributes

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect

data class AttributeRange <T:Any>(private val attributeType : Class<IAttribute<T>>, val noumenonOrder : Int = 0, val minValue: T?, val maxValue: T?) {

    val attribute: IAttribute<T> = attributeType.kotlin.objectInstance!!

    fun getRangeValues() : List<AttributeValue<T>> {
        val rangeValues = attribute.values().toMutableList()

        if (maxValue != null) rangeValues.removeAll( attribute.values().filter { (it.value as Comparable<T>) > maxValue } )
        if (minValue != null) rangeValues.removeAll( attribute.values().filter { (it.value as Comparable<T>) < minValue } )

        return rangeValues
    }

    fun getRandomRangeAttributeValue() : AttributeValue<T> = ProbabilitySelect( getRangeValues().map { it }.associateWith { Probability(100f / getRangeValues().size, 0f) } ).getSelectedProbability()!!

    companion object {
        fun List<AttributeRange<*>>.mergeOverrideAttributeRanges(thisAttributeRanges : List<AttributeRange<*>>) : List<AttributeRange<*>> {

            val returnList = thisAttributeRanges.toMutableList()
            val thisTags = thisAttributeRanges.map { it.attribute.tag() }

            this.forEach { attributeRange -> if (!thisTags.contains(attributeRange.attribute.tag())) returnList.add(attributeRange) }
            return returnList
        }

        fun List<AttributeRange<*>>.getRandomAttributes() : List<AttributeInstance<*>> {
            val returnList = mutableListOf<AttributeInstance<*>>()

            this.forEach { returnList.add(AttributeInstance(it.attribute.javaClass, it.getRandomRangeAttributeValue(), it.noumenonOrder)) }

            return returnList.sortedBy { it.noumenonOrder }
        }
    }

}