package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFacet.FearFacet
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolDespawnAction
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolSpawnAction

object HungerSymbol : IPerceivedSymbol {

    override var tag = "hunger"
    override var type = SymbolType.DRIVE
    override var targetPosition = SymbolTargetPosition.REPEL_LIMINAL
    override var cycle = SymbolCycle.SINGLE

    override var symbolActions = mutableSetOf(
        SymbolSpawnAction(StarveSymbol, .2f, SymbolThresholdType.LESS_THAN),
        SymbolDespawnAction(StarveSymbol, .3f, SymbolThresholdType.GREATER_THAN),

        SymbolSpawnAction(FoodSymbol, .6f, SymbolThresholdType.LESS_THAN, SymbolDisplayType.ABSENT),
        SymbolDespawnAction(FoodSymbol, .8f, SymbolThresholdType.GREATER_THAN, SymbolDisplayType.ABSENT),

        SymbolModifyAction(FoodSymbol, HungerSymbol, .1f, SymbolModifierType.CYCLE_TO_POSITION),
        SymbolModifyAction(MomentElapseSymbol, HungerSymbol, -.001f, SymbolModifierType.POSITION_TO_POSITION),

        SymbolModifyAction(HungerSymbol, FoodSymbol, -1f, SymbolModifierType.POSITION_TO_POSITION, SymbolDisplayType.ABSENT)
    )

    override var focusSatisfiers = mutableSetOf<IInternalFocus>()

    override var facetModifiers = mutableSetOf(
        FacetModifier(FearFacet, .1f)
    )

    override fun spawn() = SymbolInstance(HungerSymbol, position = SymbolTargetPosition.REPEL_LIMINAL.targetPosition())
}
