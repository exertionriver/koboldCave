package org.river.exertion.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.systems.IntervalSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.ashley.contains
import ktx.ashley.get
import org.river.exertion.ecs.component.ActionLookComponent
import org.river.exertion.ecs.component.KoboldComponent
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
class ActionLookSystem() : IntervalSystem(1f) {

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        updateInterval()
    }

    override fun updateInterval() {
        lookAtAllOtherKobolds()
    }

    fun lookAtAllOtherKobolds() {
        engine.entities.filter { it.contains(ActionLookComponent.mapper) }.forEach { lookingEntity ->
            var lookDigest = ""
            engine.entities.filter { it.contains(KoboldComponent.mapper) }.forEach { koboldEntity ->
                if (lookingEntity != koboldEntity) {
                    koboldEntity[KoboldComponent.mapper]?.let {
                        lookDigest += it.desc + ", "
                    }
                }
            }
            println ("entity ${lookingEntity[KoboldComponent.mapper]?.name} sees $lookDigest")
        }
    }
}
