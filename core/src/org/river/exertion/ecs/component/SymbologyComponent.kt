package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.ai.internalSymbol.core.SymbologyInstance
import org.river.exertion.ecs.component.action.core.IComponent

class SymbologyComponent(var entity : Telegraph) : IComponent, Component {

    override val componentName = "Symbology"

    var internalSymbology = SymbologyInstance(entity)

    companion object {
        val mapper = mapperFor<SymbologyComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is SymbologyComponent } != null
        fun getFor(entity : Entity) : SymbologyComponent? = if (has(entity)) entity.components.first { it is SymbologyComponent } as SymbologyComponent else null

    }

}