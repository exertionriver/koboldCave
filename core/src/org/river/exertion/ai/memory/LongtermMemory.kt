package org.river.exertion.ai.memory

import org.river.exertion.ai.perception.PerceivedNoumenon

class LongtermMemory() : IMemory {

    //populated to begin, updated by resolution, other information
    override var noumenaRegister = mutableListOf<PerceivedNoumenon>()

}