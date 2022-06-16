package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.SymbolInstance

object NoneFocus : IInternalFocus {

    override var tag = "none"
    override var satisfyingStrategies = mutableListOf<IInternalFocus>()
    override fun satisfyingCondition(targetPresentSymbol : SymbolInstance) = false
    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {}

}