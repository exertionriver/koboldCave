package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.ActionType
import org.river.exertion.ecs.component.action.core.IAction
import org.river.exertion.ecs.component.action.core.IComponent

class WatchActionComponent : IComponent, IAction, Component {

    override val actionType = ActionType.WATCH
    override val componentName = actionType.tag()

    companion object {
        val mapper = mapperFor<WatchActionComponent>()
    }

}