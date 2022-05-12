package org.river.exertion.ai.internalFocus.internalFocuses.proximity

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.ApproachFocus
import org.river.exertion.ai.symbol.PresentSymbolInstance
import org.river.exertion.ai.symbol.SymbolMagnetism

object CloseIntimateFocus : IInternalFocus {

    override var tag = "close intimate focus"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
        CloseFamiliarFocus
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>(
        ApproachFocus
    )
    override fun satisfyingCondition(targetSymbol : PresentSymbolInstance) = targetSymbol.position <= SymbolMagnetism.STABILIZE_INTIMATE.targetPosition()

    override fun satisfyingResult(targetSymbol : PresentSymbolInstance) = targetSymbol

}