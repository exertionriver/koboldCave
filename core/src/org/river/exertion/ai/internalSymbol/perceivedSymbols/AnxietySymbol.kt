package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFacet.DisgustFacet
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.ConsumeFocus
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.internalSymbol.core.symbolAction.ISymbolAction

object AnxietySymbol : IPerceivedSymbol {

    override var tag = "anxiety"
    override var type = SymbolType.DRIVE
    override var baseTargetPosition = SymbolTargetPosition.ATTRACT_CONSUME
    override var cycle = SymbolCycle.SINGLE

    override var symbolActions = mutableSetOf<ISymbolAction>()

    override var focusSatisfiers = mutableSetOf<IInternalFocus>(ConsumeFocus)

//    override var facetModifiers = mutableSetOf<FacetModifier>()
    override var facetModifiers = mutableSetOf(
            FacetModifier(DisgustFacet, .01f)
    )
    override fun spawn() = SymbolInstance(AnxietySymbol, cycles = 1f, position = SymbolTargetPosition.ATTRACT_CONSUME.targetPosition(), initTargetPosition = baseTargetPosition)
}
