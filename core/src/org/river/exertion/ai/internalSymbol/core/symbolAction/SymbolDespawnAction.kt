package org.river.exertion.ai.internalSymbol.core.symbolAction

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.logDebug

class SymbolDespawnAction(var targetSymbol : IPerceivedSymbol
                          , var thresholdPosition : Float
                          , var thresholdType: SymbolThresholdType
                          , var targetDisplayType: SymbolDisplayType = SymbolDisplayType.PRESENT) : ISymbolAction {

    override var symbolActionType: SymbolActionType = SymbolActionType.DESPAWN

    override fun execute(entity: Telegraph, symbolMessage: SymbolMessage) {

        //symbolInstance is the spawning symbol
        if (symbolMessage.symbolInstance != null) {
            if ((thresholdType == SymbolThresholdType.LESS_THAN && symbolMessage.symbolInstance!!.position < thresholdPosition) ||
                (thresholdType == SymbolThresholdType.GREATER_THAN && symbolMessage.symbolInstance!!.position > thresholdPosition)) {
                //set parameters to despawn
//                logDebug("symbolAction", "${(entity as IEntity).entityName} executing SymbolDespawnAction $targetSymbol, $targetDisplayType")
                executeImmediate(entity, symbolMessage.apply { this.symbol = targetSymbol; this.symbolInstance = null; this.symbolDisplayType = targetDisplayType })
            }
        }
    }

    companion object {
        fun executeImmediate(entity: Telegraph, symbolMessage: SymbolMessage) {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_DESPAWN.id(), symbolMessage)
        }
    }
}