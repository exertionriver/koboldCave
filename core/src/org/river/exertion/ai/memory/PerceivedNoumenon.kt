package org.river.exertion.ai.memory

import org.river.exertion.ai.noumena.INoumenon
import org.river.exertion.ai.noumena.OtherNoumenon
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance

class PerceivedNoumenon() {

    var noumenonTag = OtherNoumenon.tag()

    var isNamed = false
    var perceivedAttributeTags = mutableListOf<String>()

    var count = 0 // times perceived

    var knowledgeSource = KnowledgeSource()
    var internalPhenomenaInstance = InternalPhenomenaInstance()

}