package org.river.exertion.ai.memory

import org.river.exertion.ai.internalState.InternalFacetInstance
import org.river.exertion.ai.internalState.InternalStateInstance
import org.river.exertion.ai.perception.PerceivedNoumenon

interface IMemory {

    //populated to begin, updated by resolution, other information
    var noumenaRegister : MutableList<PerceivedNoumenon>

    fun opinions(onTopic : String) : List<InternalStateInstance> {

        val test123 = noumenaRegister

        var avgBy = 0

        val directNoumenaPerceptions = noumenaRegister.filter { it.noumenonType.tag() == onTopic && it.isNamed }

        avgBy = directNoumenaPerceptions.size

        return directNoumenaPerceptions.map { it.internalStateInstance }
    }

    fun opinion(onTopic : String) : InternalFacetInstance? = opinions(onTopic).firstOrNull()?.magnitudeOpinion()

}