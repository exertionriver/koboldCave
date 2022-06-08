package org.river.exertion.ai.internalSymbol.core

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolActionMessage

class InternalSymbolDisplay(val entity : Telegraph) {

    var symbolsPresent = SymbolDisplayInstance(entity, SymbolDisplayType.PRESENT)
    var symbolsAbsent = SymbolDisplayInstance(entity, SymbolDisplayType.ABSENT)

/*    @Suppress("NewApi")
    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_PRESENT_SYMBOL_MODIFY.id()) {
                val symbolMessage = msg.extraInfo as SymbolActionMessage
                symbolsPresent.firstOrNull { it == symbolMessage.symbolInstance }?.updatePosition(entity, symbolMessage.deltaCycles, symbolMessage.deltaPosition)
            }
            if (msg.message == MessageChannel.INT_PRESENT_SYMBOL_MODIFY_ALL.id()) {
                val symbolMessage = msg.extraInfo as SymbolActionMessage
                symbolsPresent.filter { it.symbolObj == symbolMessage.symbolInstance.symbolObj }.forEach { it.updatePosition(entity, symbolMessage.deltaCycles, symbolMessage.deltaPosition) }
            }
            if (msg.message == MessageChannel.INT_PRESENT_SYMBOL_MODIFIED.id()) {
                val symbolMessage = msg.extraInfo as SymbolActionMessage
                symbolsPresent.filter { it.symbolObj.presentModifiers.map { modifier -> modifier.modifyingSymbol }.contains(symbolMessage.symbolInstance.symbolObj) }.forEach { it.updateModifiedPosition(entity, symbolMessage.symbolInstance, symbolMessage.deltaCycles, symbolMessage.deltaPosition) }
            }
            if (msg.message == MessageChannel.INT_ABSENT_SYMBOL_MODIFY.id()) {
                val symbolMessage = msg.extraInfo as SymbolActionMessage
                symbolsAbsent.firstOrNull { it == symbolMessage.symbolInstance }?.updateModifiedPosition(entity, symbolMessage.modifyingSymbolInstance!!, symbolMessage.deltaCycles, symbolMessage.deltaPosition)
            }
            if (msg.message == MessageChannel.INT_PRESENT_SYMBOL_SPAWN.id()) {
                val symbolMessage = msg.extraInfo as SymbolActionMessage
                symbolsPresent.add(symbolMessage.symbolInstance)
            }
            if (msg.message == MessageChannel.INT_ABSENT_SYMBOL_SPAWN.id()) {
                val symbolMessage = msg.extraInfo as SymbolActionMessage
                if (!symbolsAbsent.map { it.symbolObj }.contains(symbolMessage.symbolInstance.symbolObj) )
                    symbolsAbsent.add(symbolMessage.symbolInstance)
            }
            if (msg.message == MessageChannel.INT_PRESENT_SYMBOL_DESPAWN.id()) {
                val symbolMessage = msg.extraInfo as SymbolActionMessage
                symbolsPresent.removeIf { it == symbolMessage.symbolInstance }
            }
            if (msg.message == MessageChannel.INT_ABSENT_SYMBOL_DESPAWN.id()) {
                val symbolMessage = msg.extraInfo as SymbolActionMessage
                symbolsAbsent.removeIf { it == symbolMessage.symbolInstance }
            }
        }
        return true
    }*/
}