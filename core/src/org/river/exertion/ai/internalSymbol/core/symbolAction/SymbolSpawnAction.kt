package org.river.exertion.ai.internalSymbol.core.symbolAction

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage
import org.river.exertion.ecs.entity.IEntity
import org.river.exertion.logDebug

class SymbolSpawnAction(var targetSymbol : IPerceivedSymbol
                        , var thresholdPosition : Float
                        , var thresholdType: SymbolThresholdType
                        , var targetDisplayType: SymbolDisplayType = SymbolDisplayType.PRESENT) : ISymbolAction {

    override var symbolActionType: SymbolActionType = SymbolActionType.SPAWN

    override fun execute(entity: Telegraph, symbolMessage: SymbolMessage) {

        //symbolInstance is the spawning symbol
        if (symbolMessage.symbolInstance != null) {
            if ((thresholdType == SymbolThresholdType.LESS_THAN && symbolMessage.symbolInstance!!.position < thresholdPosition) ||
                (thresholdType == SymbolThresholdType.GREATER_THAN && symbolMessage.symbolInstance!!.position > thresholdPosition)) {
                //set parameters to spawn
//                logDebug("symbolAction", "${(entity as IEntity).entityName} executing SymbolSpawnAction $targetSymbol, $targetDisplayType")

                if (targetDisplayType == SymbolDisplayType.PRESENT)
                    executeImmediate(entity, SymbolMessage().apply { this.symbol = targetSymbol; this.symbolDisplayType = targetDisplayType })
                else
                    executeImmediate(entity, SymbolMessage().apply { this.symbolInstance = targetSymbol.spawn().apply { this.displayType = targetDisplayType; this.position = symbolMessage.symbolInstance!!.position - 1f } })
            }
        }
    }

    companion object {
        fun executeImmediate(entity: Telegraph, symbolMessage: SymbolMessage) {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_SPAWN.id(), symbolMessage)
        }
    }
}