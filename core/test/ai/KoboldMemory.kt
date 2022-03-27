package ai

import com.badlogic.gdx.math.Vector3
import org.river.exertion.ai.attributes.IntelligenceAttributable
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
            this.count++

            this.knowledgeSource = KnowledgeSource().apply {
                this.source = KnowledgeSource.Source.EXPERIENCE
                this.trust = .8f
            }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.origin = Vector3(.3f, .4f, .5f)
                        this.arising = Vector3(.4f, .4f, .4f)
                        this.loss = 0f
                    }
        })

        returnList.add(PerceivedAttributable().apply {
            this.attributableTag = IntelligenceAttributable.tag()
            this.attributeValue = IntelligenceAttributable.values().first()
            this.perceivedNoumenaTags.add(KoboldNoumenon().tag)
            this.count++

            this.knowledgeSource = KnowledgeSource().apply {
                this.source = KnowledgeSource.Source.LORE
                this.trust = .3f
            }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.origin = Vector3(.3f, .4f, .5f)
                        this.arising = Vector3(.5f, .4f, .5f)
                        this.loss = 0f
                    }
        })

        return returnList
    }

    fun memoriesPN() : MutableList<PerceivedNoumenon> {

        val returnList = mutableListOf<PerceivedNoumenon>()

        returnList.add(PerceivedNoumenon().apply {
            this.noumenonTag = OtherNoumenon.tag()
            this.perceivedAttributeTags.add(IntelligenceAttributable.tag())
            this.count++

            this.knowledgeSource = KnowledgeSource().apply {
                this.source = KnowledgeSource.Source.EXPERIENCE
                this.trust = .8f
            }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.origin = Vector3(.3f, .4f, .5f)
                        this.arising = Vector3(.4f, .4f, .4f)
                        this.loss = 0f
                    }
        })

        returnList.add(PerceivedNoumenon().apply {
            this.noumenonTag = KoboldNoumenon.tag()
            this.perceivedAttributeTags.add(IntelligenceAttributable.tag())
            this.isNamed = true
            this.count++

            this.knowledgeSource = KnowledgeSource().apply {
                this.source = KnowledgeSource.Source.LORE
                this.trust = .3f
            }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.origin = Vector3(.3f, .4f, .5f)
                        this.arising = Vector3(.5f, .4f, .5f)
                        this.loss = 0f
                    }
        })

        return returnList
    }
}