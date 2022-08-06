package org.river.exertion.ai.perception

import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.memory.KnowledgeSourceType
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ecs.component.action.core.ActionType

data class PerceivedNoumenon(var perceivedAttributes : MutableSet<PerceivedAttribute> = mutableSetOf()) {

    var noumenonType : NoumenonType = NoumenonType.NONE
    var instanceName : String? = null
    var isNamed : Boolean = false

    fun facts(onTopic : String) : MutableList<String> {

        val returnFacts = mutableListOf<String>()

        perceivedAttributes.forEach { perceivedAttribute ->
            val attributePhenomenaType = perceivedAttribute.attributeInstance!!.attributeObj.howPerceived()

            var perceptionStatement = attributePhenomenaType.perceivedAction(perceivedAttribute.knowledgeSourceInstance.source)
            if (perceivedAttribute.knowledgeSourceInstance.trust < .5f) perceptionStatement += " maybe"

            perceptionStatement +=
                if ((noumenonType == NoumenonType.INDIVIDUAL) && (isNamed)) {
                    " $instanceName"
                } else {
                    if (perceivedAttribute.knowledgeSourceInstance.source == KnowledgeSourceType.LEARNING || perceivedAttribute.knowledgeSourceInstance.source == KnowledgeSourceType.LEARNING)
                        " ${onTopic}s "
                    else
                        " a $onTopic"
                }

            perceptionStatement += if (perceivedAttribute.knowledgeSourceInstance.source == KnowledgeSourceType.EXPERIENCE || perceivedAttribute.knowledgeSourceInstance.source == KnowledgeSourceType.EXPERIENCE)
                " with a ${perceivedAttribute.attributeInstance!!.characteristicValue.description}"
            else
                " can have a ${perceivedAttribute.attributeInstance!!.characteristicValue.description}"

            val actionTag = perceivedAttribute.perceivedExternalPhenomena?.externalPhenomenaImpression?.actionType?.tag()
            if (actionTag != null)
                perceptionStatement += " while $actionTag"

            returnFacts.add(perceptionStatement)
        }

        return returnFacts
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PerceivedNoumenon

        if (perceivedAttributes != other.perceivedAttributes) return false
        if (noumenonType != other.noumenonType) return false
        if (instanceName != other.instanceName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = perceivedAttributes.hashCode()
        result = 31 * result + noumenonType.hashCode()
        result = 31 * result + (instanceName?.hashCode() ?: 0)
        return result
    }
}

