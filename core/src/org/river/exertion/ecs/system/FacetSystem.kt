package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.ai.msg.MessageManager
import ktx.ashley.allOf
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ecs.component.FacetComponent
import org.river.exertion.ecs.entity.IEntity

class FacetSystem : IntervalIteratingSystem(allOf(FacetComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        val delta = 1/10f

        val arisingImpressions = FacetComponent.getFor(entity)!!.arisingInternalState.projections()
        FacetComponent.getFor(entity)!!.arisingInternalState.baseline()

        MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity), MessageChannel.INT_PHENOMENA_FACETS.id(), arisingImpressions)

    }
}
