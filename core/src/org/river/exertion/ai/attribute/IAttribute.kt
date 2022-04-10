package org.river.exertion.ai.attribute

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

interface IAttribute <T:Any> {

    fun type() : AttributeType
    fun howPerceived() : ExternalPhenomenaType
    fun attributeValues() : List<AttributeValue<T>>

    fun getAttributeValueByOrder(order : Int) : AttributeValue<T>? = attributeValues().first { it.attributeOrder == order }

    fun getDescriptionByValue(value : Any) : String? = attributeValues().firstOrNull { it.value!! == value }?.description

    fun getDescriptions() : List<String> = attributeValues().sortedBy { it.attributeOrder }.map { it.description }

    fun getRandomAttributeValue() : AttributeValue<T> = ProbabilitySelect( attributeValues().map { it }.associateWith { Probability(100f / attributeValues().size, 0f) } ).getSelectedProbability()!!
}