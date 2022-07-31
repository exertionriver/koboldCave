package org.river.exertion.ai.internalSymbol.core.symbolAction

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.IPerceivedSymbol
import org.river.exertion.ai.internalSymbol.core.SymbolActionType
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolThresholdType
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

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
                    MessageChannel.INT_SYMBOL_SPAWN.send(entity, SymbolMessage().apply { this.symbol = targetSymbol; this.symbolDisplayType = targetDisplayType })
                else
                    MessageChannel.INT_SYMBOL_SPAWN.send(entity, SymbolMessage().apply { this.symbolInstance = targetSymbol.spawn().apply { this.displayType = targetDisplayType; this.position = symbolMessage.symbolInstance!!.position - 1f } })
            }
        }
    }
}