package org.river.exertion.ecs.component.entity.core

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import org.river.exertion.Angle
import org.river.exertion.Point
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.ActionPlexComponent
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.time.Moment

object EntityNone : IEntity {

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

    override var actionPlexMaxSize = 5
    override var moment = Moment(100f)
    override var momentCountdown: Float
        get() = TODO("Not yet implemented")
        set(value) {}

    override var actionPlex = ActionPlexComponent(actionPlexMaxSize, moment)

    override var baseActions = mutableListOf<IActionComponent>(
        ActionLookComponent(), ActionReflectComponent()
    )
    override var extendedActions = mutableMapOf<IActionComponent, Probability>(
        ActionIdleComponent() to Probability(50f, 0),
        ActionLookComponent() to Probability(25f, 0),
        ActionWatchComponent() to Probability(15f, 0),
        ActionReflectComponent() to Probability(10f, 0)
    )

}