package org.river.exertion.ai.attributes

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

interface IAttribute <T:Any> {

    fun type() : AttributeType
    fun howPerceived() : ExternalPhenomenaType
    fun values() : List<AttributeValue<T>>

    fun getAttributeValueByOrder(order : Int) : AttributeValue<T>? = values().first { it.attributeOrder == order }

    fun getDescriptionByValue(value : Any) : String? = values().firstOrNull { it.value!! == value }?.description

    fun getDescriptions() : List<String> = values().sortedBy { it.attributeOrder }.map { it.description }

    fun getRandomAttributeValue() : AttributeValue<T> = ProbabilitySelect( values().map { it }.associateWith { Probability(100f / values().size, 0f) } ).getSelectedProbability()!!

/*    fun List<AttributeInstance>.getRandomAttributes() : List<AttributeInstance> {
            val returnList = mutableListOf<AttributeInstance>()

            this.forEach { returnList.add(AttributeInstance(it.attribute, it.attribute, it.noumenonOrder)) }

            return returnList.sortedBy { it.noumenonOrder }
        }
    }*/
}