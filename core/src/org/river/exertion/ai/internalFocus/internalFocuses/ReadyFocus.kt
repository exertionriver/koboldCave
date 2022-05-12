package org.river.exertion.ai.internalFocus.internalFocuses

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.symbol.PresentSymbolInstance
import org.river.exertion.ai.symbol.SymbolMagnetism

object ReadyFocus : IInternalFocus {

    override var tag = "ready"
    override var dependsUpon = mutableSetOf<IInternalFocus>()
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>()
    override fun satisfyingCondition(targetSymbol : PresentSymbolInstance) = PossessFocus.satisfyingCondition(targetSymbol)
    override fun satisfyingResult(targetSymbol : PresentSymbolInstance) = targetSymbol.apply { this.position = SymbolMagnetism.STABILIZE_HANDLING.targetPosition() }
}