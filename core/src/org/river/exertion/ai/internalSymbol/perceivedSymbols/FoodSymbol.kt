package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.ConsumeFocus
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.internalSymbol.core.symbolAction.ISymbolAction

object FoodSymbol : IPerceivedSymbol {

    override var tag = "food"
    override var type = SymbolType.NEED
    override var targetPosition = SymbolTargetPosition.ATTRACT_CONSUME
    override var cycle = SymbolCycle.SINGLE

    override var symbolActions = mutableSetOf<ISymbolAction>()

    override var focusSatisfiers = mutableSetOf<IInternalFocus>(ConsumeFocus)

    override fun spawn() = SymbolInstance(FoodSymbol, position = SymbolTargetPosition.REPEL_LIMINAL.targetPosition())
}
