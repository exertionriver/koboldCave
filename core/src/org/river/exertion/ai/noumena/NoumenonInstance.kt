package org.river.exertion.ai.noumena

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.attributes.*
import org.river.exertion.ai.attributes.AttributeRange.Companion.getRandomAttributes
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

class NoumenonInstance(sourceNoumenonType : Class<InstantiatableNoumenon>, var instanceName : String) {

    val sourceNoumenon: InstantiatableNoumenon = sourceNoumenonType.kotlin.objectInstance!!
    var attributeInstances = sourceNoumenon.attributeRange().getRandomAttributes()

    private fun filteredAttributes(externalPhenomenaType: ExternalPhenomenaType?) =
        if (externalPhenomenaType == null) attributeInstances else attributeInstances.filter { attrInst -> attrInst.attribute().howPerceived() == externalPhenomenaType }

    fun pollRandomAttribute(externalPhenomenaType: ExternalPhenomenaType? = null) : AttributeInstance<*>? {
        val filteredAttributes = filteredAttributes(externalPhenomenaType)

        return if (filteredAttributes.isNotEmpty()) {
            return ProbabilitySelect( filteredAttributes.map { it }.associateWith { Probability(100f / filteredAttributes.size, 0f) } ).getSelectedProbability()!!
        } else null
    }
}