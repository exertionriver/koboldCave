package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.MessageIds
import org.river.exertion.ai.internalState.InternalFacetInstancesState
import org.river.exertion.ecs.component.action.core.IComponent

class FacetComponent(var entity : Telegraph) : IComponent, Component, Telegraph {

    init {
        MessageManager.getInstance().addListener(this, MessageIds.INT_FACET.id())
    }

    override val componentName = "Facet"

    var internalState = InternalFacetInstancesState()

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.receiver == entity) ) {
            if (msg.message == MessageIds.INT_FACET.id()) {

            }
        }
        return true
    }

    companion object {
        val mapper = mapperFor<FacetComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is FacetComponent } != null
        fun getFor(entity : Entity) : FacetComponent? = if (has(entity)) entity.components.first { it is FacetComponent } as FacetComponent else null

    }

}