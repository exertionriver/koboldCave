package org.river.exertion.ecs.component.action.core

object NoneActionComponent : IAction, IComponent {

    override val actionType = ActionType.NONE
    override val componentName = actionType.tag()
}