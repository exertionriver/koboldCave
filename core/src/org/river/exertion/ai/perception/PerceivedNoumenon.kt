package org.river.exertion.ai.perception

import org.river.exertion.ai.internalState.InternalFacetInstancesState
import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.memory.KnowledgeSourceType
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ecs.component.action.core.ActionType

data class PerceivedNoumenon(var perceivedAttributes : MutableSet<PerceivedAttribute> = mutableSetOf()//, var internalStateInstance: InternalFacetInstancesState = InternalFacetInstancesState()
        , var knowledgeSourceInstance: KnowledgeSourceInstance = KnowledgeSourceInstance()) {

    var noumenonType : NoumenonType = NoumenonType.NONE
    var instanceName : String? = null
    var threat : Float = 0f
    var opportunity : Float = 0f
    var isNamed : Boolean = false

    fun facts() : MutableList<String> {

        val returnFacts = mutableListOf<String>()

        perceivedAttributes.forEach { perceivedAttribute ->
            val attributePhenomenaType = perceivedAttribute.attributeInstance!!.attribute().howPerceived()

            var perceptionStatement = attributePhenomenaType.perceivedAction(knowledgeSourceInstance.source)
            if (knowledgeSourceInstance.trust < .5f) perceptionStatement += " maybe"

            perceptionStatement +=
                    if (knowledgeSourceInstance.source == KnowledgeSourceType.LEARNING || knowledgeSourceInstance.source == KnowledgeSourceType.LEARNING)
                        " ${perceivedAttribute.attributeInstance!!.noumenon().type().tag()}s "
                    else
                        " a ${perceivedAttribute.attributeInstance!!.noumenon().type().tag()}"

            perceptionStatement += " with a ${perceivedAttribute.attributeInstance!!.characteristicValue.description}"

            perceptionStatement += " while ${perceivedAttribute.perceivedExternalPhenomena?.externalPhenomenaImpression?.actionType?.tag() ?: ActionType.NONE.tag() }"

       //     perceptionStatement += " and it made me feel ${internalStateInstance.description()}"

            returnFacts.add(perceptionStatement)
        }

        return returnFacts
    }
}

