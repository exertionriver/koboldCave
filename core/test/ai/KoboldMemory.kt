package ai

import com.badlogic.gdx.math.Vector3
import org.river.exertion.ai.attributes.IntelligenceAttributable
import org.river.exertion.ai.manifest.InternalState.Companion.scaleToMagnitude
import org.river.exertion.ai.manifest.InternalStateBiases
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
            this.attributableTag = IntelligenceAttributable.tag()
            this.attributeValue = IntelligenceAttributable.values().first()
            this.perceivedNoumenaTags.add(OtherNoumenon.tag())
            this.isNamed = true
            this.count++

            this.knowledgeSource = KnowledgeSource(KnowledgeSource.SourceEnum.EXPERIENCE).apply {
                this.trust = .8f
            }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.origin = InternalStateBiases.none()
                        this.arising = InternalStateBiases.fear()
                        this.loss = 0f
                    }
        })

        returnList.add(PerceivedAttributable().apply {
            this.attributableTag = IntelligenceAttributable.tag()
            this.attributeValue = IntelligenceAttributable.values().first()
            this.perceivedNoumenaTags.add(KoboldNoumenon.tag())
            this.isNamed = true
            this.count++

            this.knowledgeSource = KnowledgeSource(KnowledgeSource.SourceEnum.LORE).apply {
                this.trust = .3f
            }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.origin = InternalStateBiases.none()
                        this.arising = InternalStateBiases.anger().apply { this.aGrid.x = 0.8f }.scaleToMagnitude()
                        this.loss = 0f
                    }
        })

        return returnList
    }

    fun memoriesPN() : MutableList<PerceivedNoumenon> {

        val returnList = mutableListOf<PerceivedNoumenon>()

        returnList.add(PerceivedNoumenon().apply {
            this.noumenonTag = OtherNoumenon.tag()
            this.perceivedAttributableTags.add(IntelligenceAttributable.tag())
            this.isNamed = true
            this.count++

            this.knowledgeSource = KnowledgeSource(KnowledgeSource.SourceEnum.EXPERIENCE).apply {
                this.trust = .8f
            }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.origin = InternalStateBiases.none()
                        this.arising = InternalStateBiases.fear()
                        this.loss = 0f
                    }
        })

        returnList.add(PerceivedNoumenon().apply {
            this.noumenonTag = KoboldNoumenon.tag()
            this.perceivedAttributableTags.add(IntelligenceAttributable.tag())
            this.isNamed = true
            this.count++

            this.knowledgeSource = KnowledgeSource(KnowledgeSource.SourceEnum.LORE).apply {
                this.trust = .3f
            }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.origin = InternalStateBiases.none()
                        this.arising = InternalStateBiases.anger()
                        this.loss = 0f
                    }
        })

        return returnList
    }
}