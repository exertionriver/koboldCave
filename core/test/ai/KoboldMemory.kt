package ai

import org.river.exertion.ai.internalFacet.AngerFacet.angerFacet
import org.river.exertion.ai.internalFacet.FearFacet.fearFacet
import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.memory.KnowledgeSourceType
import org.river.exertion.ai.noumena.KoboldNoumenon.kobold
import org.river.exertion.ai.noumena.NoumenonType
import org.river.exertion.ai.perception.PerceivedAttribute
import org.river.exertion.ai.perception.PerceivedNoumenon

object KoboldMemory {

    fun memoriesPA() : MutableList<PerceivedAttribute> {

        val returnList = mutableListOf<PerceivedAttribute>()
        val kobold = kobold { }

        returnList.add(PerceivedAttribute(kobold.pollRandomAttribute()))
        returnList.add(PerceivedAttribute(kobold.pollRandomAttribute()))
        returnList.add(PerceivedAttribute(kobold.pollRandomAttribute()))

        return returnList
    }

    fun memoriesPN() : MutableList<PerceivedNoumenon> {

        val returnList = mutableListOf<PerceivedNoumenon>()
        val kobold = kobold { }

        returnList.add(PerceivedNoumenon().apply {
            this.perceivedAttributes.add(PerceivedAttribute(kobold.pollRandomAttribute()))
            this.internalStateInstance.internalState.add(fearFacet { magnitude = 0.3f })
            this.knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE)
            this.noumenonType = NoumenonType.KOBOLD
            this.isNamed = true
        })

        returnList.add(PerceivedNoumenon().apply {
            this.perceivedAttributes.add(PerceivedAttribute(kobold.pollRandomAttribute()))
            this.internalStateInstance.internalState.add(angerFacet { magnitude = 0.5f })
            this.knowledgeSourceInstance = KnowledgeSourceInstance(KnowledgeSourceType.EXPERIENCE)
            this.noumenonType = NoumenonType.KOBOLD
            this.isNamed = true
        })

        return returnList
    }
}