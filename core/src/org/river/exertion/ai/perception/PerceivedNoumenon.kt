package org.river.exertion.ai.perception

import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.memory.KnowledgeSourceType
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ecs.component.action.core.ActionType

data class PerceivedNoumenon(var perceivedAttributes : MutableSet<PerceivedAttribute> = mutableSetOf(), var knowledgeSourceInstance: KnowledgeSourceInstance = KnowledgeSourceInstance()) {

    var noumenonType : NoumenonType = NoumenonType.NONE
    var instanceName : String? = null
    var isNamed : Boolean = false

    fun facts() : MutableList<String> {

        val returnFacts = mutableListOf<String>()

        perceivedAttributes.forEach { perceivedAttribute ->
            val attributePhenomenaType = perceivedAttribute.attributeInstance!!.attributeObj.howPerceived()

            var perceptionStatement = attributePhenomenaType.perceivedAction(knowledgeSourceInstance.source)
            if (knowledgeSourceInstance.trust < .5f) perceptionStatement += " maybe"

            perceptionStatement +=
                    if (knowledgeSourceInstance.source == KnowledgeSourceType.LEARNING || knowledgeSourceInstance.source == KnowledgeSourceType.LEARNING)
                        " ${perceivedAttribute.attributeInstance!!.noumenonObj.type().tag()}s "
                    else
                        " a ${perceivedAttribute.attributeInstance!!.noumenonObj.type().tag()}"

            perceptionStatement += " with a ${perceivedAttribute.attributeInstance!!.characteristicValue.description}"

            perceptionStatement += " while ${perceivedAttribute.perceivedExternalPhenomena?.externalPhenomenaImpression?.actionType?.tag() ?: ActionType.NONE.tag() }"

       //     perceptionStatement += " and it made me feel ${internalStateInstance.description()}"

            returnFacts.add(perceptionStatement)
        }

        return returnFacts
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PerceivedNoumenon

        if (perceivedAttributes != other.perceivedAttributes) return false
        if (knowledgeSourceInstance != other.knowledgeSourceInstance) return false
        if (noumenonType != other.noumenonType) return false
        if (instanceName != other.instanceName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = perceivedAttributes.hashCode()
        result = 31 * result + knowledgeSourceInstance.hashCode()
        result = 31 * result + noumenonType.hashCode()
        result = 31 * result + (instanceName?.hashCode() ?: 0)
        return result
    }
}

