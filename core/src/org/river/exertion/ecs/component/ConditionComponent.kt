package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.mapperFor
import org.river.exertion.ecs.component.action.core.IComponent

class ConditionComponent() : IComponent, Component {

    override val componentName = "Condition"

    var mLife = 1f
    var mLifeRegen = 0.05f

    var mIntAnxiety = .2f
    var mAwake = .6f

    var mTiredness = .2f
    var mExhaustion = .2f
    var mHunger = .2f
    var mThirst = .2f

    //out of 20
    var aIntelligence : Float = 10f
    var aWisdom : Float = 10f

    // posture enum
//    var isLyingDown : Boolean
//    var isSitting : Boolean
//    var isStanding : Boolean

    companion object {
        val mapper = mapperFor<ConditionComponent>()

        fun has(entity : Entity) : Boolean = entity.components.firstOrNull{ it is ConditionComponent } != null
        fun getFor(entity : Entity) : ConditionComponent? = if (has(entity)) entity.components.first { it is ConditionComponent } as ConditionComponent else null

    }

}