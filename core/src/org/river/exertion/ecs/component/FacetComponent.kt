package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import org.river.exertion.ai.internalState.InternalFacetInstancesState
import org.river.exertion.ecs.component.action.core.IComponent

class FacetComponent() : IComponent, Component {

    override val componentName = "Facet"

    var internalState = InternalFacetInstancesState()

    companion object {
        val mapper = mapperFor<FacetComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is FacetComponent } != null
        fun getFor(entity : Entity) : FacetComponent? = if (has(entity)) entity.components.first { it is FacetComponent } as FacetComponent else null

    }

}