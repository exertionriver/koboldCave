package org.river.exertion.ai.memory

import com.badlogic.gdx.math.Vector3
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance
import org.river.exertion.ai.phenomena.InternalPhenomenaInstance.Companion.opinion

class CharacterRegister {

    //populated to begin, updated by resolution, other information
    var associativePerceptionList = mutableListOf<PerceivedAttributable>()
    var associativeNoumenaList = mutableListOf<PerceivedNoumenon>()

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