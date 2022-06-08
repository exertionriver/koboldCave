package org.river.exertion.ai.internalFocus.internalFocuses.proximity

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolTargetPosition

object CloseLiminalFocus : IInternalFocus {

    override var tag = "close liminal focus"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
        //memory
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>()

    override fun satisfyingCondition(targetSymbol : SymbolInstance) = targetSymbol.position <= SymbolTargetPosition.REPEL_LIMINAL.targetPosition()

    override fun satisfyingResult(entity: Telegraph, targetSymbol : SymbolInstance) {}

}