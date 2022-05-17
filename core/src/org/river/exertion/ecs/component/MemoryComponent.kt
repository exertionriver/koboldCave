package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import org.river.exertion.ai.memory.InternalMemory
import org.river.exertion.ecs.component.action.core.IComponent

class MemoryComponent() : IComponent, Component {

    override val componentName = "Memory"

    var internalMemory = InternalMemory()

    companion object {
        val mapper = mapperFor<MemoryComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is MemoryComponent } != null
        fun getFor(entity : Entity) : MemoryComponent? = if (has(entity)) entity.components.first { it is MemoryComponent } as MemoryComponent else null

    }

}