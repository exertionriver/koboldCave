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

class ActionSimpleDecideMoveComponent : IActionComponent, Component {

    override val componentName = "SimpleDecideMove"

    companion object {
        val mapper = mapperFor<ActionSimpleDecideMoveComponent>()
    }
}