package org.river.exertion.ai.internalSymbol.core.symbolAction

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage
import org.river.exertion.logDebug

class SymbolModifyAction(var sourceSymbol : IPerceivedSymbol
                         , var targetSymbol : IPerceivedSymbol
                         , var sourceToTargetRatio : Float
                         , var modifierType: SymbolModifierType
                         , var targetDisplayType: SymbolDisplayType = SymbolDisplayType.PRESENT) : ISymbolAction {

    override var symbolActionType: SymbolActionType = SymbolActionType.MODIFY

    override fun execute(entity : Telegraph, symbolMessage: SymbolMessage) {
//        logDebug("symbolAction", "executing SymbolModifyAction ${symbolMessage.symbolInstance!!.position}")

        if ( symbolMessage.symbolInstance != null
                && symbolMessage.symbolInstance!!.symbolObj == sourceSymbol
                && symbolMessage.targetSymbolInstance != null
                && symbolMessage.targetSymbolInstance!!.symbolObj == targetSymbol
                && symbolMessage.targetSymbolDisplayType != null
                && symbolMessage.targetSymbolDisplayType!! == targetDisplayType
            ) {
                symbolMessage.targetSymbolInstance!!.updateModifiedPosition(symbolMessage.symbolInstance!!, modifierType, sourceToTargetRatio)
                executeImmediate(entity, SymbolMessage(symbolInstance = symbolMessage.targetSymbolInstance, symbolDisplayType = symbolMessage.targetSymbolDisplayType))
        }
    }

    companion object {

        fun executeImmediate(entity: Telegraph, symbolMessage: SymbolMessage) {
            if (symbolMessage.symbolInstance != null) {
                MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_MODIFIED.id(), symbolMessage)
                symbolMessage.symbolInstance!!.normalizePosition()
                MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_MODIFY.id(), symbolMessage)
            }
        }
    }
}