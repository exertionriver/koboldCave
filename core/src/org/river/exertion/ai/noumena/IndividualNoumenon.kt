package org.river.exertion.ai.noumena

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.attributes.AttributeValue
import org.river.exertion.ai.attributes.IAttributable
import org.river.exertion.ai.attributes.IntelligenceAttributable
import org.river.exertion.ai.attributes.InternalStateAttributable
import org.river.exertion.ai.noumena.INoumenon.Companion.getRandomAttributes
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

class IndividualNoumenon(val name : String, private val tags : List<String>, attributables : MutableMap<IAttributable<*>, Int> ) {

    fun tags() = tags + mutableListOf(name)
    var attributes = attributables.getRandomAttributes()

    fun filteredAttributes(externalPhenomenaType: ExternalPhenomenaType?) =
            if (externalPhenomenaType == null) attributes else attributes.filter { attr -> attr.value.first == externalPhenomenaType }

    fun pollRandomAttribute(externalPhenomenaType: ExternalPhenomenaType? = null) : Pair<String, Pair<ExternalPhenomenaType, AttributeValue<*>>>? {
        val filteredAttributes = filteredAttributes(externalPhenomenaType)

        return if (filteredAttributes.isNotEmpty()) {
            ProbabilitySelect( filteredAttributes.map { it }.associateWith { Probability(100f / filteredAttributes.size, 0f) } ).getSelectedProbability()!!.toPair()
        } else null
    }
}