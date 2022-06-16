package org.river.exertion.ai.internalFocus.internalFocuses.proximity

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.ApproachFocus
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolTargetPosition

object CloseFamiliarFocus : IInternalFocus {

    override var tag = "close familiar focus"

    override var satisfyingStrategies = mutableListOf(
        CloseSocialFocus,
        ApproachFocus
    )
    override fun satisfyingCondition(targetPresentSymbol : SymbolInstance) = targetPresentSymbol.position <= SymbolTargetPosition.STABILIZE_FAMILIAR.targetPosition()

    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {}

}