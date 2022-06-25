package org.river.exertion.ai.internalFocus

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.InternalSymbolDisplay
import org.river.exertion.ai.messaging.FocusMessage
import org.river.exertion.ai.messaging.MessageChannel

class InternalFocusDisplay(val entity : Telegraph) : Telegraph {

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.INT_ADD_FOCUS_PLAN.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_REMOVE_FOCUS_PLAN.id())
    }

    var focusPlans = mutableSetOf<InternalFocusPlan>()

    fun rebuildPlans(internalSymbolDisplay: InternalSymbolDisplay, momentDelta : Float) {
//        sortedByDescending { it.absentSymbolInstance.impact }
        focusPlans.forEach {

            if (!internalSymbolDisplay.symbolDisplay.contains(it.absentSymbolInstance))
                removePlan(FocusMessage(it.satisfierFocus, it.absentSymbolInstance))

            if (it.instancesChain.isEmpty()) it.seedChain()

            it.processChain(internalSymbolDisplay, momentDelta)
        }
    }

    fun addPlan(focusMessage : FocusMessage) {
        if ( (focusMessage.satisfierFocus != null && focusMessage.absentSymbolInstance != null)
                    && (focusPlans.none {it.absentSymbolInstance == focusMessage.absentSymbolInstance && it.satisfierFocus == focusMessage.satisfierFocus} ) ) {
            val addedPlan = InternalFocusPlan(entity, focusMessage.satisfierFocus!!, focusMessage.absentSymbolInstance!!)
            focusPlans.add(addedPlan)
        }
    }

    @Suppress("NewApi")
    fun removePlan(focusMessage : FocusMessage) {
        if (focusMessage.absentSymbolInstance != null)
            if (focusMessage.satisfierFocus != null)
                focusPlans.removeIf { it.satisfierFocus == focusMessage.satisfierFocus!! && it.absentSymbolInstance == focusMessage.absentSymbolInstance!! }
            else
                focusPlans.removeIf { it.absentSymbolInstance == focusMessage.absentSymbolInstance!! }
   }

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_ADD_FOCUS_PLAN.id()) {
                this.addPlan(msg.extraInfo as FocusMessage)
            }
            if (msg.message == MessageChannel.INT_REMOVE_FOCUS_PLAN.id()) {
                this.removePlan(msg.extraInfo as FocusMessage)
            }
        }
        return true
    }
}