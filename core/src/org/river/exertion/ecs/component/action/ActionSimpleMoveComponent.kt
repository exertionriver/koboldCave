package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.Angle
import org.river.exertion.Point
import org.river.exertion.ecs.component.action.core.ActionNoneComponent
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.ActionType
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.nodeMesh.NodeRoom

class ActionSimpleMoveComponent(base : Boolean = false)  : IActionComponent, Component {

    override val label = "SimpleMove"
    override val description = { "SimpleMove" }
    override var type = if (base) ActionType.Continual else ActionNoneComponent.type
    override var priority = ActionNoneComponent.priority
    override var state = if (base) ActionState.ActionQueue else ActionState.ActionStateNone

    override var plexSlotsFilled = ActionNoneComponent.plexSlotsFilled
    override var plexSlotsRequired = ActionNoneComponent.plexSlotsRequired
    override var maxParallel = ActionNoneComponent.maxParallel

    override val momentsToPrepare = ActionNoneComponent.momentsToPrepare
    override val momentsToExecute = ActionNoneComponent.momentsToExecute
    override val momentsToRecover = ActionNoneComponent.momentsToRecover

    //in moments
    override var stateCountdown = 0
    override var executed = false

    var currentNodeRoom = NodeRoom()
    var currentNode = Node()
    var currentPosition = Point(0f, 0f)
    var currentAngle: Angle = 0f

    companion object {
        val mapper = mapperFor<ActionSimpleMoveComponent>()
    }
}