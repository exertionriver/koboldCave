package org.river.exertion.ai.memory

import com.badlogic.gdx.math.Vector3
import org.river.exertion.ai.manifest.InternalState
import org.river.exertion.ai.manifest.InternalStateBiases
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance.Companion.opinion
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
                                    InternalPhenomenaInstance().apply { arising = InternalStateBiases.fear() } //placeholder for regExec state
                            ).apply { isNamed = true }
                    )
                }

                character.characterMemory.perceptionRegister.add(
                        PerceivedAttributable.perceivedAttributable(
                                it.sender.noumenon.name,
                                attrib.attributableTag,
                                attrib.attributeValue,
                                KnowledgeSource(KnowledgeSource.SourceEnum.EXPERIENCE),
                                InternalPhenomenaInstance().apply { arising = InternalStateBiases.fear() } //placeholder for regExec state
                        ).apply { isNamed = true }
                )
            }
        }
    }

    fun opinions(onTopic : String) : List<InternalPhenomenaInstance> {

        val attributePerceptions = perceptionRegister.filter { it.attributableTag == onTopic && it.isNamed }.sortedByDescending { it.internalPhenomenaInstance.magnitude() }
        val noumenaPerceptions = noumenaRegister.filter { it.noumenonTag == onTopic && it.isNamed }.sortedByDescending { it.internalPhenomenaInstance.magnitude() }

        return attributePerceptions.map { it.internalPhenomenaInstance }.toMutableList() +
                noumenaPerceptions.map { it.internalPhenomenaInstance }.toMutableList()
    }

    fun opinion(onTopic : String) : InternalState {

        val attributePerceptions = perceptionRegister.filter { it.attributableTag == onTopic && it.isNamed }.sortedByDescending { it.internalPhenomenaInstance.magnitude() }
        val noumenaPerceptions = noumenaRegister.filter { it.noumenonTag == onTopic && it.isNamed }.sortedByDescending { it.internalPhenomenaInstance.magnitude() }

        return (attributePerceptions.map { it.internalPhenomenaInstance }.toMutableList() +
                noumenaPerceptions.map { it.internalPhenomenaInstance }.toMutableList() ).opinion()
    }


}