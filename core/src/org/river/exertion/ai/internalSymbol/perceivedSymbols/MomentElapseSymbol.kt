package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.internalSymbol.core.symbolAction.ISymbolAction

object MomentElapseSymbol : IPerceivedSymbol {

    override var tag = "moment elapse"
    override var type = SymbolType.TIME
    override var targetPosition = SymbolTargetPosition.ATTRACT_CONSUME
    override var cycle = SymbolCycle.MULTIPLE

    override var symbolActions = mutableSetOf<ISymbolAction>()
    override var focusSatisfiers = mutableSetOf<IInternalFocus>()

    override fun spawn() = SymbolInstance(MomentElapseSymbol, position = SymbolTargetPosition.REPEL_LIMINAL.targetPosition())
}
