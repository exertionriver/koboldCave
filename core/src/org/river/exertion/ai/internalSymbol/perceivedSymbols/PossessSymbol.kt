package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.internalSymbol.core.symbolAction.ISymbolAction

object PossessSymbol : IPerceivedSymbol {

    override var tag = "possess"
    override var type = SymbolType.ORNAMENT
    override var targetPosition = SymbolTargetPosition.NONE
    override var cycle = SymbolCycle.NONE

    override var symbolActions = mutableSetOf<ISymbolAction>()
    override var focusSatisfiers = mutableSetOf<IInternalFocus>()
    override var facetModifiers = mutableSetOf<FacetModifier>()

    override fun spawn() = SymbolInstance()
}
