package org.river.exertion.ai.internalFocus.internalFocuses

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.symbol.PresentSymbolInstance

object NoneFocus : IInternalFocus {

    override var tag = "none"
    override var dependsUpon = mutableSetOf<IInternalFocus>()
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>()
    override fun satisfyingCondition(targetSymbol : PresentSymbolInstance) = false
    override fun satisfyingResult(targetSymbol : PresentSymbolInstance) = targetSymbol

}