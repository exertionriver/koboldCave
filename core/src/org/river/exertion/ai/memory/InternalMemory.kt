package org.river.exertion.ai.memory

import com.badlogic.gdx.ai.msg.Telegraph

class InternalMemory(var entity : Telegraph) {

    val activeMemory = ActiveMemory(entity)
    val encounterMemory = EncounterMemory(entity)
    val longtermMemory = LongtermMemory(entity)

}