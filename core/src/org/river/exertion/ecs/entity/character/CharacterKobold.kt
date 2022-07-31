package org.river.exertion.ecs.entity.character

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.ashley.with
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.noumena.core.NoumenonInstance
import org.river.exertion.ai.noumena.other.being.humanoid.low_race.KoboldNoumenon.kobold
import org.river.exertion.ecs.component.*
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.action.ActionSimpleDecideMoveComponent
import org.river.exertion.ecs.component.action.ScreechActionComponent
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.IComponent
import org.river.exertion.ecs.entity.location.ILocation
import org.river.exertion.logDebug
import org.river.exertion.s2d.actor.ActorKobold
import java.util.*

class CharacterKobold : ICharacter, Component {

    override lateinit var entityName : String
    override lateinit var noumenonInstance: NoumenonInstance

    override val stateMachine = DefaultStateMachine(this, ActionState.NONE)

/*    fun getDesc(): String = ProbabilitySelect(mapOf(
            "ugly Kobold!" to Probability(40f, 0)
            ,"toothy Kobold!" to Probability(30f, 0)
            ,"scaly Kobold!" to Probability(30f, 0)
    )).getSelectedProbability()!!
*/
    override fun initialize(initName : String, entity: Entity) {
        entityName = initName
        noumenonInstance = kobold { instanceName = initName }

//        actions.add(MessageComponent(entityName))
        actions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }
        entity.add(FacetComponent(this, noumenonInstance.facetAttributes!!))

        MessageChannel.S2D_ECS_BRIDGE.enableReceive(this)

        logDebug (this.javaClass.name, "$initName initialized!")
    }

    override var moment = 6f // six tenths of a second
    override var actions = mutableListOf<IComponent>(
        MomentComponent(moment),
        ConditionComponent(this),
        ManifestComponent(this),
        MemoryComponent(this),
        SymbologyComponent(this),
        ActionMoveComponent(),
        ActionSimpleDecideMoveComponent(),
        ScreechActionComponent(),

    ).apply { this.addAll(CharacterNone.actions) }

    companion object {
        val mapper = mapperFor<CharacterKobold>()

        fun has(entity : Entity) : Boolean { return entity.components.firstOrNull{ it is CharacterKobold } != null }
        fun getFor(entity : Entity) : CharacterKobold? = if (has(entity)) entity.components.first { it is CharacterKobold } as CharacterKobold else null

        fun ecsInstantiate(engine: PooledEngine, initName : String = "krazza" + Random().nextInt()) : Entity {
            return engine.entity {
                with<CharacterKobold>()
            }.apply { this[mapper]?.initialize(initName, this) }
        }

        fun instantiate(engine: PooledEngine, stage : Stage, location : Entity) : Entity {
            val newKobold = ecsInstantiate(engine)

            newKobold[ActionMoveComponent.mapper]!!.initialize(ILocation.getFor(location)!!)

            val entityName = newKobold[mapper]!!.entityName

            stage.addActor(ActorKobold(entityName, newKobold[ActionMoveComponent.mapper]!!.currentPosition, newKobold[ActionMoveComponent.mapper]!!.currentAngle ) )

            logDebug(this.javaClass.name, "entity $entityName instantiated at ${newKobold[ActionMoveComponent.mapper]!!.currentNode}, pointing ${newKobold[ActionMoveComponent.mapper]!!.currentAngle}..!")

            return newKobold
        }
    }
}