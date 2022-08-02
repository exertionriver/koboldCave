package org.river.exertion.ai.internalFocus

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.InternalSymbolDisplay
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.messaging.FocusMessage
import org.river.exertion.ai.messaging.MessageChannel

@Suppress("NewApi")
class InternalFocusDisplay(val entity : Telegraph) : Telegraph {

    init {
        MessageChannel.INT_ADD_FOCUS_PLAN.enableReceive(this)
        MessageChannel.INT_REMOVE_FOCUS_PLAN.enableReceive(this)
    }

    var focusPlans = mutableSetOf<InternalFocusPlan>()

    fun rebuildPlans(internalSymbolDisplay: InternalSymbolDisplay, momentDelta : Float) {

        focusPlans.forEach {

            if (!internalSymbolDisplay.symbolDisplay.contains(it.absentSymbolInstance))
                removePlan(it.satisfierFocus, it.absentSymbolInstance)

            if (it.instancesChain.isEmpty()) it.seedChain()

            it.processChain(internalSymbolDisplay, momentDelta)
        }
    }

    fun addPlanHandler(focusMessage : FocusMessage) {
        if (focusMessage.satisfierFocus != null && focusMessage.absentSymbolInstance != null)
            addPlan(focusMessage.satisfierFocus!!, focusMessage.absentSymbolInstance!!)
    }

    fun addPlan(satisfierFocus : IInternalFocus, absentSymbolInstance : SymbolInstance) {
        if (focusPlans.none {it.absentSymbolInstance == absentSymbolInstance && it.satisfierFocus == satisfierFocus} ) {
            val addedPlan = InternalFocusPlan(entity, satisfierFocus, absentSymbolInstance)
            focusPlans.add(addedPlan)
        }
    }

    fun removePlanHandler(focusMessage : FocusMessage) {
        if (focusMessage.absentSymbolInstance != null)
            if (focusMessage.satisfierFocus != null)
                removePlan(focusMessage.satisfierFocus!!, focusMessage.absentSymbolInstance!!)
            else
                removePlan(focusMessage.absentSymbolInstance!!)
   }

    fun removePlan(satisfierFocus : IInternalFocus, absentSymbolInstance : SymbolInstance) {
        focusPlans.removeIf { it.satisfierFocus == satisfierFocus && it.absentSymbolInstance == absentSymbolInstance }
    }

    fun removePlan(absentSymbolInstance : SymbolInstance) {
        focusPlans.removeIf { it.absentSymbolInstance == absentSymbolInstance }
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_ADD_FOCUS_PLAN.id()) {
                this.addPlanHandler(MessageChannel.INT_ADD_FOCUS_PLAN.receiveMessage(msg.extraInfo))
            }
            if (msg.message == MessageChannel.INT_REMOVE_FOCUS_PLAN.id()) {
                this.removePlanHandler(MessageChannel.INT_REMOVE_FOCUS_PLAN.receiveMessage(msg.extraInfo))
            }
        }
        return true
    }
}