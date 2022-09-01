package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFacet.FearFacet
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.FriendFocus
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.internalSymbol.core.symbolAction.ISymbolAction
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolSpawnAction

object FriendSymbol : IPerceivedSymbol {

    override var tag = "friend"
    override var type = SymbolType.NEED
    override var baseTargetPosition = SymbolTargetPosition.STABILIZE_FAMILIAR
    override var cycle = SymbolCycle.SINGLE

    override var symbolActions = mutableSetOf<ISymbolAction>(
        SymbolSpawnAction(FriendSymbol, 1f, SymbolThresholdType.LESS_THAN, SymbolDisplayType.ABSENT),
    )

    override var focusSatisfiers = mutableSetOf<IInternalFocus>(FriendFocus)

    override var facetModifiers = mutableSetOf(
        FacetModifier(FearFacet, -.1f)
    )

    override fun spawn() = SymbolInstance(FriendSymbol, cycles = 1f, position = SymbolTargetPosition.REPEL_LIMINAL.targetPosition(), initTargetPosition = baseTargetPosition)
}
