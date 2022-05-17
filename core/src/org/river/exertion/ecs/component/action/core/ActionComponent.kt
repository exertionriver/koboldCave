package org.river.exertion.ecs.component.action.core

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.btree.v0_1.Behavior
import org.river.exertion.btree.v0_1.ExecLeafTask
import org.river.exertion.btree.v0_1.task_cond.AbideTask
import org.river.exertion.ecs.component.action.core.IComponent

class ActionComponent : IComponent, Component {

    override val componentName = "Action"

    var decideSequenceList = mutableListOf<ExecLeafTask>()
    var currentAction : ExecLeafTask = AbideTask()
    var actionList = mutableListOf<Pair<Behavior, Float>>()

    var actionTimer = 0f
    val actionMoment = .6f
    var momentsLongAgo = 10f

    companion object {
        val mapper = mapperFor<ActionComponent>()
    }
}