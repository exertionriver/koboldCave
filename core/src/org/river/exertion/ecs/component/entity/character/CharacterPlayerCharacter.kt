package org.river.exertion.ecs.component.entity.character

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.fsm.StateMachine
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.ashley.with
import org.river.exertion.MessageIds
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.entity.IEntity
import org.river.exertion.ecs.component.entity.location.ILocation
import org.river.exertion.s2d.ActorPlayerCharacter

class CharacterPlayerCharacter : ICharacter, Component {

    override lateinit var entityName : String

    override var description = "PlayerCharacter"

    override val stateMachine = DefaultStateMachine(this, ActionState.NONE)

    override fun initialize(initName : String, entity: Entity) {
        entityName = initName
//        actions.add(MessageComponent(entityName))
        actions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }
        MessageManager.getInstance().addListener(this, MessageIds.S2D_ECS_BRIDGE.id())

        Gdx.app.log (this.javaClass.name, "$initName initialized!")
    }

    override var moment = 10f
    override var actions = mutableListOf<IActionComponent>(
            MomentComponent(moment),
            ActionMoveComponent()
    ).apply { this.addAll(CharacterNone.actions) }

    companion object {
        val mapper = mapperFor<CharacterPlayerCharacter>()

        fun has(entity : Entity) : Boolean { return entity.components.firstOrNull{ it is CharacterPlayerCharacter } != null }
        fun getFor(entity : Entity) : CharacterPlayerCharacter? = if (has(entity)) entity.components.first { it is CharacterPlayerCharacter } as CharacterPlayerCharacter else null

        fun instantiate(engine: PooledEngine, stage : Stage, initName : String = "PlayerCharacter", location : Entity, camera : OrthographicCamera?) : Entity {
            val newPC = engine.entity {
                with<CharacterPlayerCharacter>()
            }.apply { this[mapper]?.initialize(initName, this) }

            newPC[ActionMoveComponent.mapper]!!.initialize(ILocation.getFor(location)!!, camera)

            stage.addActor(ActorPlayerCharacter(initName, newPC[ActionMoveComponent.mapper]!!.currentPosition, newPC[ActionMoveComponent.mapper]!!.currentAngle ) )

            Gdx.app.log (this.javaClass.name, "$initName instantiated at ${newPC[ActionMoveComponent.mapper]!!.currentNode}, pointing ${newPC[ActionMoveComponent.mapper]!!.currentAngle}..!")

            return newPC
        }

    }
}
