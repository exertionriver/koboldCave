package org.river.exertion.ai.internalFocus

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalFocus.internalFocuses.NoneFocus
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolActionMessage
import org.river.exertion.ai.messaging.SymbolMessage

class InternalFocusDisplay(val entity : Telegraph) : Telegraph {

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.INT_ADD_FOCUS_PLAN.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_REMOVE_FOCUS_PLAN.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_ADD_FOCUS_CHAIN_LINK.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_REMOVE_FOCUS_CHAIN_LINK.id())
    }

    var focusPlansPresent = mutableSetOf<InternalFocusPlan>()

    @Suppress("NewApi")
    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_ADD_FOCUS_PLAN.id()) {
                val symbolMessage = msg.extraInfo as SymbolMessage
                //todo: correct for FocusMessage
                focusPlansPresent.add(InternalFocusPlan(symbolMessage.symbolInstance, NoneFocus))
            }
            if (msg.message == MessageChannel.INT_REMOVE_FOCUS_PLAN.id()) {
                val symbolMessage = msg.extraInfo as SymbolMessage
                focusPlansPresent.removeIf { it.absentSymbolInstance == symbolMessage.symbolInstance }
            }
            if (msg.message == MessageChannel.INT_ADD_FOCUS_CHAIN_LINK.id()) {
                val symbolMessage = msg.extraInfo as SymbolMessage
                focusPlansPresent.first { it.absentSymbolInstance == symbolMessage.symbolInstance }.addLink()
            }
            if (msg.message == MessageChannel.INT_REMOVE_FOCUS_CHAIN_LINK.id()) {
                val symbolMessage = msg.extraInfo as SymbolMessage
                focusPlansPresent.first { it.absentSymbolInstance == symbolMessage.symbolInstance }.instancesChain.removeAt(symbolMessage.symbolInstance.deltaPosition.toInt())
            }
        }
        return true
    }
}