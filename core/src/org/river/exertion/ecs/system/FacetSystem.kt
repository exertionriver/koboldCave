package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.ai.msg.MessageManager
import ktx.ashley.allOf
import org.river.exertion.MessageIds
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.ecs.component.FacetComponent
import org.river.exertion.ecs.component.ManifestComponent
import org.river.exertion.ecs.component.MomentComponent
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.ecs.entity.character.CharacterKobold

class FacetSystem : IntervalIteratingSystem(allOf(FacetComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        val delta = 1/10f

        val arisingImpressions = FacetComponent.getFor(entity)!!.arisingInternalState.projections(FacetComponent.getFor(entity)!!.mInternalAnxiety)

        MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity), MessageIds.INT_PHENOMENA_FACETS.id(), arisingImpressions)

    }
}
