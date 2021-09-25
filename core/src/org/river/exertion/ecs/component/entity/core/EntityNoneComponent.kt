package org.river.exertion.ecs.component.entity.core

import org.river.exertion.Angle
import org.river.exertion.Point
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.koboldQueue.time.Moment

object EntityNoneComponent : IEntityComponent {

    override var name = "None"
    override var description = "None"

    override fun instantiate(initName: String) {
        name = initName
        baseActions.forEach { actionPlex.add(it) }
    }

    override var actionPlexMaxSize = 5

    override var actionPlex = mutableListOf<IActionComponent>()

    override var baseActions = mutableListOf<IActionComponent>(
        ActionLookComponent(), ActionReflectComponent()
    )
    override var extendedActions = mutableMapOf<IActionComponent, Int>(
        ActionIdleComponent() to 50,
        ActionLookComponent() to 25,
        ActionWatchComponent() to 25
    )

    override var currentPosition = Point(0f, 0f)
    override var currentAngle: Angle = 0f
    override var moment = Moment(100)
}