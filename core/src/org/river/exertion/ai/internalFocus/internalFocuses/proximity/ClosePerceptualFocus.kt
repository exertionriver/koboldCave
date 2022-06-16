package org.river.exertion.ai.internalFocus.internalFocuses.proximity

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.ApproachFocus
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolTargetPosition

object ClosePerceptualFocus : IInternalFocus {

    override var tag = "close perceptual focus"

    override var satisfyingStrategies = mutableListOf(
        CloseLiminalFocus,
        ApproachFocus
    )

    override fun satisfyingCondition(targetPresentSymbol : SymbolInstance) = targetPresentSymbol.position <= SymbolTargetPosition.STABILIZE_PERCEPTUAL.targetPosition()

    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {}

}