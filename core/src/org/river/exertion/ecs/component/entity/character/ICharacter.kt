package org.river.exertion.ecs.component.entity.character

import com.badlogic.ashley.core.Entity
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.entity.IEntity

interface ICharacter : IEntity {

    companion object {
        fun has(entity : Entity) : Boolean { return entity.components.firstOrNull{ it is ICharacter } != null }
        fun getFor(entity : Entity) : ICharacter? = if ( has(entity) ) entity.components.first { it is ICharacter } as ICharacter else null
    }
}