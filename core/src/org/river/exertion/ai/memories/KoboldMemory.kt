package org.river.exertion.ai.memories

import org.river.exertion.ai.*
import org.river.exertion.btree.v0_1.KoboldCharacter

object KoboldMemory {

    fun memories() : MutableList<AssociativeMemory> {

        val returnList = mutableListOf<AssociativeMemory>()

        returnList.add(AssociativeMemory().apply {
            this.knowable =
            Knowable().apply {
                this.granularity = Knowable.KnowableGranularity.OTHER
                this.source = Knowable.KnowableSource.EXPERIENCE
                this.signature = Signature.empty()
                this.trust = .8f
            }
            this.internalPhenomenaInstance =
            InternalPhenomenaInstance().apply {
                this.type = InternalPhenomenaType.FEAR
                this.magnitude = .3f
                this.loss = 0f
            }
        })

        returnList.add(AssociativeMemory().apply {
            this.knowable =
                    Knowable().apply {
                        this.granularity = Knowable.KnowableGranularity.ENTITY_GROUP
                        this.source = Knowable.KnowableSource.EXPERIENCE
                        this.signature = Signature.empty().apply { type = "kobold"; group = "red hand" }
                        this.trust = .3f
                    }
            this.internalPhenomenaInstance =
                    InternalPhenomenaInstance().apply {
                        this.type = InternalPhenomenaType.AGGRESSION
                        this.magnitude = .5f
                        this.loss = 0f
                    }
        })

        return returnList
    }
}