package ai

import org.river.exertion.ai.internalFacet.AngerFacet.angerFacet
import org.river.exertion.ai.internalFacet.FearFacet.fearFacet
import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.memory.KnowledgeSourceType
import org.river.exertion.ai.noumena.other.being.humanoid.low_race.KoboldNoumenon.kobold
import org.river.exertion.ai.noumena.core.NoumenonType
import org.river.exertion.ai.perception.PerceivedAttribute
import org.river.exertion.ai.perception.PerceivedNoumenon

object KoboldMemory {

    fun memoriesPA() : MutableList<PerceivedAttribute> {

        val returnList = mutableListOf<PerceivedAttribute>()
        val kobold = kobold { }

        returnList.add(PerceivedAttribute(kobold.pollRandomAttributeInstance()))
        returnList.add(PerceivedAttribute(kobold.pollRandomAttributeInstance()))
        returnList.add(PerceivedAttribute(kobold.pollRandomAttributeInstance()))

        return returnList
    }

    fun memoriesPN() : MutableList<PerceivedNoumenon> {

        val returnList = mutableListOf<PerceivedNoumenon>()
        val kobold = kobold { }

        returnList.add(PerceivedNoumenon().apply {
            this.perceivedAttributes.add(PerceivedAttribute(kobold.pollRandomAttributeInstance()))
            this.internalStateInstance.internalState.add(fearFacet { magnitude = 0.3f })
            this.knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE)
            this.noumenonType = NoumenonType.KOBOLD
            this.isNamed = true
        })

        returnList.add(PerceivedNoumenon().apply {
            this.perceivedAttributes.add(PerceivedAttribute(kobold.pollRandomAttributeInstance()))
            this.internalStateInstance.internalState.add(angerFacet { magnitude = 0.5f })
            this.knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE)
            this.noumenonType = NoumenonType.KOBOLD
            this.isNamed = true
        })

        return returnList
    }
}