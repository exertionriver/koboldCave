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

class ActionSimpleMoveComponent : IActionComponent, Component {

    override val componentName = "SimpleMove"

    var currentNodeRoom = NodeRoom()
    var currentNode = Node()
    var currentPosition = Point(0f, 0f)
    var currentAngle: Angle = 0f

    companion object {
        val mapper = mapperFor<ActionSimpleMoveComponent>()
    }
}