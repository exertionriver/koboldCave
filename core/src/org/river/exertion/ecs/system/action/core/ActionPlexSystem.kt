package org.river.exertion.ecs.system.action.core

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.systems.IteratingSystem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.selects.select
import ktx.ashley.allOf
import ktx.ashley.contains
import ktx.ashley.get
import org.river.exertion.ecs.component.action.ActionDestantiateComponent
import org.river.exertion.ecs.component.action.ActionIdleComponent
import org.river.exertion.ecs.component.action.ActionLookComponent
import org.river.exertion.ecs.component.action.ActionReflectComponent
import org.river.exertion.ecs.component.action.core.*
import org.river.exertion.ecs.component.entity.EntityKoboldComponent
import org.river.exertion.ecs.component.entity.core.IEntityComponent
import org.river.exertion.ecs.component.environment.core.IEnvironmentComponent
import org.river.exertion.ecs.system.action.*
import org.river.exertion.getEntityComponent
import org.river.exertion.getEnvironmentComponent
import org.river.exertion.isEntity
import org.river.exertion.isEnvironment
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import kotlin.time.ExperimentalTime

class ActionPlexSystem(private val pooledEngine: PooledEngine, val initInterval : Float = 0.1f) : IntervalSystem(initInterval) {

    init {
        pooledEngine.addSystem(this)
        pooledEngine.addSystem(ActionDestantiateSystem())
        pooledEngine.addSystem(ActionIdleSystem())
        pooledEngine.addSystem(ActionInstantiateSystem())
        pooledEngine.addSystem(ActionLookSystem())
        pooledEngine.addSystem(ActionMoveSystem())
        pooledEngine.addSystem(ActionReflectSystem())
        pooledEngine.addSystem(ActionScreechSystem())
        pooledEngine.addSystem(ActionWatchSystem())
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        updateInterval()
    }

    override fun updateInterval() {

        engine.entities.filter { it.contains(ActionPlexComponent.mapper) }.forEach { entity ->

//            println(entity[ActionPlexComponent.mapper]!!.countdown)

            if (entity[ActionPlexComponent.mapper]!!.countdown <= 0) {
                entity[ActionPlexComponent.mapper]!!.countdown = entity[ActionPlexComponent.mapper]!!.moment.milliseconds
                cycleStates(entity)
                queueAction(entity)
            }

            entity[ActionPlexComponent.mapper]!!.countdown -= 100
        }
    }

    private fun cycleStates(entity : Entity) {

 /*       if ( entity.isEntity() )
            println ("cycling states for ${entity.getEntityComponent().name}; ${entity[ActionPlexComponent.mapper]!!.slotsAvailable()} slots available")
        if ( entity.isEnvironment() )
            println ("cycling states for ${entity.getEnvironmentComponent().name}; ${entity[ActionPlexComponent.mapper]!!.slotsAvailable()} slots available")
*/
        entity.components.filter {it is IActionComponent}.sortedBy { (it as IActionComponent).priority }.forEach { (it as IActionComponent)

//            println ("${it.label} - ${it.state}: countdown: ${it.stateCountdown}; slotsFilled: ${it.plexSlotsFilled}")

            if (it.stateCountdown <= 0) {
                when ( it.state ) {
                    ActionState.ActionQueue -> {
                        //only prepare if a slot is available
                        if (it.plexSlotsRequired <= entity[ActionPlexComponent.mapper]!!.slotsAvailable()) {
                            entity[ActionPlexComponent.mapper]!!.slotsInUse += it.plexSlotsRequired
                            it.plexSlotsFilled += it.plexSlotsRequired

                            it.state = ActionState.ActionPrepare
                            it.stateCountdown = it.momentsToPrepare
 //                           println ("setting to prepare!")
                        } else it.stateCountdown = 1
                    }
                    ActionState.ActionPrepare -> {
                        it.state = ActionState.ActionExecute
                        it.stateCountdown = it.momentsToExecute
                        it.executed = false
//                        println("setting to execute!")
                    }
                    ActionState.ActionExecute -> {
                        it.state = ActionState.ActionRecover
                        it.stateCountdown = it.momentsToRecover
                        it.executed = false
//                       println("setting to recover!")
                    }
                    ActionState.ActionRecover -> {
                        if (it.type == ActionType.OneTimeExec) {
                            it.state = ActionState.ActionStateNone
//                            println("completed action!")
                        } else {
                            it.state = ActionState.ActionQueue
//                            println("requeued action!")
                        }

                        entity[ActionPlexComponent.mapper]!!.slotsInUse -= it.plexSlotsRequired
                        it.plexSlotsFilled -= it.plexSlotsRequired

                        it.stateCountdown = 1

                    }
                    else -> { //stopped
                        it.stateCountdown = 1
                    }
                }
            }

            it.stateCountdown--
        }
    }

    private fun queueAction(entity : Entity) {

        val selectedActionComponent = when {
            (entity.isEntity()) -> if (entity.getEntityComponent().extendedActions.isEmpty()) ActionNoneComponent
                else ProbabilitySelect(entity.getEntityComponent().extendedActions).getSelectedProbability()!!
            (entity.isEnvironment()) -> if (entity.getEnvironmentComponent().extendedActions.isEmpty()) ActionNoneComponent
                else ProbabilitySelect(entity.getEnvironmentComponent().extendedActions).getSelectedProbability()!!
            else -> ActionNoneComponent
        }

        val name = when {
            (entity.isEntity()) -> entity.getEntityComponent().name
            (entity.isEnvironment()) -> entity.getEnvironmentComponent().name
            else -> ""
        }

        if (selectedActionComponent != ActionNoneComponent) {
//            println("selected Action state: ${selectedActionComponent.state}")

            if ( selectedActionComponent.state == ActionState.ActionStateNone ) {
                selectedActionComponent.state = ActionState.ActionQueue
//                println ("queueing ${selectedActionComponent.label} for $name")
            } else {
//                println ("${selectedActionComponent.label} already in process, no action queued!")
            }
        }
    }

    companion object {
        fun <T: Component> readyToExecute(entity: Entity, mapper: ComponentMapper<T>) =
            entity.contains(ActionPlexComponent.mapper) &&
                    entity[ActionPlexComponent.mapper]!!.countdown == 0L &&
                    entity.contains(mapper) &&
                    (entity[mapper]!! is IActionComponent) &&
                    (entity[mapper]!! as IActionComponent).state == ActionState.ActionExecute &&
                    (entity[mapper]!! as IActionComponent).stateCountdown == 0 &&
                    !(entity[mapper]!! as IActionComponent).executed

    }
}
