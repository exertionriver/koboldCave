package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.*
import org.river.exertion.ecs.component.MomentComponent
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.ActionState
import org.river.exertion.ecs.entity.character.CharacterKobold
import org.river.exertion.ecs.entity.location.ILocation

class ActionInstantiateSystem : IteratingSystem(allOf(ActionInstantiateComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {

        if ( ILocation.has(entity) && MomentComponent.has(entity) && ILocation.getFor(entity)!!.stateMachine.currentState == ActionState.SOME ) {

            //max three entities spawning for now
            if ( engine.entities.filter { CharacterKobold.has(it) }.count() < 2) {
                CharacterKobold.instantiate(this.engine as PooledEngine, entity[ActionInstantiateComponent.mapper]!!.stage, location = entity)
                ILocation.getFor(entity)!!.stateMachine.update()
            }
        }
    }
}
