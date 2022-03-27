package org.river.exertion.ai.memory

import org.river.exertion.ai.attributes.AttributeValue
import org.river.exertion.ai.attributes.NoneAttributable
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance

class PerceivedAttributable() {

    var attributableTag : String = NoneAttributable.tag()
    lateinit var attributeValue : AttributeValue<*>

    var isNamed = false
    var perceivedNoumenaTags = mutableListOf<String>()

    var count = 0 // times perceived

    var knowledgeSource = KnowledgeSource()
    var internalPhenomenaInstance = InternalPhenomenaInstance()

}