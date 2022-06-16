package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

object ApproachFocus : IInternalFocus {

    override var tag = "approach"

    override var satisfyingStrategies = mutableListOf<IInternalFocus>()

    override fun satisfyingCondition(targetPresentSymbol : SymbolInstance) = true

    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {
        SymbolModifyAction.executeImmediate(entity, SymbolMessage(symbolInstance = targetPresentSymbol.apply { deltaPosition = -0.05f }))
//        MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_MODIFY.id(), SymbolMessage(symbolInstance = targetPresentSymbol.apply { this.deltaPosition = -0.05f }))
    }
}