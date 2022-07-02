package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.internalSymbol.core.symbolAction.ISymbolAction

object NonePerceivedSymbol : IPerceivedSymbol {

    override var tag = "none perceived"
    override var type = SymbolType.NONE
    override var targetPosition = SymbolTargetPosition.NONE
    override var cycle = SymbolCycle.NONE

    override var symbolActions = mutableSetOf<ISymbolAction>()
    override var focusSatisfiers = mutableSetOf<IInternalFocus>()
    override var facetModifiers = mutableSetOf<FacetModifier>()

    override fun spawn() = SymbolInstance()
}
