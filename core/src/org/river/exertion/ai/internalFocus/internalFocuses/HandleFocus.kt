package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolTargetPosition

object HandleFocus : IInternalFocus {

    override var tag = "handle"

    override var satisfyingStrategies = mutableListOf(
        ReadyFocus,
        PossessFocus
    )

    override fun satisfyingCondition(targetPresentSymbol : SymbolInstance) = targetPresentSymbol.position <= SymbolTargetPosition.STABILIZE_HANDLING.targetPosition()

    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {}
}