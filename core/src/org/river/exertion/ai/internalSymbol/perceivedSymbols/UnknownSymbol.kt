package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFacet.AngerFacet
import org.river.exertion.ai.internalFacet.DisgustFacet
import org.river.exertion.ai.internalFacet.FearFacet
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.ConsumeFocus
import org.river.exertion.ai.internalFocus.internalFocuses.proximity.CloseFamiliarFocus
import org.river.exertion.ai.internalFocus.internalFocuses.proximity.ClosePerceptualFocus
import org.river.exertion.ai.internalFocus.internalFocuses.proximity.CloseSocialFocus
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.internalSymbol.core.symbolAction.ISymbolAction

object UnknownSymbol : IPerceivedSymbol {

    override var tag = "unknown"
    override var type = SymbolType.NEED
    override var targetPosition = SymbolTargetPosition.STABILIZE_PERCEPTUAL
    override var cycle = SymbolCycle.SINGLE

    override var symbolActions = mutableSetOf<ISymbolAction>()

    override var focusSatisfiers = mutableSetOf<IInternalFocus>(ClosePerceptualFocus)

//    override var facetModifiers = mutableSetOf<FacetModifier>()
    override var facetModifiers = mutableSetOf(
            FacetModifier(FearFacet, .1f)
    )
    override fun spawn() = SymbolInstance(UnknownSymbol, cycles = 1f, position = SymbolTargetPosition.REPEL_LIMINAL.targetPosition())
}
