package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.InternalFocusType
import org.river.exertion.ai.internalFocus.internalFocuses.proximity.CloseIntimateFocus
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolTargetPosition
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

object PickUpFocus : IInternalFocus {

    override var tag = "pick up"
    override var type = InternalFocusType.ACTION
    override var momentMinimum = 1f

    override var satisfyingStrategies = mutableListOf<IInternalFocus>()
    override fun satisfyingCondition(targetPresentSymbol : SymbolInstance) = CloseIntimateFocus.satisfyingCondition(targetPresentSymbol)
    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {

        if (targetPresentSymbol.cycles <= targetPresentSymbol.handleCapacity) {
            val deltaPosition = targetPresentSymbol.position - SymbolTargetPosition.STABILIZE_POSSESSION.targetPosition()

            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_MODIFY.id(), SymbolMessage(symbolInstance = targetPresentSymbol.apply { this.deltaCycles = 0f; this.deltaPosition = -deltaPosition}) )
        } else {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_SPAWN.id(), SymbolMessage(symbolInstance =
            SymbolInstance(symbolObj = targetPresentSymbol.symbolObj, cycles = targetPresentSymbol.cycles - targetPresentSymbol.handleCapacity, position = targetPresentSymbol.position).apply { this.consumeCapacity = targetPresentSymbol.consumeCapacity; this.handleCapacity = targetPresentSymbol.handleCapacity}
            ) )
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_SPAWN.id(), SymbolMessage(symbolInstance =
            SymbolInstance(symbolObj = targetPresentSymbol.symbolObj, cycles = targetPresentSymbol.handleCapacity, position = SymbolTargetPosition.STABILIZE_POSSESSION.targetPosition()).apply { this.consumeCapacity = targetPresentSymbol.consumeCapacity; this.handleCapacity = targetPresentSymbol.handleCapacity}
            ) )
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_DESPAWN.id(), SymbolMessage(symbolInstance = targetPresentSymbol))
        }
    }
}