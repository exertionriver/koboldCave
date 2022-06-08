package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.proximity.CloseIntimateFocus
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolTargetPosition

object PossessFocus : IInternalFocus {

    override var tag = "possess"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
        CloseIntimateFocus
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>(
        PickUpFocus
    )
    override fun satisfyingCondition(targetSymbol : SymbolInstance) = targetSymbol.position <= SymbolTargetPosition.STABILIZE_POSSESSION.targetPosition()
    override fun satisfyingResult(entity: Telegraph, targetSymbol : SymbolInstance) {}

}