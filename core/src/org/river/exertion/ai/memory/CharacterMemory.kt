package org.river.exertion.ai.memory

import org.river.exertion.ai.internalState.FearState.fearState
import org.river.exertion.ai.internalState.InternalStateInstance
import org.river.exertion.ai.internalState.InternalStateInstance.Companion.magnitudeOpinion
import org.river.exertion.ai.internalState.InternalStateInstance.Companion.mergeAvg
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

        var avgBy = 0

        val directAttributePerceptions = perceptionRegister.filter { it.attributableTag == onTopic && it.isNamed }
        val directNoumenaPerceptions = noumenaRegister.filter { it.noumenonTag == onTopic && it.isNamed }

        val indirectAttributePerceptions = perceptionRegister.filter { it.perceivedNoumenaTags.contains(onTopic) }
        val indirectNoumenaPerceptions = noumenaRegister.filter { it.perceivedAttributableTags.contains(onTopic) }

        avgBy = directAttributePerceptions.size + directNoumenaPerceptions.size + indirectAttributePerceptions.size + indirectNoumenaPerceptions.size

        val directOpinions = directAttributePerceptions.map { it.internalPhenomenaInstance.arising }.toSet().mergeAvg(
                directNoumenaPerceptions.map { it.internalPhenomenaInstance.arising }.toSet(), avgBy )

        val inDirectOpinions = indirectAttributePerceptions.map { it.internalPhenomenaInstance.arising }.toSet().mergeAvg(
                indirectNoumenaPerceptions.map { it.internalPhenomenaInstance.arising }.toSet(), avgBy )

        avgBy = directOpinions.size + inDirectOpinions.size

        return directOpinions.mergeAvg(inDirectOpinions, avgBy)
    }

    fun opinion(onTopic : String) : InternalStateInstance = opinions(onTopic).magnitudeOpinion()

}