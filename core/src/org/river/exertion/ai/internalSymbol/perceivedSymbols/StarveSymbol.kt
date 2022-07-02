package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFacet.FearFacet
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.internalSymbol.core.symbolAction.ISymbolAction
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction

object StarveSymbol : IPerceivedSymbol {

    override var tag = "starving"
    override var type = SymbolType.DRIVE
    override var targetPosition = SymbolTargetPosition.REPEL_LIMINAL
    override var cycle = SymbolCycle.SINGLE

    override var symbolActions = mutableSetOf<ISymbolAction>(
        SymbolModifyAction(MomentElapseSymbol, StarveSymbol, -.005f, SymbolModifierType.POSITION_TO_POSITION),

            //for use with impact
//        SymbolModifyAction(StarveSymbol, FoodSymbol, 0f, SymbolModifierType.POSITION_TO_POSITION)
    )
    override var focusSatisfiers = mutableSetOf<IInternalFocus>()
    override var facetModifiers = mutableSetOf(
            FacetModifier(FearFacet, .3f)
    )
    override fun spawn() = SymbolInstance(StarveSymbol, position = SymbolTargetPosition.REPEL_LIMINAL.targetPosition())
}
