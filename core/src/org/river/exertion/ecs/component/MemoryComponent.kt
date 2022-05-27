package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState
import org.river.exertion.ai.memory.InternalMemory
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.perception.PerceivedPhenomena
import org.river.exertion.ecs.component.action.core.IComponent

class MemoryComponent(var entity : Telegraph) : IComponent, Component, Telegraph {

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.INT_MEMORY.id())
    }

    override val componentName = "Memory"

    var internalMemory = InternalMemory()
    var perceivedPhenomena = mutableListOf<PerceivedPhenomena>()
    var internalFacetInstancesState = InternalFacetInstancesState()

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_MEMORY.id()) {
                perceivedPhenomena = msg.extraInfo as MutableList<PerceivedPhenomena>
            }
            if (msg.message == MessageChannel.INT_MEMORY_FACETS.id()) {
                internalFacetInstancesState = msg.extraInfo as InternalFacetInstancesState
            }
        }
        return true
    }

    companion object {
        val mapper = mapperFor<MemoryComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is MemoryComponent } != null
        fun getFor(entity : Entity) : MemoryComponent? = if (has(entity)) entity.components.first { it is MemoryComponent } as MemoryComponent else null

    }

}