package org.river.exertion.ecs.component.entity

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.ashley.with
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.entity.core.EntityNone
import org.river.exertion.ecs.component.entity.core.IEntity
import org.river.exertion.ecs.component.environment.core.IEnvironment
import org.river.exertion.s2d.ActorPlayerCharacter

class EntityPlayerCharacter : IEntity, Component {

    override lateinit var entityName : String

    override var description = "PlayerCharacter"

    override fun initialize(initName : String, entity: Entity) {
        entityName = initName
        actions.add(MessageComponent(entityName))
        actions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }
        Gdx.app.log (this.javaClass.name, "$initName initialized!")
    }

    override var moment = 10f
    override var actions = mutableListOf<IActionComponent>(
            MomentComponent(moment),
            ActionMoveComponent()
    ).apply { this.addAll(EntityNone.actions) }

    companion object {
        val mapper = mapperFor<EntityPlayerCharacter>()

        fun has(entity : Entity) : Boolean { return entity.components.firstOrNull{ it is EntityPlayerCharacter } != null }

        fun instantiate(engine: PooledEngine, stage : Stage, initName : String = "PlayerCharacter", cave : Entity, camera : OrthographicCamera?) : Entity {
            val newPC = engine.entity {
                with<EntityPlayerCharacter>()
            }.apply { this[mapper]?.initialize(initName, this) }

            //TODO: put following code in initialize()?
            newPC[ActionMoveComponent.mapper]!!.nodeRoomMesh = IEnvironment.getFor(cave)!!.nodeRoomMesh
            newPC[ActionMoveComponent.mapper]!!.currentNodeRoom = newPC[ActionMoveComponent.mapper]!!.nodeRoomMesh.nodeRooms.first()
            newPC[ActionMoveComponent.mapper]!!.currentNode = newPC[ActionMoveComponent.mapper]!!.currentNodeRoom.getRandomUnoccupiedNode()
            newPC[ActionMoveComponent.mapper]!!.currentNode.attributes.occupied = true
            newPC[ActionMoveComponent.mapper]!!.currentPosition = newPC[ActionMoveComponent.mapper]!!.currentNode.position

            val randomNodeLinkAngle = newPC[ActionMoveComponent.mapper]!!.currentNodeRoom.getRandomNextNodeLinkAngle(newPC[ActionMoveComponent.mapper]!!.currentNode)
            newPC[ActionMoveComponent.mapper]!!.currentNodeLink = randomNodeLinkAngle.first
            newPC[ActionMoveComponent.mapper]!!.currentAngle = randomNodeLinkAngle.second
            newPC[ActionMoveComponent.mapper]!!.direction = ActionMoveComponent.Direction.NONE

            newPC[ActionMoveComponent.mapper]!!.camera = camera

            stage.addActor(ActorPlayerCharacter(initName, newPC[ActionMoveComponent.mapper]!!.currentPosition, newPC[ActionMoveComponent.mapper]!!.currentAngle ) )

            Gdx.app.log (this.javaClass.name, "entity $initName instantiated at ${newPC[ActionMoveComponent.mapper]!!.currentNode}, pointing ${newPC[ActionMoveComponent.mapper]!!.currentAngle}..!")

            return newPC
        }

    }
}
