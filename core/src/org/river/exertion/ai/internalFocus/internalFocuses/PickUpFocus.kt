package org.river.exertion.ai.internalFocus.internalFocuses

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
    override var momentMinimum = 4f

    override var satisfyingStrategies = mutableListOf<IInternalFocus>(
        CloseIntimateFocus
    )
    override fun satisfyingCondition(targetPresentSymbol : SymbolInstance) = CloseIntimateFocus.satisfyingCondition(targetPresentSymbol)
    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {

        if (targetPresentSymbol.cycles <= targetPresentSymbol.handleCapacity) {
            val deltaPosition = targetPresentSymbol.position - SymbolTargetPosition.STABILIZE_POSSESSION.targetPosition()

            MessageChannel.INT_SYMBOL_MODIFY.send(entity, SymbolMessage(symbolInstance = targetPresentSymbol.apply { this.deltaCycles = 0f; this.deltaPosition = -deltaPosition}) )
        } else {
            MessageChannel.INT_SYMBOL_SPAWN.send(entity, SymbolMessage(symbolInstance =
                SymbolInstance(symbolObj = targetPresentSymbol.symbolObj, cycles = targetPresentSymbol.cycles - targetPresentSymbol.handleCapacity, position = targetPresentSymbol.position).apply { this.consumeCapacity = targetPresentSymbol.consumeCapacity; this.handleCapacity = targetPresentSymbol.handleCapacity}
            ) )
            MessageChannel.INT_SYMBOL_SPAWN.send(entity, SymbolMessage(symbolInstance =
                SymbolInstance(symbolObj = targetPresentSymbol.symbolObj, cycles = targetPresentSymbol.handleCapacity, position = SymbolTargetPosition.STABILIZE_POSSESSION.targetPosition()).apply { this.consumeCapacity = targetPresentSymbol.consumeCapacity; this.handleCapacity = targetPresentSymbol.handleCapacity}
            ) )
            MessageChannel.INT_SYMBOL_DESPAWN.send(entity, SymbolMessage(symbolInstance = targetPresentSymbol))
        }
    }
}