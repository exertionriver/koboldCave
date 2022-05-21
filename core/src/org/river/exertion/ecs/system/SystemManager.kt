package org.river.exertion.ecs.system

import com.badlogic.ashley.core.*
import org.river.exertion.ecs.system.action.*

object SystemManager {

    fun init(pooledEngine: PooledEngine) {
        pooledEngine.addSystem(DestantiateActionSystem())
        pooledEngine.addSystem(IdleActionSystem())
        pooledEngine.addSystem(InstantiateActionSystem())
        pooledEngine.addSystem(LookActionSystem())
        pooledEngine.addSystem(ActionSimpleMoveSystem())
        pooledEngine.addSystem(ActionSimpleDecideMoveSystem())
        pooledEngine.addSystem(ActionMoveSystem())
        pooledEngine.addSystem(ActionFulfillMoveSystem())
        pooledEngine.addSystem(ReflectActionSystem())
        pooledEngine.addSystem(ScreechActionSystem())
        pooledEngine.addSystem(WatchActionSystem())
        pooledEngine.addSystem(MomentSystem())
    }
}
