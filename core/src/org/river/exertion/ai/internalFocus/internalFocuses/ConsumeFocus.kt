package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusType
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

object ConsumeFocus : IInternalFocus {

    override var tag = "consume"
    override var type = InternalFocusType.ACTION
    override var momentMinimum = 4f

    override var satisfyingStrategies = mutableListOf<IInternalFocus>(
        HandleFocus
    )
    override fun satisfyingCondition(targetPresentSymbol : SymbolInstance) = HandleFocus.satisfyingCondition(targetPresentSymbol)

    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {
        //consume capacity or consume remaining

        if (targetPresentSymbol.cycles <= targetPresentSymbol.consumeCapacity) {
            SymbolModifyAction.executeImmediate(entity, SymbolMessage(symbolInstance = targetPresentSymbol.apply { this.deltaCycles = -targetPresentSymbol.cycles }))

            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_DESPAWN.id(), SymbolMessage(symbolInstance = targetPresentSymbol.apply { this.displayType = SymbolDisplayType.PRESENT}))
        } else {
            SymbolModifyAction.executeImmediate(entity, SymbolMessage(symbolInstance = targetPresentSymbol.apply { this.deltaCycles = -targetPresentSymbol.consumeCapacity }))

//            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_MODIFY.id(), SymbolMessage(symbolInstance = targetPresentSymbol.apply { this.deltaCycles = -targetPresentSymbol.consumeCapacity}))
        }
    }
}