package org.river.exertion.ai.attribute

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.noumena.core.INoumenon
import org.river.exertion.ai.noumena.NoneNoumenon

//ranged set of attribute values
data class Trait <T:Any>(var attributeObj: IAttribute<T>, var noumenonObj: INoumenon, var noumenonOrder : Int = 0, var minValue: T? = null, var maxValue: T? = null) {

//    fun noumenon() : INoumenon = noumenonObj.kotlin.objectInstance!!

    fun getAttributeValues() : List<AttributeValue<T>> {
        val rangeValues = attributeObj.attributeValues().toMutableList()

        if (maxValue != null) rangeValues.removeAll( attributeObj.attributeValues().filter { (it.value as Comparable<T>) > maxValue!! } )
        if (minValue != null) rangeValues.removeAll( attributeObj.attributeValues().filter { (it.value as Comparable<T>) < minValue!! } )

        return rangeValues
    }

    fun getRandomAttributeValue() : AttributeValue<T> = ProbabilitySelect( getAttributeValues().map { it }.associateWith { Probability(100f / getAttributeValues().size, 0f) } ).getSelectedProbability()!!

    companion object {
        fun List<Trait<*>>.mergeOverrideTraits(thisTraits : List<Trait<*>>) : List<Trait<*>> {

            val returnList = thisTraits.toMutableList()
            val thisTags = thisTraits.map { it.attributeObj.type() }

            this.forEach { characteristic -> if (!thisTags.contains(characteristic.attributeObj.type())) returnList.add(characteristic) }
            return returnList
        }

        fun List<Trait<*>>.getRandomCharacteristics() : List<Characteristic<*>> {
            val returnList = mutableListOf<Characteristic<*>>()

            this.forEach { returnList.add(Characteristic(it.attributeObj, it.noumenonObj, it.getRandomAttributeValue(), it.noumenonOrder)) }

            return returnList.sortedBy { it.noumenonOrder }
        }

    }

}