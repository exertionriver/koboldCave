package org.river.exertion.ecs.component.entity

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.*
import org.river.exertion.ecs.component.action.core.IActionComponent
import org.river.exertion.ecs.component.entity.core.EntityNoneComponent
import org.river.exertion.ecs.component.entity.core.IEntityComponent
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import org.river.exertion.koboldQueue.time.Moment

class EntityKoboldComponent : IEntityComponent, Component {

    override lateinit var name : String
    override var description = getDesc()

    override fun instantiate(initName : String) {
        name = initName
        baseActions.forEach { actionPlex.add(it) }
    }

    fun getDesc(): String = ProbabilitySelect(mapOf(
        "ugly Kobold!" to Probability(40f, 0)
        ,"toothy Kobold!" to Probability(30f, 0)
        ,"scaly Kobold!" to Probability(30f, 0)
    )).getSelectedProbability()!!

    override var actionPlexMaxSize = EntityNoneComponent.actionPlexMaxSize

    override var actionPlex = mutableListOf<IActionComponent>()

    override var baseActions = mutableListOf<IActionComponent>(
        ActionLookComponent(), ActionReflectComponent()
    )
    override var extendedActions = mutableMapOf<IActionComponent, Int>(
        ActionIdleComponent() to 60
        , ActionLookComponent() to 15
        , ActionWatchComponent() to 15
        , ActionScreechComponent() to 10
    )

    override var currentPosition = EntityNoneComponent.currentPosition
    override var currentAngle = EntityNoneComponent.currentAngle

    override var moment = Moment(800)

    companion object {
        val mapper = mapperFor<EntityKoboldComponent>()
    }
}