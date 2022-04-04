package ai

import org.river.exertion.ai.attributes.IntelligenceAttribute
import org.river.exertion.ai.internalState.AngerState.angerState
import org.river.exertion.ai.internalState.FearState.fearState
import org.river.exertion.ai.memory.KnowledgeSource
import org.river.exertion.ai.memory.PerceivedAttributable
import org.river.exertion.ai.memory.PerceivedNoumenon
import org.river.exertion.ai.noumena.KoboldNoumenon
import org.river.exertion.ai.noumena.OtherNoumenon
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance

object KoboldMemory {

    fun memoriesPA() : MutableList<PerceivedAttributable> {

        val returnList = mutableListOf<PerceivedAttributable>()

        returnList.add(PerceivedAttributable().apply {
            this.attributableTag = IntelligenceAttribute.tag()
            this.attributeValue = IntelligenceAttribute.values().first()
            this.perceivedNoumenaTags.add(OtherNoumenon.tag())
            this.isNamed = true
            this.count++

            this.knowledgeSource = KnowledgeSource(KnowledgeSource.SourceEnum.EXPERIENCE).apply {
                this.trust = .8f
            }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.arising = fearState { 0.3f }
                        this.loss = 0f
                    }
        })

        returnList.add(PerceivedAttributable().apply {
            this.attributableTag = IntelligenceAttribute.tag()
            this.attributeValue = IntelligenceAttribute.values().first()
            this.perceivedNoumenaTags.add(KoboldNoumenon.tag())
            this.isNamed = true
            this.count++

            this.knowledgeSource = KnowledgeSource(KnowledgeSource.SourceEnum.LORE).apply {
                this.trust = .3f
            }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.arising = angerState { 0.4f }
                        this.loss = 0f
                    }
        })

        return returnList
    }

    fun memoriesPN() : MutableList<PerceivedNoumenon> {

        val returnList = mutableListOf<PerceivedNoumenon>()

        returnList.add(PerceivedNoumenon().apply {
            this.noumenonTag = OtherNoumenon.tag()
            this.perceivedAttributableTags.add(IntelligenceAttribute.tag())
            this.isNamed = true
            this.count++

            this.knowledgeSource = KnowledgeSource(KnowledgeSource.SourceEnum.EXPERIENCE).apply {
                this.trust = .8f
            }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.arising = fearState { 0.3f }
                        this.loss = 0f
                    }
        })

        returnList.add(PerceivedNoumenon().apply {
            this.noumenonTag = KoboldNoumenon.tag()
            this.perceivedAttributableTags.add(IntelligenceAttribute.tag())
            this.isNamed = true
            this.count++

            this.knowledgeSource = KnowledgeSource(KnowledgeSource.SourceEnum.LORE).apply {
                this.trust = .3f
            }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.arising = angerState { 0.4f }
                        this.loss = 0f
                    }
        })

        return returnList
    }
}