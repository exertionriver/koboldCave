package org.river.exertion.ai.memory

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFacet.InternalFacetInstance
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState.Companion.merge
import org.river.exertion.ai.perception.PerceivedNoumenon

interface IMemory {

    //populated to begin, updated by resolution, other information
    var entity : Telegraph
    var noumenaRegister : MutableSet<MemoryInstance>

    fun opinions(onTopic : String) : Set<InternalFacetInstancesState> {

        val directNoumenaPerceptions = noumenaRegister.filter { (it.perceivedNoumenon.instanceName == onTopic || it.perceivedNoumenon.noumenonType.tag() == onTopic) && it.perceivedNoumenon.isNamed }

        return directNoumenaPerceptions.map { it.internalFacetInstancesState }.toSet()
    }

    fun facts(onTopic : String) : List<String> {

        val returnFacts = mutableListOf<String>()

        val directNoumenaPerceptions = noumenaRegister.filter { (it.perceivedNoumenon.instanceName == onTopic || it.perceivedNoumenon.noumenonType.tag() == onTopic) && it.perceivedNoumenon.isNamed }

        directNoumenaPerceptions.forEach { returnFacts.addAll(it.perceivedNoumenon.facts()) }

        return returnFacts
    }

    fun opinion(onTopic : String) : InternalFacetInstance? = opinions(onTopic).merge(entity).magnitudeOpinion()

}