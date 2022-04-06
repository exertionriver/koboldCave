package org.river.exertion.ai.memory

import org.river.exertion.ai.internalState.InternalFacetInstance
import org.river.exertion.ai.internalState.InternalStateInstance
import org.river.exertion.ai.internalState.InternalStateInstance.Companion.merge
import org.river.exertion.ai.perception.PerceivedNoumenon

interface IMemory {

    //populated to begin, updated by resolution, other information
    var noumenaRegister : MutableList<PerceivedNoumenon>

    fun opinions(onTopic : String) : Set<InternalStateInstance> {

        val directNoumenaPerceptions = noumenaRegister.filter { (it.instanceName == onTopic || it.noumenonType.tag() == onTopic) && it.isNamed }

        return directNoumenaPerceptions.map { it.internalStateInstance }.toSet()
    }

    fun facts(onTopic : String) : List<String> {

        val returnFacts = mutableListOf<String>()

        val directNoumenaPerceptions = noumenaRegister.filter { (it.instanceName == onTopic || it.noumenonType.tag() == onTopic) && it.isNamed }

        directNoumenaPerceptions.forEach { returnFacts.addAll(it.facts()) }

        return returnFacts
    }

    fun opinion(onTopic : String) : InternalFacetInstance? = opinions(onTopic).merge().magnitudeOpinion()

}