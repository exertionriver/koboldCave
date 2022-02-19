package org.river.exertion.ecs.component.entity.core

import com.badlogic.ashley.core.Entity
import org.river.exertion.ecs.component.action.core.ActionPlexComponent
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.time.Moment

interface IEntity {

    var name : String
    var description : String

    fun initialize(initName : String, entity: Entity)

    var actionPlexMaxSize : Int
    var actionPlex : ActionPlexComponent

    var baseActions : MutableList<IActionComponent>
    var extendedActions : MutableMap<IActionComponent, Probability>

    var moment : Moment
    var momentCountdown : Float
}