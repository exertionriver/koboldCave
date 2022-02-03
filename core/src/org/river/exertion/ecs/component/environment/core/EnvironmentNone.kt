package org.river.exertion.ecs.component.environment.core

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.ActionPlexComponent
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.time.Moment

object EnvironmentNone : IEnvironment {
    override var name = "None"
    override var description = "None"

    override fun initialize(initName: String, entity: Entity) {
        name = initName
        baseActions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }
        extendedActions.keys.forEach {
            if (!entity.components.contains(it as Component)) entity.add(it as Component)
        }

        entity.add(actionPlex)
    }

    override var actionPlexMaxSize = 1
    override var moment = Moment(5000f)

    override var actionPlex = ActionPlexComponent(actionPlexMaxSize, moment)

    override var baseActions = mutableListOf<IActionComponent>(
        ActionInstantiateComponent(base = true), ActionDestantiateComponent()
    )
    override var extendedActions = mutableMapOf<IActionComponent, Probability>()

    override var nodeRoomMesh = NodeRoomMesh()
}