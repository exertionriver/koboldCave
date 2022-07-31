package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.ai.manifest.InternalManifest
import org.river.exertion.ecs.component.action.core.IComponent

class ManifestComponent(var entity : Telegraph) : IComponent, Component {

    override val componentName = "Manifest"
    var internalManifest = InternalManifest(entity)

    companion object {
        val mapper = mapperFor<ManifestComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is ManifestComponent } != null
        fun getFor(entity : Entity) : ManifestComponent? = if (has(entity)) entity.components.first { it is ManifestComponent } as ManifestComponent else null
    }
}