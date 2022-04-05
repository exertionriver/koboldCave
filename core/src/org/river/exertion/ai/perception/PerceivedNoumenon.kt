package org.river.exertion.ai.perception

import org.river.exertion.ai.internalState.InternalStateInstance
import org.river.exertion.ai.memory.KnowledgeSourceInstance
import org.river.exertion.ai.noumena.NoumenonType

data class PerceivedNoumenon(var perceivedAttributes : MutableSet<PerceivedAttribute> = mutableSetOf(), var internalStateInstance: InternalStateInstance = InternalStateInstance(), var knowledgeSourceInstance: KnowledgeSourceInstance = KnowledgeSourceInstance()) {

    var noumenonType : NoumenonType = NoumenonType.NONE
    var isNamed : Boolean = false
}

