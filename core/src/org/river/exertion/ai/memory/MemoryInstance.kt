package org.river.exertion.ai.memory

import org.river.exertion.ai.internalFacet.InternalFacetInstancesState
import org.river.exertion.ai.internalSymbol.core.IPerceivedSymbol
import org.river.exertion.ai.perception.PerceivedNoumenon

data class MemoryInstance(val perceivedNoumenon: PerceivedNoumenon, var symbol: IPerceivedSymbol)
