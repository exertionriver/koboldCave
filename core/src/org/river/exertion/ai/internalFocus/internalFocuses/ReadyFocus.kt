package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.internalSymbol.core.SymbolTargetPosition
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolActionMessage
import org.river.exertion.ai.messaging.SymbolMessage

object ReadyFocus : IInternalFocus {

    override var tag = "ready"
    override var dependsUpon = mutableSetOf<IInternalFocus>()
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>()
    override fun satisfyingCondition(targetSymbol : SymbolInstance) = PossessFocus.satisfyingCondition(targetSymbol)
    override fun satisfyingResult(entity: Telegraph, targetSymbol : SymbolInstance) {
        val deltaPosition = targetSymbol.position - SymbolTargetPosition.STABILIZE_HANDLING.targetPosition()

        MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_MODIFY_INSTANCE.id(), SymbolMessage(targetSymbol.apply { this.deltaPosition = -deltaPosition}, SymbolDisplayType.PRESENT) )
    }
}