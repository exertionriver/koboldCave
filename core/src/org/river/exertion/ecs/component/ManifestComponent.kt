package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.MessageIds
import org.river.exertion.ai.manifest.InternalManifest
import org.river.exertion.ai.phenomena.ExternalPhenomenaInstance
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.btree.v0_1.IBTCharacter
import org.river.exertion.ecs.component.action.core.IComponent

class ManifestComponent(var entity : Telegraph) : IComponent, Component, Telegraph {

    override val componentName = "Manifest"

    var internalManifest = InternalManifest()

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender != entity) ) {
            if (msg.message == MessageIds.EXT_PHENOMENA.id()) {
                internalManifest.addImpression(msg.sender as IBTCharacter, (msg.extraInfo as ExternalPhenomenaInstance).impression())
            }
            if (msg.message == MessageIds.INT_PHENOMENA.id()) {
                internalManifest.addImpression((msg.extraInfo as InternalPhenomenaInstance).impression())
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