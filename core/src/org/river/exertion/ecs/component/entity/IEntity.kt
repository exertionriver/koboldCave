package org.river.exertion.ecs.component.entity

import com.badlogic.ashley.core.Entity
import org.river.exertion.ecs.component.action.core.IActionComponent

interface IEntity {

    var entityName : String
    var description : String

    fun initialize(initName : String, entity: Entity)

    var actions : MutableList<IActionComponent>

    //tenths of a second
    var moment : Float

    companion object {
        fun has(entity : Entity) : Boolean { return entity.components.firstOrNull{ it is IEntity } != null }
        fun getFor(entity : Entity) : IEntity? = if ( has(entity) ) entity.components.first { it is IEntity } as IEntity else null
    }
}