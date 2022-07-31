package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.ai.internalFacet.InternalFacetAttribute
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState
import org.river.exertion.ecs.component.action.core.IComponent

class FacetComponent(val entity : Telegraph, var facetAttributes: Set<InternalFacetAttribute>) : IComponent, Component {

    override val componentName = "Facet"

    var internalFacetState = InternalFacetInstancesState(entity, internalAttributes = facetAttributes)

    companion object {
        val mapper = mapperFor<FacetComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is FacetComponent } != null
        fun getFor(entity : Entity) : FacetComponent? = if (has(entity)) entity.components.first { it is FacetComponent } as FacetComponent else null

    }

}