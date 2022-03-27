package org.river.exertion.ai.memory

import org.river.exertion.ai.phenomena.InternalPhenomenaInstance

class CharacterMemory {

    //populated to begin, updated by resolution, other information
    var associativePerceptionList = mutableListOf<PerceivedAttribute>()

    var associativeNoumenaList = mutableListOf<PerceivedNoumenon>()

    fun opinions(onTopic : String) : List<InternalPhenomenaInstance> {

        val attributePerceptions = associativePerceptionList.filter { it.attributableTag == onTopic }.sortedByDescending { it.internalPhenomenaInstance.magnitude() }

        val noumenaPerceptions = associativeNoumenaList.filter { it.noumenonTag == onTopic }.sortedByDescending { it.internalPhenomenaInstance.magnitude() }

        return attributePerceptions.map { it.internalPhenomenaInstance }.toMutableList() +
                noumenaPerceptions.map { it.internalPhenomenaInstance }.toMutableList()
    }

}