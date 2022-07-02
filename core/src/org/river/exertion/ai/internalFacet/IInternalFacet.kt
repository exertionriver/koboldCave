package org.river.exertion.ai.internalFacet

import org.river.exertion.ai.internalSymbol.core.SymbolInstance

interface IInternalFacet {

    val type : InternalFacetType

    fun spawn() : InternalFacetInstance
}