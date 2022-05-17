package org.river.exertion.ai.internalFocus.internalFocuses

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.PresentSymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolMagnetism

object HandleFocus : IInternalFocus {

    override var tag = "handle"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
        PossessFocus
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>(
        ReadyFocus
    )
    override fun satisfyingCondition(targetSymbol : PresentSymbolInstance) = targetSymbol.position <= SymbolMagnetism.STABILIZE_HANDLING.targetPosition()

    override fun satisfyingResult(targetSymbol : PresentSymbolInstance) = targetSymbol
}