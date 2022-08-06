package ai

import org.river.exertion.ai.internalFacet.AngerFacet.angerFacet
import org.river.exertion.ai.internalFacet.FearFacet.fearFacet
import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.memory.KnowledgeSourceType
import org.river.exertion.ai.noumena.core.NoumenonInstance
import org.river.exertion.ai.noumena.other.being.humanoid.low_race.KoboldNoumenon.kobold
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.perception.PerceivedAttribute
import org.river.exertion.ai.perception.PerceivedNoumenon

object KoboldMemory {

    fun memoriesPA(ofKobold : NoumenonInstance = kobold {}) : MutableList<PerceivedAttribute> {

        val returnList = mutableListOf<PerceivedAttribute>()

        returnList.add(PerceivedAttribute(ofKobold.pollRandomAttributeInstance()))
        returnList.add(PerceivedAttribute(ofKobold.pollRandomAttributeInstance()))
        returnList.add(PerceivedAttribute(ofKobold.pollRandomAttributeInstance()))

        return returnList
    }

    fun memoriesPN(ofKobold : NoumenonInstance = kobold {}) : MutableList<PerceivedNoumenon> {

        val returnList = mutableListOf<PerceivedNoumenon>()

        returnList.add(PerceivedNoumenon().apply {
            this.perceivedAttributes.add(PerceivedAttribute(ofKobold.pollRandomAttributeInstance()
                , knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE)))
            this.noumenonType = NoumenonType.OTHER
            this.isNamed = true
        })

        returnList.add(PerceivedNoumenon().apply {
            this.perceivedAttributes.add(PerceivedAttribute(ofKobold.pollRandomAttributeInstance()
                , knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.LORE)))
            this.noumenonType = NoumenonType.KOBOLD
            this.isNamed = true
        })

        returnList.add(PerceivedNoumenon().apply {
            this.perceivedAttributes.add(PerceivedAttribute(ofKobold.pollRandomAttributeInstance()
                , knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE)))
            this.noumenonType = NoumenonType.INDIVIDUAL
            this.instanceName = ofKobold.instanceName
            this.isNamed = true
        })

        return returnList
    }
}