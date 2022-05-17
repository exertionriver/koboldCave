package org.river.exertion.ecs.system

import com.badlogic.ashley.core.*
import org.river.exertion.ecs.system.action.*

object SystemManager {

    fun init(pooledEngine: PooledEngine) {
        pooledEngine.addSystem(ActionDestantiateSystem())
        pooledEngine.addSystem(ActionIdleSystem())
        pooledEngine.addSystem(ActionInstantiateSystem())
        pooledEngine.addSystem(ActionLookSystem())
        pooledEngine.addSystem(ActionSimpleMoveSystem())
        pooledEngine.addSystem(ActionSimpleDecideMoveSystem())
        pooledEngine.addSystem(ActionMoveSystem())
        pooledEngine.addSystem(ActionFulfillMoveSystem())
        pooledEngine.addSystem(ActionReflectSystem())
        pooledEngine.addSystem(ActionScreechSystem())
        pooledEngine.addSystem(ActionWatchSystem())
        pooledEngine.addSystem(MomentSystem())
    }
}
