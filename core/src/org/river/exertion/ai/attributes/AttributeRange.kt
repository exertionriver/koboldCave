package org.river.exertion.ai.attributes

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.NoneNoumenon

data class AttributeRange <T:Any>(var attributeObj: Class<IAttribute<T>> = (NoneAttribute as IAttribute<T>).javaClass, var noumenonObj: Class<INoumenon> = (NoneNoumenon as INoumenon).javaClass, var noumenonOrder : Int = 0, var minValue: T? = null, var maxValue: T? = null) {

    fun attribute() : IAttribute<T> = attributeObj.kotlin.objectInstance!!
    fun noumenon() : INoumenon = noumenonObj.kotlin.objectInstance!!

    fun getRangeValues() : List<AttributeValue<T>> {
        val rangeValues = attribute().values().toMutableList()

        if (maxValue != null) rangeValues.removeAll( attribute().values().filter { (it.value as Comparable<T>) > maxValue!! } )
        if (minValue != null) rangeValues.removeAll( attribute().values().filter { (it.value as Comparable<T>) < minValue!! } )

        return rangeValues
    }

    fun getRandomRangeAttributeValue() : AttributeValue<T> = ProbabilitySelect( getRangeValues().map { it }.associateWith { Probability(100f / getRangeValues().size, 0f) } ).getSelectedProbability()!!

    companion object {
        fun List<AttributeRange<*>>.mergeOverrideAttributeRanges(thisAttributeRanges : List<AttributeRange<*>>) : List<AttributeRange<*>> {

            val returnList = thisAttributeRanges.toMutableList()
            val thisTags = thisAttributeRanges.map { it.attribute().type() }

            this.forEach { attributeRange -> if (!thisTags.contains(attributeRange.attribute().type())) returnList.add(attributeRange) }
            return returnList
        }

        fun List<AttributeRange<*>>.getRandomAttributes() : List<AttributeInstance<*>> {
            val returnList = mutableListOf<AttributeInstance<*>>()

            this.forEach { returnList.add(AttributeInstance(it.attribute().javaClass, it.noumenon().javaClass, it.getRandomRangeAttributeValue(), it.noumenonOrder)) }

            return returnList.sortedBy { it.noumenonOrder }
        }

    }

}