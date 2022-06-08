package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolDespawnAction
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolSpawnInstanceAction

object HungerSymbol : IPerceivedSymbol {

    override var tag = "hunger"
    override var type = SymbolType.DRIVE
    override var targetPosition = SymbolTargetPosition.REPEL_LIMINAL
    override var cycle = SymbolCycle.SINGLE

    override var symbolActions = mutableSetOf(
        SymbolSpawnInstanceAction(StarveSymbol, SymbolDisplayType.PRESENT, SymbolThresholdType.LESS_THAN, .2f),
        SymbolDespawnAction(StarveSymbol, SymbolDisplayType.PRESENT, SymbolThresholdType.GREATER_THAN, .3f),

        SymbolSpawnInstanceAction(FoodSymbol, SymbolDisplayType.ABSENT, SymbolThresholdType.LESS_THAN, .6f),
        SymbolDespawnAction(FoodSymbol, SymbolDisplayType.ABSENT, SymbolThresholdType.GREATER_THAN, .8f),

        SymbolModifyAction(FoodSymbol, HungerSymbol, SymbolDisplayType.PRESENT, SymbolModifierType.CYCLE_TO_POSITION, .1f),
        SymbolModifyAction(MomentElapseSymbol, HungerSymbol, SymbolDisplayType.PRESENT, SymbolModifierType.POSITION_TO_POSITION, -.001f),

        SymbolModifyAction(HungerSymbol, FoodSymbol, SymbolDisplayType.ABSENT, SymbolModifierType.POSITION_TO_POSITION, -.1f)
    )

    override var focusSatisfiers = mutableSetOf<IInternalFocus>()

    override fun spawn() = SymbolInstance(HungerSymbol, position = SymbolTargetPosition.REPEL_LIMINAL.targetPosition())
}
