package org.river.exertion.ecs.component

import com.badlogic.ashley.core.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.ashley.mapperFor
import org.river.exertion.koboldQueue.action.actions.*
import org.river.exertion.koboldQueue.condition.Probability
import org.river.exertion.koboldQueue.condition.ProbabilitySelect
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
class KoboldComponent : Component {

    lateinit var name : String
    val desc = getDescription()

    fun instantiate(initName : String ) {
        name = initName
        baseActions.forEach { actionPlex.add(it) }
    }

    fun getDescription(): String = ProbabilitySelect(mapOf(
        "ugly Kobold!" to Probability(40f, 0)
        ,"toothy Kobold!" to Probability(30f, 0)
        ,"scaly Kobold!" to Probability(30f, 0)
    )).getSelectedProbability()!!

    val actionPlexSize = 5

    val actionPlex : MutableList<ActionComponent> = mutableListOf()

    val baseActions = listOf(
        ActionLookComponent(initBase = true)
        , ActionReflectComponent(initBase = true)
    )
    val extendedActions = mutableMapOf(
        ActionIdleComponent() to 60
        , ActionLookComponent() to 15
        , ActionWatchComponent() to 15
        , ActionScreechComponent() to 10
    )

    override fun toString(): String {
        return "$name: ${super.toString()}"
    }

    companion object {
        val mapper = mapperFor<KoboldComponent>()
    }
}