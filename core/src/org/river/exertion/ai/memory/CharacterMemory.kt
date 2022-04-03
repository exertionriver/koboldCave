package org.river.exertion.ai.memory

import org.river.exertion.ai.internalState.FearState.fearState
import org.river.exertion.ai.internalState.InternalStateInstance
import org.river.exertion.ai.internalState.InternalStateInstance.Companion.magnitudeOpinion
import org.river.exertion.ai.internalState.InternalStateInstance.Companion.mergePlus
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.btree.v0_1.IBTCharacter

class CharacterMemory {

    //populated to begin, updated by resolution, other information
    var perceptionRegister = mutableListOf<PerceivedAttributable>()
    var noumenaRegister = mutableListOf<PerceivedNoumenon>()

    fun update(delta : Float, character : IBTCharacter) {

        character.characterManifest.getExternalPhenomenaList().forEach {
            val attrib = it.sender.noumenon.pollRandomAttribute(it.externalPhenomenaImpression.type) //poll random attribute not yet seen in encounter?

            if (attrib != null) {
                it.sender.noumenon.tags.forEach { noumenonTag ->
                    character.characterMemory.noumenaRegister.add(
                            PerceivedNoumenon.perceivedNoumenon(
                                    noumenonTag,
                                    attrib.attributableTag,
                                    KnowledgeSource(KnowledgeSource.SourceEnum.EXPERIENCE),
                                    InternalPhenomenaInstance().apply { arising = fearState { 0.3f } } //placeholder for regExec state
                            ).apply { isNamed = true }
                    )
                }

                character.characterMemory.perceptionRegister.add(
                        PerceivedAttributable.perceivedAttributable(
                                it.sender.noumenon.name,
                                attrib.attributableTag,
                                attrib.attributeValue,
                                KnowledgeSource(KnowledgeSource.SourceEnum.EXPERIENCE),
                                InternalPhenomenaInstance().apply { arising = fearState { 0.5f } } //placeholder for regExec state
                        ).apply { isNamed = true }
                )
            }
        }
    }

    fun opinions(onTopic : String) : Set<InternalStateInstance> {

        val attributePerceptions = perceptionRegister.filter { it.attributableTag == onTopic && it.isNamed }.sortedByDescending { it.internalPhenomenaInstance.arising.magnitude }
        val noumenaPerceptions = noumenaRegister.filter { it.noumenonTag == onTopic && it.isNamed }.sortedByDescending { it.internalPhenomenaInstance.arising.magnitude }

        val opinions = attributePerceptions.map { it.internalPhenomenaInstance.arising }.toSet().mergePlus(
                noumenaPerceptions.map { it.internalPhenomenaInstance.arising }.toSet() )

        return opinions
    }

    fun opinion(onTopic : String) : InternalStateInstance = opinions(onTopic).magnitudeOpinion()

}