package org.river.exertion.ai.noumena

import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ai.attributes.*
import org.river.exertion.ai.noumena.INoumenon.Companion.getRandomAttributes
import org.river.exertion.ai.phenomena.ExternalPhenomenaType

class IndividualNoumenon(val name : String, typeTags : MutableList<String>, typeAttributables : MutableList<Attributable> ) {

    val tags : MutableList<String> = typeTags.apply { this.add(name) }
    var attributes = typeAttributables.getRandomAttributes()

    private fun filteredAttributes(externalPhenomenaType: ExternalPhenomenaType?) =
            if (externalPhenomenaType == null) attributes else attributes.filter { attr -> attr.howPerceived == externalPhenomenaType }

    fun pollRandomAttribute(externalPhenomenaType: ExternalPhenomenaType? = null) : PolledAttribute? {
        val filteredAttributes = filteredAttributes(externalPhenomenaType)

        return if (filteredAttributes.isNotEmpty()) {
            return ProbabilitySelect( filteredAttributes.map { it }.associateWith { Probability(100f / filteredAttributes.size, 0f) } ).getSelectedProbability()!!
        } else null
    }
}