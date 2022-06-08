package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolTargetPosition

object HandleFocus : IInternalFocus {

    override var tag = "handle"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
        PossessFocus
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>(
        ReadyFocus
    )
    override fun satisfyingCondition(targetSymbol : SymbolInstance) = targetSymbol.position <= SymbolTargetPosition.STABILIZE_HANDLING.targetPosition()

    override fun satisfyingResult(entity: Telegraph, targetSymbol : SymbolInstance) {}
}