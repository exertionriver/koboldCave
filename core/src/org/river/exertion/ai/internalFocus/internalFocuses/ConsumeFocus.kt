package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

object ConsumeFocus : IInternalFocus {

    override var tag = "consume"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
        HandleFocus
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>(
        HandleFocus
    )
    override fun satisfyingCondition(targetSymbol : SymbolInstance) = HandleFocus.satisfyingCondition(targetSymbol)

    override fun satisfyingResult(entity: Telegraph, targetSymbol : SymbolInstance) {
        //consume capacity or consume remaining
        MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_MODIFY.id(), SymbolMessage().apply{this.targetSymbolInstance = targetSymbol.apply { this.deltaCycles = -targetSymbol.consumeCapacity; this.displayType = SymbolDisplayType.PRESENT}})

        if (targetSymbol.cycles < targetSymbol.consumeCapacity) {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_DESPAWN.id(), SymbolMessage().apply{this.targetSymbolInstance = targetSymbol.apply { this.displayType = SymbolDisplayType.PRESENT}})
        }
    }
}