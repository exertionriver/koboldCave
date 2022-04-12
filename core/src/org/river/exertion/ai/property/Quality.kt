package org.river.exertion.ai.property

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.NoneNoumenon

data class Quality <T:Any>(var propertyObj: Class<IProperty<T>> = (NoneProperty as IProperty<T>).javaClass, var noumenonObj: Class<INoumenon> = (NoneNoumenon as INoumenon).javaClass, var noumenonOrder : Int = 0, var minValue: T? = null, var maxValue: T? = null) {

    fun property() : IProperty<T> = propertyObj.kotlin.objectInstance!!
    fun noumenon() : INoumenon = noumenonObj.kotlin.objectInstance!!

    fun getPropertyValues() : List<PropertyValue<T>> {
        val rangeValues = property().propertyValues().toMutableList()

        if (maxValue != null) rangeValues.removeAll( property().propertyValues().filter { (it.value as Comparable<T>) > maxValue!! } )
        if (minValue != null) rangeValues.removeAll( property().propertyValues().filter { (it.value as Comparable<T>) < minValue!! } )

        return rangeValues
    }

    fun getRandomPropertyValue() : PropertyValue<T> = ProbabilitySelect( getPropertyValues().map { it }.associateWith { Probability(100f / getPropertyValues().size, 0f) } ).getSelectedProbability()!!

    companion object {
        fun List<Quality<*>>.mergeOverrideQualities(thisQualities : List<Quality<*>>) : List<Quality<*>> {

            val returnList = thisQualities.toMutableList()
            val thisTags = thisQualities.map { it.property().type() }

            this.forEach { quality -> if (!thisTags.contains(quality.property().type())) returnList.add(quality) }
            return returnList
        }

        fun List<Quality<*>>.getRandomFeatures() : List<PropertyInstance<*>> {
            val returnList = mutableListOf<PropertyInstance<*>>()

            this.forEach { returnList.add(PropertyInstance(it.property().javaClass, it.noumenon().javaClass, it.getRandomPropertyValue(), it.noumenonOrder)) }

            return returnList.sortedBy { it.noumenonOrder }
        }

    }

}