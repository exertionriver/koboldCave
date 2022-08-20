package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

object RetreatFocus : IInternalFocus {

    override var tag = "approach"
    override var type = InternalFocusType.ACTION
    override var momentMinimum = 4f

    override var satisfyingStrategies = mutableListOf<IInternalFocus>()

    override fun satisfyingCondition(targetPresentSymbol : SymbolInstance) = true

    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {
        MessageChannel.INT_SYMBOL_MODIFY.send(entity, SymbolMessage(symbolInstance = targetPresentSymbol.apply { deltaPosition = 0.05f }))
    }
}