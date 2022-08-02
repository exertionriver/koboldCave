package org.river.exertion.ai.internalSymbol.core.symbolAction

import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.IPerceivedSymbol
import org.river.exertion.ai.internalSymbol.core.SymbolActionType
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolModifierType
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

class SymbolModifyAction(var sourceSymbol : IPerceivedSymbol
                         , var targetSymbol : IPerceivedSymbol
                         , var sourceToTargetRatio : Float
                         , var modifierType: SymbolModifierType
                         , var targetDisplayType: SymbolDisplayType = SymbolDisplayType.PRESENT) : ISymbolAction {

    override var symbolActionType: SymbolActionType = SymbolActionType.MODIFY

    override fun execute(entity : Telegraph, symbolMessage: SymbolMessage) {

        if ( symbolMessage.symbolInstance != null
                && symbolMessage.symbolInstance!!.symbolObj == sourceSymbol
                && symbolMessage.targetSymbolInstance != null
                && symbolMessage.targetSymbolInstance!!.symbolObj == targetSymbol
                && symbolMessage.targetSymbolDisplayType != null
                && symbolMessage.targetSymbolDisplayType!! == targetDisplayType
            ) {
//            logDebug("symbolAction", "${(entity as IEntity).entityName} executing SymbolModifyAction ${symbolMessage.symbolInstance!!.position}")

            val updateSymbolInstance = symbolMessage.targetSymbolInstance!!

            updateSymbolInstance.displayType = targetDisplayType

            updateSymbolInstance.setDeltas(symbolMessage.symbolInstance!!, modifierType, sourceToTargetRatio)

            MessageChannel.INT_SYMBOL_MODIFY.send(entity, SymbolMessage(symbolInstance = updateSymbolInstance ))
        }
    }
}