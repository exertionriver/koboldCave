package org.river.exertion.ai.internalSymbol.core.symbolAction

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

class SymbolSpawnInstanceAction(var targetSymbol : IPerceivedSymbol
                            , var targetDisplayType: SymbolDisplayType
                            , var thresholdType: SymbolThresholdType = SymbolThresholdType.NONE
                            , var thresholdPosition : Float = 0f) : ISymbolAction {

    override var symbolActionType: SymbolActionType = SymbolActionType.SPAWN_INSTANCE

    override fun execute(entity: Telegraph, sourceSymbolInstance: SymbolInstance, sourceDisplayType: SymbolDisplayType) {
        if ((thresholdType == SymbolThresholdType.LESS_THAN && sourceSymbolInstance.position < thresholdPosition) ||
            (thresholdType == SymbolThresholdType.GREATER_THAN && sourceSymbolInstance.position > thresholdPosition))
                executeImmediate(entity, targetSymbol, targetDisplayType)
    }

    companion object {
        fun executeImmediate(entity: Telegraph, targetSymbol: IPerceivedSymbol, targetDisplayType: SymbolDisplayType) {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_SPAWN_INSTANCE.id(), SymbolMessage(targetSymbol.spawn(), targetDisplayType))
        }
    }
}