package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.OrthographicCamera
import ktx.ashley.get
import ktx.ashley.mapperFor
import org.river.exertion.Angle
import org.river.exertion.Point
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.entity.location.ILocation
import org.river.exertion.geom.node.Node
import org.river.exertion.geom.node.NodeLink
import org.river.exertion.geom.node.nodeMesh.NodeLine
import org.river.exertion.geom.node.nodeMesh.NodeRoom
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh

class ActionMoveComponent : IActionComponent, Component {

    enum class Direction { NONE, FORWARD, BACKWARD, LEFT, RIGHT }

    override val componentName = "Move"

    fun initialize(location : ILocation, camera: OrthographicCamera? = null) {

        this.nodeRoomMesh = location.nodeRoomMesh
        this.currentNodeRoom = this.nodeRoomMesh.nodeRooms.first()
        this.currentNode = this.currentNodeRoom.getRandomUnoccupiedNode()
        this.currentNode.attributes.occupied = true
        this.currentPosition = this.currentNode.position

        val randomNodeLinkAngle = this.currentNodeRoom.getRandomNextNodeLinkAngle(this.currentNode)

        this.currentNodeLink = randomNodeLinkAngle.first
        this.currentAngle = randomNodeLinkAngle.second
        this.direction = Direction.NONE

        this.camera = camera

    }


    //TODO: split this into location component?
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

    fun moveIncomplete() = netMove() > 0f

    fun moveComplete() = !moveIncomplete()

    var beganByMoving = Direction.NONE

    companion object {
        val mapper = mapperFor<ActionMoveComponent>()
    }
}