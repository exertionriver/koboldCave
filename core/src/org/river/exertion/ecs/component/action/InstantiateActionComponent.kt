package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.ActionType
import org.river.exertion.ecs.component.action.core.IAction
import org.river.exertion.ecs.component.action.core.IComponent

class InstantiateActionComponent : IComponent, IAction, Component {

    override val actionType = ActionType.INSTANTIATE
    override val componentName = actionType.tag()

    lateinit var stage : Stage

    companion object {
        val mapper = mapperFor<InstantiateActionComponent>()
    }

}