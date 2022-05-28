package org.river.exertion.ecs.system

import com.badlogic.ashley.core.*
import org.river.exertion.ecs.system.action.*

object SystemManager {

    fun init(pooledEngine: PooledEngine) {
        pooledEngine.addSystem(ActionFulfillMoveSystem())
        pooledEngine.addSystem(ActionMoveSystem())
        pooledEngine.addSystem(ActionSimpleDecideMoveSystem())
        pooledEngine.addSystem(ActionSimpleMoveSystem())

        pooledEngine.addSystem(DestantiateActionSystem())
        pooledEngine.addSystem(IdleActionSystem())
        pooledEngine.addSystem(InstantiateActionSystem())
        pooledEngine.addSystem(LookActionSystem())
        pooledEngine.addSystem(ReflectActionSystem())
        pooledEngine.addSystem(ScreechActionSystem())
        pooledEngine.addSystem(WatchActionSystem())

        pooledEngine.addSystem(ActionSystem())
        pooledEngine.addSystem(ConditionSystem())
        pooledEngine.addSystem(FacetSystem())
        pooledEngine.addSystem(SymbologySystem())
        pooledEngine.addSystem(ManifestSystem())
        pooledEngine.addSystem(MemorySystem())
        pooledEngine.addSystem(MomentSystem())
    }
}
