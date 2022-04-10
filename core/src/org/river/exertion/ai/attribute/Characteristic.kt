package org.river.exertion.ai.attribute

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.NoneNoumenon

//ranged set of attribute values
data class Characteristic <T:Any>(var attributeObj: Class<IAttribute<T>> = (NoneAttribute as IAttribute<T>).javaClass, var noumenonObj: Class<INoumenon> = (NoneNoumenon as INoumenon).javaClass, var noumenonOrder : Int = 0, var minValue: T? = null, var maxValue: T? = null) {

    fun attribute() : IAttribute<T> = attributeObj.kotlin.objectInstance!!
    fun noumenon() : INoumenon = noumenonObj.kotlin.objectInstance!!

    fun getCharacteristicAttributeValues() : List<AttributeValue<T>> {
        val rangeValues = attribute().attributeValues().toMutableList()

        if (maxValue != null) rangeValues.removeAll( attribute().attributeValues().filter { (it.value as Comparable<T>) > maxValue!! } )
        if (minValue != null) rangeValues.removeAll( attribute().attributeValues().filter { (it.value as Comparable<T>) < minValue!! } )

        return rangeValues
    }

    fun getRandomCharacteristicAttributeValue() : AttributeValue<T> = ProbabilitySelect( getCharacteristicAttributeValues().map { it }.associateWith { Probability(100f / getCharacteristicAttributeValues().size, 0f) } ).getSelectedProbability()!!

    companion object {
        fun List<Characteristic<*>>.mergeOverrideCharacteristics(thisCharacteristics : List<Characteristic<*>>) : List<Characteristic<*>> {

            val returnList = thisCharacteristics.toMutableList()
            val thisTags = thisCharacteristics.map { it.attribute().type() }

            this.forEach { characteristic -> if (!thisTags.contains(characteristic.attribute().type())) returnList.add(characteristic) }
            return returnList
        }

        fun List<Characteristic<*>>.getRandomCharacteristicAttributeInstances() : List<AttributeInstance<*>> {
            val returnList = mutableListOf<AttributeInstance<*>>()

            this.forEach { returnList.add(AttributeInstance(it.attribute().javaClass, it.noumenon().javaClass, it.getRandomCharacteristicAttributeValue(), it.noumenonOrder)) }

            return returnList.sortedBy { it.noumenonOrder }
        }

    }

}