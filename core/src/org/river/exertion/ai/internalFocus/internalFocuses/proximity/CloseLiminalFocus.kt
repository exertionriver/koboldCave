package org.river.exertion.ai.internalFocus.internalFocuses.proximity

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.symbol.PresentSymbolInstance
import org.river.exertion.ai.symbol.SymbolMagnetism

object CloseLiminalFocus : IInternalFocus {

    override var tag = "close liminal focus"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
        //memory
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>()
    override fun satisfyingCondition(targetSymbol : PresentSymbolInstance) = targetSymbol.position <= SymbolMagnetism.REPEL_LIMINAL.targetPosition()

    override fun satisfyingResult(targetSymbol : PresentSymbolInstance) = targetSymbol

}