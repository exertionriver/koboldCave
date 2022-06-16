package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolTargetPosition
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

object ReadyFocus : IInternalFocus {

    override var tag = "ready"

    override var satisfyingStrategies = mutableListOf<IInternalFocus>()
    override fun satisfyingCondition(targetPresentSymbol : SymbolInstance) = PossessFocus.satisfyingCondition(targetPresentSymbol)
    override fun satisfyingResult(entity: Telegraph, targetPresentSymbol : SymbolInstance) {
        val deltaPosition = targetPresentSymbol.position - SymbolTargetPosition.STABILIZE_HANDLING.targetPosition()

        SymbolModifyAction.executeImmediate(entity, SymbolMessage(symbolInstance = targetPresentSymbol.apply { this.deltaPosition = -deltaPosition }))

//        MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_MODIFY.id(), SymbolMessage(symbolInstance = targetPresentSymbol.apply { this.deltaPosition = -deltaPosition} ) )
    }


}