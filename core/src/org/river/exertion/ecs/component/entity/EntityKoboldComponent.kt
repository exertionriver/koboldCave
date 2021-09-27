package org.river.exertion.ecs.component.entity

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.EngineEntity
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.ActionPlexComponent
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.entity.core.EntityNoneComponent
import org.river.exertion.ecs.component.entity.core.IEntityComponent
import org.river.exertion.ecs.system.action.core.ActionPlex
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import org.river.exertion.koboldQueue.time.Moment

class EntityKoboldComponent : IEntityComponent, Component {

    override lateinit var name : String
    override var description = getDesc()

    override fun instantiate(initName : String, entity: Entity) {
        name = initName
        extendedActions.keys.forEach {
            if (!entity.components.contains(it as Component)) entity.add(it as Component)
        }
        baseActions.forEach {
            if (!entity.components.contains(it as Component) ) entity.add(it as Component)
        }

        entity.add(actionPlex)
    }

    fun getDesc(): String = ProbabilitySelect(mapOf(
        "ugly Kobold!" to Probability(40f, 0)
        ,"toothy Kobold!" to Probability(30f, 0)
        ,"scaly Kobold!" to Probability(30f, 0)
    )).getSelectedProbability()!!

    override var actionPlexMaxSize = EntityNoneComponent.actionPlexMaxSize
    override var moment = Moment(800)

    override var actionPlex = ActionPlexComponent(actionPlexMaxSize, moment)

    override var baseActions = mutableListOf<IActionComponent>(
        ActionLookComponent(base = true), ActionReflectComponent(base = true)
    )
    override var extendedActions = mutableMapOf<IActionComponent, Probability>(
        ActionIdleComponent() to Probability(50f, 0),
        ActionLookComponent() to Probability(25f, 0),
        ActionWatchComponent() to Probability(15f, 0),
        ActionScreechComponent() to Probability(5f, 0),
        ActionReflectComponent() to Probability(5f, 0)
    )

    override var currentPosition = EntityNoneComponent.currentPosition
    override var currentAngle = EntityNoneComponent.currentAngle


    companion object {
        val mapper = mapperFor<EntityKoboldComponent>()
    }
}