package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.Point
import org.river.exertion.ecs.component.action.core.ActionNoneComponent
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.IActionComponent

class ActionMoveComponent : IActionComponent, Component {

    override val label = "Move"
    override val description = { "Move" }
    override var type = ActionNoneComponent.type
    override var priority = ActionNoneComponent.priority
    override var state = ActionState.ActionStateNone

    override var plexSlotsFilled = ActionNoneComponent.plexSlotsFilled
    override var plexSlotsRequired = ActionNoneComponent.plexSlotsRequired
    override var maxParallel = ActionNoneComponent.maxParallel

    override val momentsToPrepare = ActionNoneComponent.momentsToPrepare
    override val momentsToExecute = ActionNoneComponent.momentsToExecute
    override val momentsToRecover = ActionNoneComponent.momentsToRecover

    val currentPosition = Point(0f,0f)

    companion object {
        val mapper = mapperFor<ActionMoveComponent>()
    }
}