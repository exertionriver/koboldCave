package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.MessageIds
import org.river.exertion.ai.internalSymbol.core.SymbologyInstance
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.ecs.component.action.core.IComponent
import org.river.exertion.ecs.entity.IEntity

class SymbologyComponent(var entity : Telegraph) : IComponent, Component, Telegraph {

    init {
        MessageManager.getInstance().addListener(this, MessageIds.INT_SYMBOL.id())
        MessageManager.getInstance().addListener(this, MessageIds.INT_FOCUS.id())
    }

    override val componentName = "Symbology"

    var internalSymbology = SymbologyInstance()

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageIds.INT_SYMBOL.id()) {
            }
            if (msg.message == MessageIds.INT_FOCUS.id()) {
            }
        }
        return true
    }

    companion object {
        val mapper = mapperFor<SymbologyComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is SymbologyComponent } != null
        fun getFor(entity : Entity) : SymbologyComponent? = if (has(entity)) entity.components.first { it is SymbologyComponent } as SymbologyComponent else null

    }

}