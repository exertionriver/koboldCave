package org.river.exertion.ai.attribute

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.NoneNoumenon

//ranged set of attribute values
data class Trait <T:Any>(var attributeObj: Class<IAttribute<T>> = (NoneAttribute as IAttribute<T>).javaClass, var noumenonObj: Class<INoumenon> = (NoneNoumenon as INoumenon).javaClass, var noumenonOrder : Int = 0, var minValue: T? = null, var maxValue: T? = null) {

    fun attribute() : IAttribute<T> = attributeObj.kotlin.objectInstance!!
    fun noumenon() : INoumenon = noumenonObj.kotlin.objectInstance!!

    fun getAttributeValues() : List<AttributeValue<T>> {
        val rangeValues = attribute().attributeValues().toMutableList()

        if (maxValue != null) rangeValues.removeAll( attribute().attributeValues().filter { (it.value as Comparable<T>) > maxValue!! } )
        if (minValue != null) rangeValues.removeAll( attribute().attributeValues().filter { (it.value as Comparable<T>) < minValue!! } )

        return rangeValues
    }

    fun getRandomAttributeValue() : AttributeValue<T> = ProbabilitySelect( getAttributeValues().map { it }.associateWith { Probability(100f / getAttributeValues().size, 0f) } ).getSelectedProbability()!!

    companion object {
        fun List<Trait<*>>.mergeOverrideTraits(thisTraits : List<Trait<*>>) : List<Trait<*>> {

            val returnList = thisTraits.toMutableList()
            val thisTags = thisTraits.map { it.attribute().type() }

            this.forEach { characteristic -> if (!thisTags.contains(characteristic.attribute().type())) returnList.add(characteristic) }
            return returnList
        }

        fun List<Trait<*>>.getRandomCharacteristics() : List<Characteristic<*>> {
            val returnList = mutableListOf<Characteristic<*>>()

            this.forEach { returnList.add(Characteristic(it.attribute().javaClass, it.noumenon().javaClass, it.getRandomAttributeValue(), it.noumenonOrder)) }

            return returnList.sortedBy { it.noumenonOrder }
        }

    }

}