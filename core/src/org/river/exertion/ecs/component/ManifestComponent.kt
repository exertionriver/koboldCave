package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.ai.manifest.InternalManifest
import org.river.exertion.ai.messaging.FacetImpressionMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.perception.PerceivedExternalPhenomena
import org.river.exertion.ai.perception.PerceivedPhenomena
import org.river.exertion.ai.phenomena.ExternalPhenomenaImpression
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.InternalPhenomenaImpression
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.ecs.component.action.core.IComponent
import org.river.exertion.ecs.entity.IEntity

class ManifestComponent(var entity : Telegraph) : IComponent, Component, Telegraph {

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.ADD_EXT_PHENOMENA.id())
        MessageManager.getInstance().addListener(this, MessageChannel.ADD_INT_PHENOMENA.id())
        MessageManager.getInstance().addListener(this, MessageChannel.REMOVE_EXT_PHENOMENA.id())
        MessageManager.getInstance().addListener(this, MessageChannel.REMOVE_INT_PHENOMENA.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_PHENOMENA_FACETS.id())
    }

    override val componentName = "Manifest"
    var internalManifest = InternalManifest()

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender != entity) ) {
            if (msg.message == MessageChannel.ADD_EXT_PHENOMENA.id()) {
                internalManifest.addImpression(msg.sender as IEntity, (msg.extraInfo as ExternalPhenomenaInstance).impression())
            }
            if (msg.message == MessageChannel.ADD_INT_PHENOMENA.id()) {
                internalManifest.addImpression((msg.extraInfo as InternalPhenomenaInstance).impression())
            }
        }
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_PHENOMENA_FACETS.id()) {
                internalManifest.addFacetImpressions( (msg.extraInfo as FacetImpressionMessage).internalFacetImpressions)
            }
            if (msg.message == MessageChannel.REMOVE_INT_PHENOMENA.id()) {
                internalManifest.removeImpression(msg.extraInfo as InternalPhenomenaImpression)
            }
            if (msg.message == MessageChannel.REMOVE_EXT_PHENOMENA.id()) {
                internalManifest.removeImpression(msg.extraInfo as PerceivedExternalPhenomena)
            }
        }
        return true
    }

    companion object {
        val mapper = mapperFor<ManifestComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is ManifestComponent } != null
        fun getFor(entity : Entity) : ManifestComponent? = if (has(entity)) entity.components.first { it is ManifestComponent } as ManifestComponent else null

    }

}