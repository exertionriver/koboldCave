package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.ai.msg.MessageManager
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
                    MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity), MessageChannel.REMOVE_EXT_PHENOMENA.id(), manifestPhenomenonEntry.perceivedExternalPhenomena)
        }

        val processedProjections = mutableSetOf<InternalPhenomenaImpression>()

        ManifestComponent.getFor(entity)!!.internalManifest.getPerceivedPhenomenaList().filter { it.internalPhenomenaImpression != null }.forEach {
            manifestProjectionEntry ->
              //  if (!processedProjections.contains(manifestProjectionEntry.internalPhenomenaImpression!!)) {
                    manifestProjectionEntry.internalPhenomenaImpression!!.countdown -= entityMomentDelta
                    if (manifestProjectionEntry.internalPhenomenaImpression!!.countdown < 0)
                        MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity), MessageChannel.REMOVE_INT_PHENOMENA.id(), manifestProjectionEntry.internalPhenomenaImpression)
//                    else
//                        processedProjections.add(manifestProjectionEntry.internalPhenomenaImpression!!)
                //}
        }

        val perceivedPhenomena = ManifestComponent.getFor(entity)!!.internalManifest.getPerceivedPhenomenaList().filter { it.perceivedExternalPhenomena?.externalPhenomenaImpression != null }

        if ( perceivedPhenomena.isNotEmpty() )
            MessageManager.getInstance().dispatchMessage(IEntity.getFor(entity), MessageChannel.INT_MEMORY.id(), perceivedPhenomena)

    }
}
