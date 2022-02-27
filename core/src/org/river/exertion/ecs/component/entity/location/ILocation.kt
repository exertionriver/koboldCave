package org.river.exertion.ecs.component.entity.location

import com.badlogic.ashley.core.Entity
import org.river.exertion.ecs.component.entity.IEntity
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh

interface ILocation : IEntity {

    var nodeRoomMesh : NodeRoomMesh

    companion object {
        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is ILocation } != null
        fun getFor(entity : Entity) : ILocation? = if ( has(entity) ) entity.components.first { it is ILocation } as ILocation else null
    }

}