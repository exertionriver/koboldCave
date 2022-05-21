package org.river.exertion.ecs.entity.location

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.ashley.with
import org.river.exertion.MessageIds
import org.river.exertion.ai.noumena.NoneNoumenon
import org.river.exertion.ai.noumena.NoneNoumenon.none
import org.river.exertion.ecs.component.action.InstantiateActionComponent
import org.river.exertion.ecs.component.MomentComponent
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.component.action.core.IComponent
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh.Companion.buildWallsAndPath
import org.river.exertion.s2d.actor.ActorCave
import java.util.*

class LocationCave : ILocation, Component {

    override var entityName = "Cave"
    override var noumenonInstance = none {}

    override val stateMachine = DefaultStateMachine(this, ActionState.NONE)

    override fun initialize(initName: String, entity: Entity) {
        entityName = initName
        actions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }

        MessageManager.getInstance().addListener(this, MessageIds.S2D_ECS_BRIDGE.id())
        MessageManager.getInstance().addListener(this, MessageIds.NODEROOMMESH_BRIDGE.id())

        Gdx.app.log (this.javaClass.name, "$initName initialized!")
    }

    override var moment = 30f

    override var actions = mutableListOf<IComponent>(
            MomentComponent(moment)
    ).apply { this.addAll(LocationNone.actions) }

    override var nodeRoomMesh = LocationNone.nodeRoomMesh

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( msg != null && msg.message == MessageIds.NODEROOMMESH_BRIDGE.id() ) {
            Gdx.app.log("message","entity $entityName received telegram:${msg.message}, ${(msg.sender as IEntity).entityName}, ${msg.extraInfo}")
            this.nodeRoomMesh = msg.extraInfo as NodeRoomMesh
        }
        return super.handleMessage(msg)
    }

    companion object {
        val mapper = mapperFor<LocationCave>()

        fun has(entity : Entity) : Boolean { return entity.components.firstOrNull{ it is LocationCave } != null }

        fun instantiate(engine: PooledEngine, stage : Stage, initName : String = "cave" + Random(), nodeRoomMesh: NodeRoomMesh) : Entity {
            val newCave = engine.entity {
                with<LocationCave>()
            }.apply { this[mapper]?.initialize(initName, this)
                this[mapper]!!.nodeRoomMesh = nodeRoomMesh
                this[mapper]!!.nodeRoomMesh.buildWallsAndPath()
                //this[mapper]!!.nodeRoomMesh.renderWallsAndPath()
            }
            newCave[InstantiateActionComponent.mapper]!!.stage = stage

            stage.addActor(ActorCave(initName, nodeRoomMesh))

            Gdx.app.log (this.javaClass.name, "$initName instantiated..!")

            return newCave
        }
    }
}