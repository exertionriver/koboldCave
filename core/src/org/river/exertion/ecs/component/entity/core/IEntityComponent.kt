package org.river.exertion.ecs.component.entity.core

import com.badlogic.ashley.core.Entity
import org.river.exertion.Angle
import org.river.exertion.Point
import org.river.exertion.ecs.component.action.core.ActionPlexComponent
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.koboldCave.node.Node
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.time.Moment

interface IEntityComponent {

    var name : String
    var description : String

    fun initialize(initName : String, entity: Entity)

    var actionPlexMaxSize : Int
    var actionPlex : ActionPlexComponent

    var baseActions : MutableList<IActionComponent>
    var extendedActions : MutableMap<IActionComponent, Probability>

    var currentNodeRoom : NodeRoom
    var currentNode : Node
    var currentPosition : Point
    var currentAngle : Angle


    var moment : Moment
}