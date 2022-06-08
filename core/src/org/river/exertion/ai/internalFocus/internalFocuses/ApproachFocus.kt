package org.river.exertion.ai.internalFocus.internalFocuses

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolActionMessage
import org.river.exertion.ai.messaging.SymbolMessage

object ApproachFocus : IInternalFocus {

    override var tag = "approach"
    override var dependsUpon = mutableSetOf<IInternalFocus>(
    )
    override var satisfyingStrategies = mutableSetOf<IInternalFocus>()

    override fun satisfyingCondition(targetSymbol : SymbolInstance) = true

    override fun satisfyingResult(entity: Telegraph, targetSymbol : SymbolInstance) {
        MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_MODIFY_INSTANCE.id(), SymbolMessage(targetSymbol.apply { this.deltaPosition = -0.05f}, SymbolDisplayType.PRESENT))
    }
}