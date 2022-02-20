package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.ActionNoneComponent
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.ActionType
import org.river.exertion.ecs.component.action.core.IActionComponent

class ActionInstantiateComponent(base : Boolean = false)  : IActionComponent, Component {

    override val label = "Instantiate"
    override val description = { "Instantiate" }
    override var type = if (base) ActionType.Continual else ActionNoneComponent.type
    override var priority = ActionNoneComponent.priority
    override var state = if (base) ActionState.ActionQueue else ActionState.ActionStateNone

    override var plexSlotsFilled = ActionNoneComponent.plexSlotsFilled
    override var plexSlotsRequired = ActionNoneComponent.plexSlotsRequired
    override var maxParallel = ActionNoneComponent.maxParallel

    override val momentsToPrepare = 5
    override val momentsToExecute = 5
    override val momentsToRecover = 5

    //in moments
    override var stateCountdown = 0
    override var executed = false

    lateinit var stage : Stage

    companion object {
        val mapper = mapperFor<ActionInstantiateComponent>()
    }
}