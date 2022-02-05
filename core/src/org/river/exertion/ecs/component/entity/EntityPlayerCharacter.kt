package org.river.exertion.ecs.component.entity

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.ashley.with
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.ActionPlexComponent
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.entity.core.EntityNone
import org.river.exertion.ecs.component.entity.core.IEntity
import org.river.exertion.getEntityComponent
import org.river.exertion.getEnvironmentComponent
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.time.Moment

class EntityPlayerCharacter : IEntity, Component {

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

        println ("$initName initialized!")
    }

    fun getDesc(): String = "PlayerCharacter"

    override var actionPlexMaxSize = EntityNone.actionPlexMaxSize
    override var moment = Moment(1000f)
    override var momentCountdown = moment.milliseconds

    override var actionPlex = ActionPlexComponent(actionPlexMaxSize, moment)

    override var baseActions = mutableListOf<IActionComponent>(
            ActionLookComponent(base = true), ActionReflectComponent(base = true)
    )
    override var extendedActions = mutableMapOf<IActionComponent, Probability>(
            ActionMoveComponent() to Probability(85f, 0),
            ActionIdleComponent() to Probability(15f, 0),
//            ActionLookComponent() to Probability(20f, 0),
//            ActionWatchComponent() to Probability(15f, 0),
//            ActionScreechComponent() to Probability(5f, 0),
//            ActionReflectComponent() to Probability(5f, 0)
    )

    companion object {
        val mapper = mapperFor<EntityPlayerCharacter>()

        fun instantiate(engine: PooledEngine, initName : String = "PlayerCharacter", cave : Entity, camera : OrthographicCamera?) : Entity {
            val newPC = engine.entity {
                with<EntityPlayerCharacter>()
            }.apply { this[mapper]?.initialize(initName, this) }

            newPC[ActionMoveComponent.mapper]!!.nodeRoomMesh = cave.getEnvironmentComponent().nodeRoomMesh
            newPC[ActionMoveComponent.mapper]!!.currentNodeRoom = newPC[ActionMoveComponent.mapper]!!.nodeRoomMesh.nodeRooms.first()
            newPC[ActionMoveComponent.mapper]!!.currentNode = newPC[ActionMoveComponent.mapper]!!.currentNodeRoom.getRandomNode()
            newPC[ActionMoveComponent.mapper]!!.currentPosition = newPC[ActionMoveComponent.mapper]!!.currentNode.position

            val randomNodeLinkAngle = newPC[ActionMoveComponent.mapper]!!.currentNodeRoom.getRandomNextNodeLinkAngle(newPC[ActionMoveComponent.mapper]!!.currentNode)
            newPC[ActionMoveComponent.mapper]!!.currentNodeLink = randomNodeLinkAngle.first
            newPC[ActionMoveComponent.mapper]!!.currentAngle = randomNodeLinkAngle.second
            newPC[ActionMoveComponent.mapper]!!.moment = newPC[mapper]!!.moment
            newPC[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.NONE

            newPC[ActionMoveComponent.mapper]!!.camera = camera

            println ("entity ${newPC.getEntityComponent().name} instantiated at ${newPC[ActionMoveComponent.mapper]!!.currentNode}, pointing ${newPC[ActionMoveComponent.mapper]!!.currentAngle}..!")

            return newPC
        }

    }
}