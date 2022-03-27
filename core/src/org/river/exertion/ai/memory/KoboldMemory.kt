package org.river.exertion.ai.memory

import com.badlogic.gdx.math.Vector3
import org.river.exertion.ai.*
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance

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
                this.origin = Vector3(.3f, .4f, .5f)
                this.arising = Vector3(.4f, .4f, .4f)
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
                        this.origin = Vector3(.3f, .4f, .5f)
                        this.arising = Vector3(.5f, .4f, .5f)
                        this.loss = 0f
                    }
        })

        return returnList
    }
}