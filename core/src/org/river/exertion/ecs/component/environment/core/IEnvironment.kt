package org.river.exertion.ecs.component.environment.core

import com.badlogic.ashley.core.Entity
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh

interface IEnvironment {

    var environmentName : String
    var description : String

    fun initialize(initName : String, entity: Entity)

    var actions : MutableList<IActionComponent>

    //tenths of a second
    var moment : Float

    var nodeRoomMesh : NodeRoomMesh

    companion object {
        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is IEnvironment } != null
        fun getFor(entity : Entity) : IEnvironment? = if ( has(entity) ) entity.components.first { it is IEnvironment } as IEnvironment else null
    }

}