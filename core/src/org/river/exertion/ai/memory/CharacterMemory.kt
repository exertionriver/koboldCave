package org.river.exertion.ai.memory

import com.badlogic.gdx.math.Vector3
import org.river.exertion.ai.attributes.AttributeValue
import org.river.exertion.ai.manifest.CharacterManifest
import org.river.exertion.ai.noumena.IndividualNoumenon
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance.Companion.opinion
import org.river.exertion.btree.v0_1.IBTCharacter

class CharacterMemory {

    //populated to begin, updated by resolution, other information
    var associativePerceptionList = mutableListOf<PerceivedAttributable>()
    var associativeNoumenaList = mutableListOf<PerceivedNoumenon>()

    fun update(delta : Float, character : IBTCharacter) {

        character.characterManifest.getExternalPhenomenaList().forEach {
            val attrib = it.first.noumenon.pollRandomAttribute(it.second.type)

            if (attrib != null) {
                it.first.noumenon.tags().forEach { noumenonTag ->
                    character.characterMemory.associativeNoumenaList.add(
                            PerceivedNoumenon.perceivedNoumenon(
                                    noumenonTag,
                                    attrib.first,
                                    KnowledgeSource(KnowledgeSource.SourceEnum.EXPERIENCE),
                                    InternalPhenomenaInstance().apply { arising = Vector3(0.5f, 0.3f, 0.4f) }
                            ).apply { isNamed = true }
                    )
                }

                character.characterMemory.associativePerceptionList.add(
                        PerceivedAttributable.perceivedAttributable(
                                it.first.noumenon.name,
                                attrib.first,
                                attrib.second.second,
                                KnowledgeSource(KnowledgeSource.SourceEnum.EXPERIENCE),
                                InternalPhenomenaInstance().apply { arising = Vector3(0.5f, 0.3f, 0.4f) }
                        ).apply { isNamed = true }
                )
            }
        }
    }

    fun opinions(onTopic : String) : List<InternalPhenomenaInstance> {

        val attributePerceptions = associativePerceptionList.filter { it.attributableTag == onTopic && it.isNamed }.sortedByDescending { it.internalPhenomenaInstance.magnitude() }
        val noumenaPerceptions = associativeNoumenaList.filter { it.noumenonTag == onTopic && it.isNamed }.sortedByDescending { it.internalPhenomenaInstance.magnitude() }

        return attributePerceptions.map { it.internalPhenomenaInstance }.toMutableList() +
                noumenaPerceptions.map { it.internalPhenomenaInstance }.toMutableList()
    }

    fun opinion(onTopic : String) : Vector3 {

        val attributePerceptions = associativePerceptionList.filter { it.attributableTag == onTopic && it.isNamed }.sortedByDescending { it.internalPhenomenaInstance.magnitude() }
        val noumenaPerceptions = associativeNoumenaList.filter { it.noumenonTag == onTopic && it.isNamed }.sortedByDescending { it.internalPhenomenaInstance.magnitude() }

        return (attributePerceptions.map { it.internalPhenomenaInstance }.toMutableList() +
                noumenaPerceptions.map { it.internalPhenomenaInstance }.toMutableList() ).opinion()
    }


}