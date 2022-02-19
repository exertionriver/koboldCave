package org.river.exertion.ecs.component.environment.core

import com.badlogic.ashley.core.Entity
import org.river.exertion.ecs.component.action.core.ActionPlexComponent
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.time.Moment

interface IEnvironment {

    var name : String
    var description : String

    fun initialize(initName : String, entity: Entity)

    var actionPlexMaxSize : Int
    var actionPlex : ActionPlexComponent

    var baseActions : MutableList<IActionComponent>
    var extendedActions : MutableMap<IActionComponent, Probability>

    var moment : Moment

    var nodeRoomMesh : NodeRoomMesh
}