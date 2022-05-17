package org.river.exertion.ai.internalFocus.internalFocuses.proximity

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.PresentSymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolMagnetism

object ClosePerceptualFocus : IInternalFocus {

    override var tag = "close perceptual focus"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
        ClosePerceptualFocus
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>()
    override fun satisfyingCondition(targetSymbol : PresentSymbolInstance) = targetSymbol.position <= SymbolMagnetism.STABILIZE_PERCEPTUAL.targetPosition()

    override fun satisfyingResult(targetSymbol : PresentSymbolInstance) = targetSymbol

}