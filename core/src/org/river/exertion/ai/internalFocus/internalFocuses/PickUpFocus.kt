package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.proximity.CloseIntimateFocus
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolTargetPosition
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

object PickUpFocus : IInternalFocus {

    override var tag = "pick up"
    override var dependsUpon = mutableSetOf<IInternalFocus>()
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>()
    override fun satisfyingCondition(targetSymbol : SymbolInstance) = CloseIntimateFocus.satisfyingCondition(targetSymbol)
    override fun satisfyingResult(entity: Telegraph, targetSymbol : SymbolInstance) {

        if (targetSymbol.cycles > targetSymbol.handleCapacity) {
//            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_PRESENT_SYMBOL_MODIFY.id(), PresentSymbolMessage(targetSymbol, deltaCycles = -targetSymbol.handleCapacity, 0f) )
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_SPAWN.id(), SymbolMessage(symbolInstance =
                    SymbolInstance(symbolObj = targetSymbol.symbolObj, cycles = targetSymbol.cycles - targetSymbol.handleCapacity, position = targetSymbol.position).apply { this.consumeCapacity = targetSymbol.consumeCapacity; this.handleCapacity = targetSymbol.handleCapacity; this.displayType = SymbolDisplayType.PRESENT}
            ) )
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_SPAWN.id(), SymbolMessage(symbolInstance =
                    SymbolInstance(symbolObj = targetSymbol.symbolObj, cycles = targetSymbol.handleCapacity, position = SymbolTargetPosition.STABILIZE_POSSESSION.targetPosition()).apply { this.consumeCapacity = targetSymbol.consumeCapacity; this.handleCapacity = targetSymbol.handleCapacity; this.displayType = SymbolDisplayType.PRESENT}
            ) )
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_DESPAWN.id(), SymbolMessage(targetSymbolInstance = targetSymbol.apply { this.displayType = SymbolDisplayType.PRESENT }))
        } else {
            val deltaPosition = targetSymbol.position - SymbolTargetPosition.STABILIZE_POSSESSION.targetPosition()
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_MODIFY.id(), SymbolMessage(targetSymbolInstance = targetSymbol.apply { this.deltaCycles = 0f; this.deltaPosition = -deltaPosition}) )
        }
    }
}