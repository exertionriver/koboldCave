package org.river.exertion.ai.internalSymbol.core.symbolAction

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

class SymbolDespawnAction(var targetSymbol : IPerceivedSymbol
                          , var targetDisplayType: SymbolDisplayType
                          , var thresholdType: SymbolThresholdType
                          , var thresholdPosition : Float) : ISymbolAction {

    override var symbolActionType: SymbolActionType = SymbolActionType.DESPAWN

    override fun execute(entity: Telegraph, sourceSymbolInstance: SymbolInstance, sourceDisplayType: SymbolDisplayType) {
        if ((thresholdType == SymbolThresholdType.LESS_THAN && sourceSymbolInstance.position < thresholdPosition) ||
            (thresholdType == SymbolThresholdType.GREATER_THAN && sourceSymbolInstance.position > thresholdPosition))
                executeImmediate(entity, targetSymbol, targetDisplayType)
    }

    companion object {
        fun executeImmediate(entity: Telegraph, targetSymbol: IPerceivedSymbol, targetDisplayType: SymbolDisplayType) {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_DESPAWN.id(), SymbolMessage(targetSymbol.spawn(), targetDisplayType))
        }
    }
}