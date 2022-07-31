package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ktx.ashley.allOf
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.ecs.component.ManifestComponent
import org.river.exertion.ecs.component.MomentComponent
import org.river.exertion.ecs.entity.IEntity

class ManifestSystem : IntervalIteratingSystem(allOf(ManifestComponent::class).get(), 1/10f) {

    override fun processEntity(entity: Entity) {

        val entityMomentDelta = (MomentComponent.getFor(entity)!!.systemMoment * this.interval * this.interval )

        ManifestComponent.getFor(entity)!!.internalManifest.getPerceivedPhenomenaList().filter { it.perceivedExternalPhenomena?.externalPhenomenaImpression != null }.forEach {
            manifestPhenomenonEntry ->
                manifestPhenomenonEntry.perceivedExternalPhenomena?.externalPhenomenaImpression!!.countdown -= entityMomentDelta
                if (manifestPhenomenonEntry.perceivedExternalPhenomena?.externalPhenomenaImpression!!.countdown < 0)
                    MessageChannel.REMOVE_EXT_PHENOMENA.send(IEntity.getFor(entity)!!, manifestPhenomenonEntry.perceivedExternalPhenomena!!)
        }

        val processedProjections = mutableSetOf<InternalPhenomenaImpression>()

        ManifestComponent.getFor(entity)!!.internalManifest.getPerceivedPhenomenaList().filter { it.internalPhenomenaImpression != null }.forEach {
            manifestProjectionEntry ->
              //  if (!processedProjections.contains(manifestProjectionEntry.internalPhenomenaImpression!!)) {
                    manifestProjectionEntry.internalPhenomenaImpression!!.countdown -= entityMomentDelta
                    if (manifestProjectionEntry.internalPhenomenaImpression!!.countdown < 0)
                        MessageChannel.REMOVE_INT_PHENOMENA.send(IEntity.getFor(entity), manifestProjectionEntry.internalPhenomenaImpression!!)
//                    else
//                        processedProjections.add(manifestProjectionEntry.internalPhenomenaImpression!!)
                //}
        }

        val perceivedPhenomena = ManifestComponent.getFor(entity)!!.internalManifest.getPerceivedPhenomenaList().filter { it.perceivedExternalPhenomena?.externalPhenomenaImpression != null }

        if ( perceivedPhenomena.isNotEmpty() )
            MessageChannel.INT_MEMORY.send(IEntity.getFor(entity), perceivedPhenomena)

    }
}
