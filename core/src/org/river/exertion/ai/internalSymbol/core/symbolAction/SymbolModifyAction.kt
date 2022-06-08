package org.river.exertion.ai.internalSymbol.core.symbolAction

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.*
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

class SymbolModifyAction(var sourceSymbol : IPerceivedSymbol
                         , var targetSymbol : IPerceivedSymbol
                         , var targetDisplayType: SymbolDisplayType
                         , var modifierType: SymbolModifierType
                         , var sourceToTargetRatio : Float = 0f) : ISymbolAction {

    override var symbolActionType: SymbolActionType = SymbolActionType.MODIFY

    lateinit var targetSymbolInstance : SymbolInstance

    /*
    SymbolModifyAction(FoodSymbol, HungerSymbol, SymbolDisplayType.PRESENT, SymbolModifierType.CYCLE_TO_POSITION, .1f),
    SymbolModifyAction(MomentElapseSymbol, HungerSymbol, SymbolDisplayType.PRESENT, SymbolModifierType.POSITION_TO_POSITION, -.001f),
    SymbolModifyAction(HungerSymbol, FoodSymbol, SymbolDisplayType.ABSENT, SymbolModifierType.POSITION_TO_POSITION, -.1f, 1f)
    */

    override fun execute(entity : Telegraph, sourceSymbolInstance : SymbolInstance, sourceDisplayType: SymbolDisplayType) {
        if (sourceSymbolInstance.symbolObj == sourceSymbol && targetSymbolInstance.symbolObj == targetSymbol) {
            targetSymbolInstance.updateModifiedPosition(sourceSymbolInstance, modifierType, sourceToTargetRatio)
            executeImmediate(entity, targetSymbolInstance, targetDisplayType)
        }
    }

    companion object {

        fun executeImmediate(entity: Telegraph, targetSymbolInstance: SymbolInstance, targetDisplayType: SymbolDisplayType) {
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_MODIFIED_INSTANCE.id(), SymbolMessage(targetSymbolInstance, targetDisplayType))
            targetSymbolInstance.normalizePosition()
            MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_SYMBOL_MODIFY_INSTANCE.id(), SymbolMessage(targetSymbolInstance, targetDisplayType))
        }
    }
}