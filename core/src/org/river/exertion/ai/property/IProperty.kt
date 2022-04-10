package org.river.exertion.ai.property

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

interface IProperty <T:Any> {

    fun type() : PropertyType
    fun howPerceived() : ExternalPhenomenaType
    fun propertyValues() : List<PropertyValue<T>>

    fun getPropertyValueByOrder(order : Int) : PropertyValue<T>? = propertyValues().first { it.propertyOrder == order }

    fun getDescriptionByValue(value : Any) : String? = propertyValues().firstOrNull { it.value!! == value }?.description

    fun getDescriptions() : List<String> = propertyValues().sortedBy { it.propertyOrder }.map { it.description }

    fun getRandomPropertyValue() : PropertyValue<T> = ProbabilitySelect( propertyValues().map { it }.associateWith { Probability(100f / propertyValues().size, 0f) } ).getSelectedProbability()!!
}