package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.OrthographicCamera
import ktx.ashley.get
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
import org.river.exertion.koboldCave.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.koboldQueue.time.Moment

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

    var nodeRoomMesh = NodeRoomMesh()
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

    var prevNetMove = netMove()
//    var prevPosition = currentPosition
    var prevTurnMaxEasing = 0f
    var curTurnMaxEasing = 0f

    fun stepPathCurrentNode() = if (forwardStepEasing > 0) stepPath.nodes.filter { it.uuid == stepPath.nodeOrder[stepPath.nodes.size - forwardStepEasing] }.first()
        else if (backwardStepEasing > 0) stepPath.nodes.filter { it.uuid == stepPath.nodeOrder[stepPath.nodes.size - backwardStepEasing] }.first()
        else finalNode

    fun stepPathFifthNextNode() = if (forwardStepEasing > 5) stepPath.nodes.filter { it.uuid == stepPath.nodeOrder[stepPath.nodes.size - forwardStepEasing + 5] }.first()
        else if (backwardStepEasing > 5) stepPath.nodes.filter { it.uuid == stepPath.nodeOrder[stepPath.nodes.size - backwardStepEasing + 5] }.first()
        else finalNode

    var moment = Moment(0f)
    var momentCountdown = 0f

    var camera : OrthographicCamera? = null


    fun halt() {
        leftTurnEasing = 0f
        rightTurnEasing = 0f
        forwardStepEasing = 0
        backwardStepEasing = 0
        direction = Direction.NONE
    }

    fun netMove() : Float {
        return leftTurnEasing + rightTurnEasing + forwardStepEasing + backwardStepEasing
    }

    fun moveStale() : Boolean {
        val curNetMove = netMove()

        val isStale = ( (prevNetMove == curNetMove) || (prevTurnMaxEasing == curTurnMaxEasing) )

        prevNetMove = curNetMove

        return isStale
    }

    fun moveIncomplete() = netMove() > 0f

    fun moveComplete() = !moveIncomplete()

    var beganByMoving = Direction.NONE

    companion object {
        val mapper = mapperFor<ActionMoveComponent>()
    }
}