package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.MessageIds
import org.river.exertion.ai.internalFacet.InternalFacetAttribute
import org.river.exertion.ai.internalFacet.InternalFacetAttributesState
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState
import org.river.exertion.ecs.component.action.core.IComponent

class FacetComponent(var entity : Telegraph, var facetAttributes: Set<InternalFacetAttribute>) : IComponent, Component, Telegraph {

    override val componentName = "Facet"

    var responsiveInternalState = InternalFacetInstancesState()
    var arisingInternalState = InternalFacetAttributesState()

    var mInternalAnxiety = 0f

    init {
        arisingInternalState.internalFacetAttributes = facetAttributes
        MessageManager.getInstance().addListener(this, MessageIds.INT_FACET.id())
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageIds.INT_FACET.id()) {
                mInternalAnxiety = msg.extraInfo as Float
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