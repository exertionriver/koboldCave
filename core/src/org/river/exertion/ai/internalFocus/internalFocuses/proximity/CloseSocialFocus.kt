package org.river.exertion.ai.internalFocus.internalFocuses.proximity

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolTargetPosition

object CloseSocialFocus : IInternalFocus {

    override var tag = "close social focus"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
        ClosePerceptualFocus
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>()

    override fun satisfyingCondition(targetSymbol : SymbolInstance) = targetSymbol.position <= SymbolTargetPosition.STABILIZE_SOCIAL.targetPosition()

    override fun satisfyingResult(entity: Telegraph, targetSymbol : SymbolInstance) {}

}