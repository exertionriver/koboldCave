package org.river.exertion.ai.internalFocus.internalFocuses.proximity

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.ApproachFocus
import org.river.exertion.ai.internalSymbol.core.PresentSymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolMagnetism

object CloseFamiliarFocus : IInternalFocus {

    override var tag = "close familiar focus"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
        CloseSocialFocus
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>(
        ApproachFocus
    )
    override fun satisfyingCondition(targetSymbol : PresentSymbolInstance) = targetSymbol.position <= SymbolMagnetism.STABILIZE_FAMILIAR.targetPosition()

    override fun satisfyingResult(targetSymbol : PresentSymbolInstance) = targetSymbol

}