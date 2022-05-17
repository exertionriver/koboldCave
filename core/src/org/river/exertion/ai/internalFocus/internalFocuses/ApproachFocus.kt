package org.river.exertion.ai.internalFocus.internalFocuses

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.PresentSymbolInstance

object ApproachFocus : IInternalFocus {

    override var tag = "approach"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>()
    override fun satisfyingCondition(targetSymbol : PresentSymbolInstance) = true

    override fun satisfyingResult(targetSymbol : PresentSymbolInstance) = targetSymbol.apply { this.position -= 0.05f }
}