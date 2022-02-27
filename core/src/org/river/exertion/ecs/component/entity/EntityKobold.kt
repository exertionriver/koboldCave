package org.river.exertion.ecs.component.entity

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.ashley.*
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.entity.core.EntityNone
import org.river.exertion.ecs.component.entity.core.IEntity
import org.river.exertion.Probability
import org.river.exertion.ProbabilitySelect
import org.river.exertion.ecs.component.environment.core.IEnvironment
import org.river.exertion.s2d.ActorKobold
import java.util.*

class EntityKobold : IEntity, Component {

    override lateinit var entityName : String

    //TODO: move description into describable component
    override var description = getDesc()

    fun getDesc(): String = ProbabilitySelect(mapOf(
            "ugly Kobold!" to Probability(40f, 0)
            ,"toothy Kobold!" to Probability(30f, 0)
            ,"scaly Kobold!" to Probability(30f, 0)
    )).getSelectedProbability()!!

    override fun initialize(initName : String, entity: Entity) {
        entityName = initName

        actions.add(MessageComponent(entityName))
        actions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }
        Gdx.app.log (this.javaClass.name, "$initName initialized!")
    }

    override var moment = 6f
    override var actions = mutableListOf<IActionComponent>(
        MomentComponent(moment),
        ActionMoveComponent(),
        ActionSimpleDecideMoveComponent(),
        ActionScreechComponent()
    ).apply { this.addAll(EntityNone.actions) }

    companion object {
        val mapper = mapperFor<EntityKobold>()

        fun has(entity : Entity) : Boolean { return entity.components.firstOrNull{ it is EntityKobold } != null }

        fun instantiate(engine: PooledEngine, stage : Stage, initName : String = "krazza" + Random().nextInt(), cave : Entity) : Entity {
            val newKobold = engine.entity {
                with<EntityKobold>()
            }.apply { this[mapper]?.initialize(initName, this) }

            newKobold[ActionMoveComponent.mapper]!!.nodeRoomMesh = IEnvironment.getFor(cave)!!.nodeRoomMesh
            newKobold[ActionMoveComponent.mapper]!!.currentNodeRoom = newKobold[ActionMoveComponent.mapper]!!.nodeRoomMesh.nodeRooms.first()
            //TODO: getRandomNodeExcluding(listofPopulatedNodes) to avoid instantiating on other entities
            newKobold[ActionMoveComponent.mapper]!!.currentNode = newKobold[ActionMoveComponent.mapper]!!.currentNodeRoom.getRandomUnoccupiedNode()
            newKobold[ActionMoveComponent.mapper]!!.currentNode.attributes.occupied = true
            newKobold[ActionMoveComponent.mapper]!!.currentPosition = newKobold[ActionMoveComponent.mapper]!!.currentNode.position

            val randomNodeLinkAngle = newKobold[ActionMoveComponent.mapper]!!.currentNodeRoom.getRandomNextNodeLinkAngle(newKobold[ActionMoveComponent.mapper]!!.currentNode)
            newKobold[ActionMoveComponent.mapper]!!.currentNodeLink = randomNodeLinkAngle.first
            newKobold[ActionMoveComponent.mapper]!!.currentAngle = randomNodeLinkAngle.second

            stage.addActor(ActorKobold(initName, newKobold[ActionMoveComponent.mapper]!!.currentPosition, newKobold[ActionMoveComponent.mapper]!!.currentAngle ) )

            Gdx.app.log (this.javaClass.name, "entity $initName instantiated at ${newKobold[ActionMoveComponent.mapper]!!.currentNode}, pointing ${newKobold[ActionMoveComponent.mapper]!!.currentAngle}..!")

            return newKobold
        }
    }
}