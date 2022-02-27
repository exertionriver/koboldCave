package org.river.exertion.ecs.component.action

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.IActionComponent

class ActionInstantiateComponent : IActionComponent, Component {

    override val componentName = "Instantiate"

    lateinit var stage : Stage

    companion object {
        val mapper = mapperFor<ActionInstantiateComponent>()
    }
}