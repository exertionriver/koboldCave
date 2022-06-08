package org.river.exertion.ai.internalSymbol.core

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.symbolAction.SymbolModifyAction
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage

data class SymbolDisplayInstance(val entity : Telegraph, val symbolDisplayType : SymbolDisplayType) : Telegraph {

    var symbolDisplay = mutableSetOf<SymbolInstance>()

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.INT_SYMBOL_SPAWN_INSTANCE.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_SYMBOL_DESPAWN_INSTANCE.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_SYMBOL_DESPAWN.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_SYMBOL_MODIFY_INSTANCE.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_SYMBOL_MODIFIED_INSTANCE.id())
    }

    @Suppress("NewApi")
    fun despawn(symbol : IPerceivedSymbol) {
        symbolDisplay.removeIf { it.symbolObj == symbol }
    }

    @Suppress("NewApi")
    fun despawn(symbolInstance : SymbolInstance) {
        symbolDisplay.removeIf { it == symbolInstance }
    }

    fun spawn(symbolInstance : SymbolInstance) {
        symbolDisplay.add(symbolInstance)
    }

    fun update(symbolInstance : SymbolInstance) {
        symbolDisplay.filter { it == symbolInstance }.firstOrNull().apply {
            this?.cycles = symbolInstance.cycles
            this?.position = symbolInstance.position
            this?.consumeCapacity = symbolInstance.consumeCapacity
            this?.handleCapacity = symbolInstance.handleCapacity
            this?.possessCapacity = symbolInstance.possessCapacity
        }
    }

    //to do: check for circularity
    fun propagateUpdate(symbolInstance : SymbolInstance, symbolDisplayType: SymbolDisplayType) {
        symbolDisplay.flatMap { it.symbolObj.symbolActions }.filter { it.symbolActionType == SymbolActionType.MODIFY && (it as SymbolModifyAction).sourceSymbol == symbolInstance.symbolObj }.forEach {
            it.execute(entity, symbolInstance, symbolDisplayType)
        }
    }

    @Suppress("NewApi")
    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_SYMBOL_SPAWN_INSTANCE.id()) {
                val symbolMessage = msg.extraInfo as SymbolMessage
                if (symbolDisplayType == symbolMessage.symbolDisplayType)
                    this.spawn(symbolMessage.symbolInstance)
            }
            if (msg.message == MessageChannel.INT_SYMBOL_DESPAWN_INSTANCE.id()) {
                val symbolMessage = msg.extraInfo as SymbolMessage
                if (symbolDisplayType == symbolMessage.symbolDisplayType)
                    this.despawn(symbolMessage.symbolInstance)
            }
            if (msg.message == MessageChannel.INT_SYMBOL_DESPAWN.id()) {
                val symbolMessage = msg.extraInfo as SymbolMessage
                if (symbolDisplayType == symbolMessage.symbolDisplayType)
                    this.despawn(symbolMessage.symbolInstance.symbolObj)
            }
            if (msg.message == MessageChannel.INT_SYMBOL_MODIFY_INSTANCE.id()) {
                val symbolMessage = msg.extraInfo as SymbolMessage
                if (symbolDisplayType == symbolMessage.symbolDisplayType)
                    this.update(symbolMessage.symbolInstance)
            }
            if (msg.message == MessageChannel.INT_SYMBOL_MODIFIED_INSTANCE.id()) {
                val symbolMessage = msg.extraInfo as SymbolMessage
                if (symbolDisplayType == symbolMessage.symbolDisplayType)
                    this.propagateUpdate(symbolMessage.symbolInstance, symbolMessage.symbolDisplayType)
            }
        }
        return true
    }
}

