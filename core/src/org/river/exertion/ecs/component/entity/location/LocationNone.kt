package org.river.exertion.ecs.component.entity.location

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh

object LocationNone : ILocation {

    override var entityName = "None"
    override var description = "None"

    override fun initialize(initName: String, entity: Entity) {
        entityName = initName
        actions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }
    }

    override var actions = mutableListOf<IActionComponent>(
        ActionInstantiateComponent(), ActionDestantiateComponent()
    )

    override var moment = 50f

    override var nodeRoomMesh = NodeRoomMesh()
}