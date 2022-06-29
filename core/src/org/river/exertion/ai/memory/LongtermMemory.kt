package org.river.exertion.ai.memory

import com.badlogic.gdx.ai.msg.Telegraph

class LongtermMemory(override var entity : Telegraph) : IMemory {

    //populated to begin, updated by resolution, other information
    override var noumenaRegister = mutableSetOf<MemoryInstance>()

}