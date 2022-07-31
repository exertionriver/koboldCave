package org.river.exertion.ai.internalFocus

import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.InternalSymbolDisplay
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.messaging.FocusMessage
import org.river.exertion.ai.messaging.MessageChannel

class InternalFocusPlan(var entity : Telegraph, var satisfierFocus: IInternalFocus, var absentSymbolInstance: SymbolInstance) : Telegraph {

    init {
        MessageChannel.INT_ADD_FOCUS_CHAIN_LINK.enableReceive(this )
        MessageChannel.INT_REMOVE_FOCUS_CHAIN_LINK.enableReceive(this)
    }

    var instancesChain = mutableListOf<InternalFocusInstance>()
    var satisied = false

    fun closestPresentSymbol(internalSymbolDisplay: InternalSymbolDisplay) : SymbolInstance {
        val presentSymbols : MutableList<SymbolInstance> = internalSymbolDisplay.symbolDisplay.filter { it.symbolObj == absentSymbolInstance.symbolObj && it.displayType == SymbolDisplayType.PRESENT }.sortedBy { it.position }.toMutableList()

        if (presentSymbols.isEmpty()) presentSymbols.add( SymbolInstance(absentSymbolInstance.symbolObj, position = 1.01f) )

        //currently closest position
        return presentSymbols.first()
    }

    fun seedChain() {

        this.instancesChain.add(satisfierFocus.instantiate())
    }

    fun processChain(internalSymbolDisplay: InternalSymbolDisplay, momentDelta : Float) {

        var lastTrue = false

        this.instancesChain.reversed().forEach {
            internalSymbolDisplay.circularity.clear()

            if (!lastTrue)
                lastTrue = it.internalFocusObj.evaluate(entity, FocusMessage(satisfierFocus = this.satisfierFocus, absentSymbolInstance = this.absentSymbolInstance, presentSymbolInstance = closestPresentSymbol(internalSymbolDisplay), chainStrategyInstance = it), momentDelta)}
    }

    fun addLink(focusMessage: FocusMessage) {

        if ( (focusMessage.satisfierFocus != null) && (focusMessage.satisfierFocus == this.satisfierFocus)
                && (focusMessage.absentSymbolInstance != null) && (focusMessage.absentSymbolInstance == this.absentSymbolInstance)
                && (focusMessage.chainStrategyInstance != null) && !this.instancesChain.map { it.internalFocusObj }.contains(focusMessage.chainStrategyInstance!!.internalFocusObj)) {
            this.instancesChain.add(focusMessage.chainStrategyInstance!!)
        }
    }

    fun removeLink(focusMessage: FocusMessage) {
        if ( (focusMessage.satisfierFocus != null) && (focusMessage.satisfierFocus == this.satisfierFocus)
                && (focusMessage.absentSymbolInstance != null) && (focusMessage.absentSymbolInstance == this.absentSymbolInstance)
                && (focusMessage.chainStrategyInstance != null) && (this.instancesChain.last().internalFocusObj == focusMessage.chainStrategyInstance!!.internalFocusObj)) {
            this.instancesChain.removeLast()
        }
    }

    fun updateMomentCounter(focusMessage: FocusMessage) {
        if ( (focusMessage.satisfierFocus != null) && (focusMessage.satisfierFocus == this.satisfierFocus)
                && (focusMessage.absentSymbolInstance != null) && (focusMessage.absentSymbolInstance == this.absentSymbolInstance)
                && (focusMessage.chainStrategyInstance != null) && (this.instancesChain.last().internalFocusObj == focusMessage.chainStrategyInstance!!.internalFocusObj)) {
            this.instancesChain.removeLast()
        }
    }

    override fun handleMessage(msg: Telegram?): Boolean {
        if ( (msg != null) && (msg.sender == entity) ) {
            if (msg.message == MessageChannel.INT_ADD_FOCUS_CHAIN_LINK.id()) {
                this.addLink(msg.extraInfo as FocusMessage)
            }
            if (msg.message == MessageChannel.INT_REMOVE_FOCUS_CHAIN_LINK.id()) {
                this.removeLink(msg.extraInfo as FocusMessage)
            }
        }
        return true
    }

}