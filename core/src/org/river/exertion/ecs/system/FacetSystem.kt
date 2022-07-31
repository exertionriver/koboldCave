package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ktx.ashley.allOf
import org.river.exertion.ai.messaging.FacetImpressionMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ecs.component.FacetComponent
import org.river.exertion.ecs.entity.IEntity

class FacetSystem : IntervalIteratingSystem(allOf(FacetComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {

        val projections = FacetComponent.getFor(entity)!!.internalFacetState.projections()

        MessageChannel.INT_PHENOMENA_FACETS.send(IEntity.getFor(entity), FacetImpressionMessage(projections))
    }
}
