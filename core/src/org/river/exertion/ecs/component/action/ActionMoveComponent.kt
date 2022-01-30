package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.OrthographicCamera
import ktx.ashley.mapperFor
import org.river.exertion.Angle
import org.river.exertion.Point
import org.river.exertion.ecs.component.action.core.ActionNoneComponent
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.ActionType
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.NodeLink
import org.river.exertion.koboldCave.node.nodeMesh.NodeLine
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom

class ActionMoveComponent(base : Boolean = false)  : IActionComponent, Component {

    enum class Direction { NONE, FORWARD, BACKWARD, LEFT, RIGHT }

    override val label = "Move"
    override val description = { "Move" }
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
    var currentNodeLink = NodeLink(Node(), Node())
    var currentPosition = Point(0f, 0f)
    var currentAngle: Angle = 0f

    var forwardNextNodeAngle : Pair<Node, Angle> = Pair(Node(), 0f)
    var backwardNextNodeAngle : Pair<Node, Angle> = Pair(Node(), 0f)
    var leftNextAngle : Angle = 0f
    var rightNextAngle : Angle = 0f

    var leftTurnEasing = 0f //degrees
    var rightTurnEasing = 0f //degrees
    var forwardStepEasing = 0 //steps
    var backwardStepEasing = 0 //steps

    var stepPath = NodeLine()
    var finalNode = Node()
    var finalAngle : Angle = 0.0f
    var direction = Direction.FORWARD

    var camera : OrthographicCamera? = null

    companion object {
        val mapper = mapperFor<ActionMoveComponent>()
    }
}