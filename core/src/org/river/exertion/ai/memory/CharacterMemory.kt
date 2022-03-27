package org.river.exertion.ai.memory

import org.river.exertion.ai.Knowable

class CharacterMemory {

    //populated to begin, updated by resolution, other information
    var associativeMemoryList = mutableListOf<AssociativeMemory>()

    fun opinions(granularity: Knowable.KnowableGranularity, onSignature : String) : List<AssociativeMemory> {
        return associativeMemoryList.filter { it.knowable.getSignatureId(granularity) == onSignature }.sortedByDescending { it.internalPhenomenaInstance.magnitude() }
    }

}