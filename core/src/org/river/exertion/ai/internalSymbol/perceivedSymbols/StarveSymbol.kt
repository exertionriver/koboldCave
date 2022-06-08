package org.river.exertion.ai.internalSymbol.perceivedSymbols

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
        SymbolModifyAction(MomentElapseSymbol, StarveSymbol, SymbolDisplayType.PRESENT, SymbolModifierType.POSITION_TO_POSITION, -.005f),

        SymbolModifyAction(StarveSymbol, FoodSymbol, SymbolDisplayType.ABSENT, SymbolModifierType.POSITION_TO_POSITION, 0f)
    )
    override var focusSatisfiers = mutableSetOf<IInternalFocus>()

    override fun spawn() = SymbolInstance(StarveSymbol, position = SymbolTargetPosition.REPEL_LIMINAL.targetPosition())
}
