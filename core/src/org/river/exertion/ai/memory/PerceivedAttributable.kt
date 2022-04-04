package org.river.exertion.ai.memory

import org.river.exertion.ai.attributes.AttributeValue
import org.river.exertion.ai.attributes.NoneAttribute
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance

class PerceivedAttributable() {

    var attributableTag : String = NoneAttribute.tag()
    lateinit var attributeValue : AttributeValue<*>

    var isNamed = false
    var perceivedNoumenaTags = mutableListOf<String>()

    var count = 0 // times perceived

    var knowledgeSource = KnowledgeSource()
    var internalPhenomenaInstance = InternalPhenomenaInstance()

    companion object {
        fun perceivedAttributable(noumenonTag : String, attributableTag : String, attributeValue : AttributeValue<*>, knowledgeSource : KnowledgeSource, internalPhenomenaInstance : InternalPhenomenaInstance) : PerceivedAttributable {

            val newPA = PerceivedAttributable()

            newPA.attributableTag = attributableTag
            newPA.attributeValue = attributeValue
            newPA.perceivedNoumenaTags.add(noumenonTag)
            newPA.knowledgeSource = knowledgeSource
            newPA.internalPhenomenaInstance = internalPhenomenaInstance

            return newPA
        }
    }
}