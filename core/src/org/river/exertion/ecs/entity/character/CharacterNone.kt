package org.river.exertion.ecs.entity.character

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import org.river.exertion.ai.noumena.NoneNoumenon
import org.river.exertion.ai.noumena.NoneNoumenon.none
import org.river.exertion.ai.noumena.core.NoumenonInstance
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.IComponent

object CharacterNone : ICharacter {

    override var entityName = "None"
    override var noumenonInstance = none {}

    override val stateMachine = DefaultStateMachine(this, ActionState.NONE)

    override fun initialize(initName: String, entity: Entity) {
        actions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }
    }

    override var moment = 1f

    override var actions = mutableListOf<IComponent>(
        LookActionComponent()
        , ReflectActionComponent()
        , IdleActionComponent()
        , WatchActionComponent()
    )

}