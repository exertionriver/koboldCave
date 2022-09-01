package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.symbolAction.ISymbolAction

interface IPerceivedSymbolOrnament : ISymbol {

    override var tag : String
    override var type : SymbolType
    var baseTargetPosition : SymbolTargetPosition
    var facetModifiers : MutableSet<FacetModifier>
}