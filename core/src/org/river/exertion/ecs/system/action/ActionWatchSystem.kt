package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.ecs.component.action.*

class ActionWatchSystem : IteratingSystem(allOf(ActionWatchComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if ( MomentComponent.has(entity) && entity[MomentComponent.mapper]!!.ready()) {
//            entity[MomentComponent.mapper]!!.reset(this.javaClass.name)
      //      println ("entity ${entity.getEntityComponent().name} watches..")

        }
    }
}
