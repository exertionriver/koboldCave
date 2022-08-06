package org.river.exertion.ai.memory

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFacet.InternalFacetInstance
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState
import org.river.exertion.ai.internalFacet.InternalFacetInstancesState.Companion.merge
import org.river.exertion.ai.internalSymbol.core.IPerceivedSymbol
import org.river.exertion.ai.internalSymbol.perceivedSymbols.UnknownSymbol
import org.river.exertion.ai.perception.PerceivedNoumenon

interface IMemory {

    //populated to begin, updated by resolution, other information
    var entity : Telegraph
    var noumenaRegister : MutableSet<MemoryInstance>

    //todo: sort by facet magnitude?
    fun opinions(onTopic : String) : Set<IPerceivedSymbol> {

        val directNoumenaPerceptions = noumenaRegister.filter { (it.perceivedNoumenon.instanceName == onTopic || it.perceivedNoumenon.noumenonType.tag() == onTopic) && it.perceivedNoumenon.isNamed }

        return if (directNoumenaPerceptions.isNotEmpty() ) directNoumenaPerceptions.map { it.symbol }.toSet() else setOf(UnknownSymbol)
    }

    fun facts(onTopic : String) : List<String> {

        val returnFacts = mutableListOf<String>()

        val directNoumenaPerceptions = noumenaRegister.filter { (it.perceivedNoumenon.instanceName == onTopic || it.perceivedNoumenon.noumenonType.tag() == onTopic) && it.perceivedNoumenon.isNamed }

        directNoumenaPerceptions.forEach { returnFacts.addAll(it.perceivedNoumenon.facts(onTopic)) }

        return returnFacts.ifEmpty { listOf("unknown") }
    }

    fun opinion(onTopic : String) : IPerceivedSymbol? = opinions(onTopic).firstOrNull() ?: UnknownSymbol

}