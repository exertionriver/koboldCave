package org.river.exertion.ecs.component.entity

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.ashley.*
import org.river.exertion.ECS_S2D_BRIDGE
import org.river.exertion.S2D_ECS_BRIDGE
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.ActionPlexComponent
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.entity.core.EntityNone
import org.river.exertion.ecs.component.entity.core.IEntity
import org.river.exertion.getEntityComponent
import org.river.exertion.getEnvironmentComponent
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import org.river.exertion.koboldQueue.time.Moment
import org.river.exertion.s2d.ActorKobold
import org.river.exertion.s2d.ActorPlayerCharacter
import java.util.*

class EntityKobold : IEntity, Component {

    override lateinit var name : String
    override var description = getDesc()

    override fun initialize(initName : String, entity: Entity) {
        name = initName
        extendedActions.keys.forEach {
            if (!entity.components.contains(it as Component)) entity.add(it as Component)
        }
        baseActions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }

        entity.add(actionPlex)

//        MessageManager.getInstance().addListener(this, S2D_ECS_BRIDGE)

        println ("$initName initialized!")
    }

    fun getDesc(): String = ProbabilitySelect(mapOf(
        "ugly Kobold!" to Probability(40f, 0)
        ,"toothy Kobold!" to Probability(30f, 0)
        ,"scaly Kobold!" to Probability(30f, 0)
    )).getSelectedProbability()!!

    override var actionPlexMaxSize = EntityNone.actionPlexMaxSize
    override var moment = Moment(600f)
    override var momentCountdown = moment.milliseconds

    override var actionPlex = ActionPlexComponent(actionPlexMaxSize, moment)

    override var baseActions = mutableListOf<IActionComponent>(
        ActionLookComponent(base = true), ActionReflectComponent(base = true), MessageComponent()
    )
    override var extendedActions = mutableMapOf<IActionComponent, Probability>(
        ActionMoveComponent() to Probability(30f, 0),
        ActionSimpleDecideMoveComponent() to Probability(30f, 0),
        ActionIdleComponent() to Probability(25f, 0),
        ActionLookComponent() to Probability(20f, 0),
        ActionWatchComponent() to Probability(15f, 0),
        ActionScreechComponent() to Probability(5f, 0),
        ActionReflectComponent() to Probability(5f, 0)
    )

    companion object {
        val mapper = mapperFor<EntityKobold>()

        fun instantiate(engine: PooledEngine, stage : Stage, initName : String = "krazza" + Random().nextInt(), cave : Entity) : Entity {
            val newKobold = engine.entity {
                with<EntityKobold>()
            }.apply { this[mapper]?.initialize(initName, this) }

            newKobold[ActionMoveComponent.mapper]!!.nodeRoomMesh = cave.getEnvironmentComponent().nodeRoomMesh
            newKobold[ActionMoveComponent.mapper]!!.currentNodeRoom = newKobold[ActionMoveComponent.mapper]!!.nodeRoomMesh.nodeRooms.first()
            //TODO: getRandomNodeExcluding(listofPopulatedNodes) to avoid instantiating on other entities
            newKobold[ActionMoveComponent.mapper]!!.currentNode = newKobold[ActionMoveComponent.mapper]!!.currentNodeRoom.getRandomNode()
            newKobold[ActionMoveComponent.mapper]!!.currentPosition = newKobold[ActionMoveComponent.mapper]!!.currentNode.position

            val randomNodeLinkAngle = newKobold[ActionMoveComponent.mapper]!!.currentNodeRoom.getRandomNextNodeLinkAngle(newKobold[ActionMoveComponent.mapper]!!.currentNode)
            newKobold[ActionMoveComponent.mapper]!!.currentNodeLink = randomNodeLinkAngle.first
            newKobold[ActionMoveComponent.mapper]!!.currentAngle = randomNodeLinkAngle.second
            newKobold[ActionMoveComponent.mapper]!!.moment = newKobold[mapper]!!.moment

            newKobold[MessageComponent.mapper]!!.name = initName

            stage.addActor(ActorKobold(initName, newKobold[ActionMoveComponent.mapper]!!.currentPosition, newKobold[ActionMoveComponent.mapper]!!.currentAngle ) )

            println ("entity ${newKobold.getEntityComponent().name} instantiated at ${newKobold[ActionMoveComponent.mapper]!!.currentNode}, pointing ${newKobold[ActionMoveComponent.mapper]!!.currentAngle}..!")

            return newKobold
        }
    }
    /*
    override fun handleMessage(msg: Telegram?): Boolean {
        if ( msg != null && msg.sender is ActorKobold) {
            Gdx.app.log("message","EntityKobold received telegram:${msg.message}, ${(msg.sender as ActorPlayerCharacter).name}, ${msg.extraInfo}")

            return true
        }

        return false
    }
     */
}