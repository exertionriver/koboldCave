package org.river.exertion.ai.internalFocus.internalFocuses

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.proximity.CloseIntimateFocus
import org.river.exertion.ai.symbol.PresentSymbolInstance
import org.river.exertion.ai.symbol.SymbolMagnetism

object PossessFocus : IInternalFocus {

    override var tag = "possess"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
        CloseIntimateFocus
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>(
        PickUpFocus
    )
    override fun satisfyingCondition(targetSymbol : PresentSymbolInstance) = targetSymbol.position <= SymbolMagnetism.STABILIZE_POSSESSION.targetPosition()
    override fun satisfyingResult(targetSymbol : PresentSymbolInstance) = targetSymbol

}