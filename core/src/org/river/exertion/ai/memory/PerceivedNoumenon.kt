package org.river.exertion.ai.memory

import org.river.exertion.ai.attributes.AttributeValue
import org.river.exertion.ai.noumena.OtherNoumenon
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance

class PerceivedNoumenon() {

    var noumenonTag = OtherNoumenon.tag()

    var isNamed = false
    var perceivedAttributableTags = mutableListOf<String>()

    var count = 0 // times perceived

    var knowledgeSource = KnowledgeSource()
    var internalPhenomenaInstance = InternalPhenomenaInstance()

    companion object {
        fun perceivedNoumenon(noumenonTag : String, attributableTag : String, knowledgeSource : KnowledgeSource, internalPhenomenaInstance : InternalPhenomenaInstance) : PerceivedNoumenon {

            val newPN = PerceivedNoumenon()

            newPN.noumenonTag = noumenonTag
            newPN.perceivedAttributableTags.add(attributableTag)
            newPN.knowledgeSource = knowledgeSource
            newPN.internalPhenomenaInstance = internalPhenomenaInstance

            return newPN
        }
    }
}