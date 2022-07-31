package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.msg.Telegraph
import ktx.ashley.mapperFor
import org.river.exertion.ai.internalCondition.InternalCondition
import org.river.exertion.ecs.component.action.core.IComponent

class ConditionComponent(val entity : Telegraph) : IComponent, Component {

    override val componentName = "Condition"

    var internalCondition = InternalCondition(entity)

    companion object {
        val mapper = mapperFor<ConditionComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is ConditionComponent } != null
        fun getFor(entity : Entity) : ConditionComponent? = if (has(entity)) entity.components.first { it is ConditionComponent } as ConditionComponent else null
    }
}