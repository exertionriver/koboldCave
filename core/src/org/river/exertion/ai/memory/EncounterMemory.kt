package org.river.exertion.ai.memory

import com.badlogic.gdx.ai.msg.Telegraph

class EncounterMemory(override var entity : Telegraph) : IMemory {

    override var noumenaRegister = mutableSetOf<MemoryInstance>()
}