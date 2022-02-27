package org.river.exertion.ecs.system.action

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.msg.MessageManager
import ktx.ashley.allOf
import ktx.ashley.get
import org.river.exertion.MessageIds
import org.river.exertion.ecs.component.action.ActionLookComponent
import org.river.exertion.ecs.component.action.ActionMoveComponent
import org.river.exertion.ecs.component.action.MessageComponent
import org.river.exertion.ecs.system.action.core.ActionPlexSystem
import org.river.exertion.getEntityComponent
import org.river.exertion.isEntity

class ActionLookSystem : IteratingSystem(allOf(ActionLookComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        var lookDigest = ""

        if ( ActionPlexSystem.readyToExecute(entity, ActionLookComponent.mapper) && entity.isEntity() ) {

            engine.entities.filter { it.isEntity() }.forEach {

                //for now, look is external--entity cannot see themselves
                if (entity != it) {
                    lookDigest += it.getEntityComponent().description + ", "
                }
            }
      //      if (lookDigest.isNotEmpty()) println ("entity ${entity.getEntityComponent().name} sees $lookDigest")

        MessageManager.getInstance().dispatchMessage(entity[MessageComponent.mapper]!!, MessageIds.PERCEPTION_BRIDGE.id(), "move to ${entity[ActionMoveComponent.mapper]!!.toString()}")

            //     else println ("entity ${entity.getEntityComponent().name} sees nothing")

            entity[ActionLookComponent.mapper]!!.executed = true
        }
    }
}
