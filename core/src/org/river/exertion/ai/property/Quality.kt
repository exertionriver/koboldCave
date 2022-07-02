package org.river.exertion.ai.property

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.NoneNoumenon

data class Quality <T:Any>(var propertyObj: IProperty<T>, var noumenonObj: INoumenon, var noumenonOrder : Int = 0, var minValue: T? = null, var maxValue: T? = null) {

//    fun property() : IProperty<T> = propertyObj.kotlin.objectInstance!!
//    fun noumenon() : INoumenon = noumenonObj.kotlin.objectInstance!!

    fun getPropertyValues() : List<PropertyValue<T>> {
        val rangeValues = propertyObj.propertyValues().toMutableList()

        if (maxValue != null) rangeValues.removeAll( propertyObj.propertyValues().filter { (it.value as Comparable<T>) > maxValue!! } )
        if (minValue != null) rangeValues.removeAll( propertyObj.propertyValues().filter { (it.value as Comparable<T>) < minValue!! } )

        return rangeValues
    }

    fun getRandomPropertyValue() : PropertyValue<T> = ProbabilitySelect( getPropertyValues().map { it }.associateWith { Probability(100f / getPropertyValues().size, 0f) } ).getSelectedProbability()!!

    companion object {
        fun List<Quality<*>>.mergeOverrideQualities(thisQualities : List<Quality<*>>) : List<Quality<*>> {

            val returnList = thisQualities.toMutableList()
            val thisTags = thisQualities.map { it.propertyObj.type() }

            this.forEach { quality -> if (!thisTags.contains(quality.propertyObj.type())) returnList.add(quality) }
            return returnList
        }

        fun List<Quality<*>>.getRandomFeatures() : List<PropertyInstance<*>> {
            val returnList = mutableListOf<PropertyInstance<*>>()

            this.forEach { returnList.add(PropertyInstance(it.propertyObj, it.noumenonObj, it.getRandomPropertyValue(), it.noumenonOrder)) }

            return returnList.sortedBy { it.noumenonOrder }
        }

    }

}