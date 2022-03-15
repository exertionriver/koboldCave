package org.river.exertion.ecs.entity.location

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh

object LocationNone : ILocation {

    override var entityName = "None"
    override var description = "None"

    override val stateMachine = DefaultStateMachine(this, ActionState.NONE)

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