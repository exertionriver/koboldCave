package org.river.exertion.ecs.component.entity.character

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.entity.IEntity

object CharacterNone : ICharacter {

    override var entityName = "None"
    override var description = "None"

    override fun initialize(initName: String, entity: Entity) {
        entityName = initName
        actions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }
    }

    override var moment = 1f

    override var actions = mutableListOf<IActionComponent>(
        ActionLookComponent()
        , ActionReflectComponent()
        , ActionIdleComponent()
        , ActionWatchComponent()
    )

}