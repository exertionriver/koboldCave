package org.river.exertion.ai.memory

import org.river.exertion.ai.perception.PerceivedNoumenon

class EncounterMemory() : IMemory {

    //populated to begin, updated by resolution, other information
    override var noumenaRegister = mutableListOf<PerceivedNoumenon>()
}