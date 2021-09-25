package org.river.exertion.ecs.component.entity.core

import org.river.exertion.Angle
import org.river.exertion.Point
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.koboldQueue.time.Moment

interface IEntityComponent {

    var name : String
    var description : String

    fun instantiate(initName : String)

    var actionPlexMaxSize : Int
    var actionPlex : MutableList<IActionComponent>

    var baseActions : MutableList<IActionComponent>
    var extendedActions : MutableMap<IActionComponent, Int>

    var currentPosition : Point
    var currentAngle : Angle
    var moment : Moment

}