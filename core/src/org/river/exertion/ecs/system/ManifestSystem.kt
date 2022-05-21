package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.ai.GdxAI
import ktx.ashley.allOf
import org.river.exertion.ecs.component.ManifestComponent
import org.river.exertion.ecs.component.MomentComponent
import org.river.exertion.ecs.entity.IEntity

class ManifestSystem : IntervalIteratingSystem(allOf(ManifestComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {
        val delta = 1/10f

        ManifestComponent.getFor(entity)!!.internalManifest.manifests.forEach {
            manifest -> manifest.perceptionList.forEach {
                manifestEntry -> if (manifestEntry != null) manifestEntry.externalPhenomenaImpression!!.countdown -= delta
            }
        }

        ManifestComponent.getFor(entity)!!.internalManifest.manifests.forEach {
            manifest -> manifest.projectionList.forEach {
                manifestEntry -> if (manifestEntry != null) manifestEntry.countdown -= delta
            }
        }
    }
}
