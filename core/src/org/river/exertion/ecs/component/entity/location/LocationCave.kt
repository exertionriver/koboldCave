package org.river.exertion.ecs.component.entity.location

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.ashley.with
import org.river.exertion.ecs.component.action.ActionInstantiateComponent
import org.river.exertion.ecs.component.action.MomentComponent
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.geom.node.nodeRoomMesh.NodeRoomMesh
import java.util.*

class LocationCave : ILocation, Component {

    override var entityName = "Cave"
    override var description = "Cave"

    override fun initialize(initName: String, entity: Entity) {
        entityName = initName
        actions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }

        Gdx.app.log (this.javaClass.name, "$initName initialized!")
    }

    override var moment = 30f

    override var actions = mutableListOf<IActionComponent>(
            MomentComponent(moment)
    ).apply { this.addAll(LocationNone.actions) }

    override var nodeRoomMesh = LocationNone.nodeRoomMesh

    companion object {
        val mapper = mapperFor<LocationCave>()

        fun has(entity : Entity) : Boolean { return entity.components.firstOrNull{ it is LocationCave } != null }

        fun instantiate(engine: PooledEngine, stage : Stage, initName : String = "cave" + Random(), nodeRoomMesh: NodeRoomMesh) : Entity {
            val newCave = engine.entity {
                with<LocationCave>()
            }.apply { this[mapper]?.initialize(initName, this)
                this[mapper]!!.nodeRoomMesh = nodeRoomMesh
            }
            newCave[ActionInstantiateComponent.mapper]!!.stage = stage

            Gdx.app.log (this.javaClass.name, "$initName instantiated..!")

            return newCave
        }
    }
}