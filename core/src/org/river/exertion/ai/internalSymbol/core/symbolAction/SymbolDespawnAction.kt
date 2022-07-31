package org.river.exertion.ai.internalSymbol.core.symbolAction

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.IPerceivedSymbol
import org.river.exertion.ai.internalSymbol.core.SymbolActionType
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolThresholdType
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

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
                MessageChannel.INT_SYMBOL_DESPAWN.send(entity, symbolMessage.apply { this.symbol = targetSymbol; this.symbolInstance = null; this.symbolDisplayType = targetDisplayType })
            }
        }
    }
}