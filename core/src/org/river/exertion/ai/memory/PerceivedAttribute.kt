package org.river.exertion.ai.memory

import org.river.exertion.ai.attributes.AttributeValue
import org.river.exertion.ai.attributes.IAttributable
import org.river.exertion.ai.attributes.NoneAttributable
import org.river.exertion.ai.noumena.OtherNoumenon
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance

class PerceivedAttribute() {

    var attributableTag : String = NoneAttributable.tag()
    lateinit var attributeValue : Any
    var perceivedNoumenaTags = mutableListOf<String>()

    var knowledgeSource = KnowledgeSource()
    var internalPhenomenaInstance = InternalPhenomenaInstance()

}