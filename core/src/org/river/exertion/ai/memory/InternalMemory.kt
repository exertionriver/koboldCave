package org.river.exertion.ai.memory

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.PerceivedPhenomenaMessage
import org.river.exertion.ai.perception.PerceivedPhenomena

class InternalMemory(var entity : Telegraph) : Telegraph {

    init {
        MessageChannel.INT_MEMORY.enableReceive(this)
    }

    var activeMemory = ActiveMemory(entity)
    var encounterMemory = EncounterMemory(entity)
    var longtermMemory = LongtermMemory(entity)

    var perceivedPhenomena = mutableListOf<PerceivedPhenomena>()

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_MEMORY.id()) {
                perceivedPhenomena = (MessageChannel.INT_MEMORY.receiveMessage(msg.extraInfo) as PerceivedPhenomenaMessage).perceivedPhenomena ?: perceivedPhenomena
            }
        }
        return true
    }
}