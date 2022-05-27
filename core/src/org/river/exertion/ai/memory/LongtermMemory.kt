package org.river.exertion.ai.memory

class LongtermMemory() : IMemory {

    //populated to begin, updated by resolution, other information
    override var noumenaRegister = mutableSetOf<MemoryInstance>()

}