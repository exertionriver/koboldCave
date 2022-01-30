package org.river.exertion.ecs.component.environment

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.mapperFor
import ktx.ashley.with
import org.river.exertion.ecs.component.action.ActionDestantiateComponent
import org.river.exertion.ecs.component.action.ActionInstantiateComponent
import org.river.exertion.ecs.component.action.core.ActionPlexComponent
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.environment.core.EnvironmentNone
import org.river.exertion.ecs.component.environment.core.IEnvironment
import org.river.exertion.koboldCave.node.nodeMesh.NodeRoom
import org.river.exertion.koboldQueue.time.Moment
import java.util.*

class EnvironmentCave : IEnvironment, Component {

    override var name = "Cave"
    override var description = "Cave"

    override fun initialize(initName: String, entity: Entity) {
        name = initName
        baseActions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }
        extendedActions.keys.forEach {
            if (!entity.components.contains(it as Component)) entity.add(it as Component)
        }

        entity.add(actionPlex)

        println ("$initName initialized!")
    }

    override var actionPlexMaxSize = 1
    override var moment = Moment(1000)

    override var actionPlex = ActionPlexComponent(actionPlexMaxSize, moment)

    override var baseActions = mutableListOf<IActionComponent>(
        ActionInstantiateComponent(base = true), ActionDestantiateComponent()
    )
    override var extendedActions = EnvironmentNone.extendedActions

    override var nodeRoom = EnvironmentNone.nodeRoom

    companion object {
        val mapper = mapperFor<EnvironmentCave>()

        fun instantiate(engine: PooledEngine, initName : String = "cave" + Random(), nodeRoom: NodeRoom? = null) : Entity {
            val newCave = engine.entity {
                with<EnvironmentCave>()
            }.apply { this[mapper]?.initialize(initName, this)
                if (nodeRoom != null) this[mapper]!!.nodeRoom = nodeRoom
            }

            println ("$initName instantiated!")

            return newCave
        }
    }
}