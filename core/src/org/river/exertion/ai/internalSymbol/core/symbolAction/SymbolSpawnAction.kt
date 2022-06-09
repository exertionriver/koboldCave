package org.river.exertion.ai.internalSymbol.core.symbolAction

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage
import org.river.exertion.logDebug

class SymbolSpawnAction(var targetSymbol : IPerceivedSymbol
                        , var thresholdPosition : Float
                        , var thresholdType: SymbolThresholdType
                        , var targetDisplayType: SymbolDisplayType = SymbolDisplayType.PRESENT) : ISymbolAction {

    override var symbolActionType: SymbolActionType = SymbolActionType.SPAWN

    override fun execute(entity: Telegraph, symbolMessage: SymbolMessage) {
//        logDebug("symbolAction", "executing SymbolSpawnAction")

        if (symbolMessage.symbolInstance != null
                && symbolMessage.targetSymbolInstance != null
                && symbolMessage.targetSymbolInstance!!.symbolObj == targetSymbol
                && symbolMessage.targetSymbolDisplayType != null
                && symbolMessage.targetSymbolDisplayType!! == targetDisplayType) {

            if ((thresholdType == SymbolThresholdType.LESS_THAN && symbolMessage.symbolInstance!!.position < thresholdPosition) ||
                    (thresholdType == SymbolThresholdType.GREATER_THAN && symbolMessage.symbolInstance!!.position > thresholdPosition))
                executeImmediate(entity, symbolMessage)
        }
    }

    companion object {
        fun executeImmediate(entity: Telegraph, symbolMessage: SymbolMessage) {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_SPAWN.id(), symbolMessage)
        }
    }
}