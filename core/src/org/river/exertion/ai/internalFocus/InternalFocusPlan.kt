package org.river.exertion.ai.internalFocus

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.InternalSymbolDisplay
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.messaging.FocusMessage
import org.river.exertion.ai.messaging.MessageChannel

class InternalFocusPlan(var entity : Telegraph, var satisfierFocus: IInternalFocus, var absentSymbolInstance: SymbolInstance) : Telegraph {

    init {
        MessageManager.getInstance().addListener(this, MessageChannel.INT_ADD_FOCUS_CHAIN_LINK.id())
        MessageManager.getInstance().addListener(this, MessageChannel.INT_REMOVE_FOCUS_CHAIN_LINK.id())
    }

    var instancesChain = mutableListOf<IInternalFocus>()
    var satisied = false

    fun closestPresentSymbol(internalSymbolDisplay: InternalSymbolDisplay) : SymbolInstance {
        val presentSymbols : MutableList<SymbolInstance> = internalSymbolDisplay.symbolDisplay.filter { it.symbolObj == absentSymbolInstance.symbolObj && it.displayType == SymbolDisplayType.PRESENT }.sortedBy { it.position }.toMutableList()

        if (presentSymbols.isEmpty()) presentSymbols.add( SymbolInstance(absentSymbolInstance.symbolObj, position = 1.01f) )

        //currently closest position
        return presentSymbols.first()
    }

    fun seedChain() {

        this.instancesChain.add(satisfierFocus)
    }

    fun processChain(internalSymbolDisplay: InternalSymbolDisplay) {

        this.instancesChain.reversed().forEach {
            internalSymbolDisplay.circularity.clear()
            it.evaluate(entity, FocusMessage(satisfierFocus = this.satisfierFocus, absentSymbolInstance = this.absentSymbolInstance, presentSymbolInstance = closestPresentSymbol(internalSymbolDisplay)))}
    }

        /*        val headInstance = InternalFocusInstance(satisfierFocus, absentSymbolInstance, 1f)

        if (headInstance.internalFocusObj.satisfyingCondition(presentSymbol) ) {
            headInstance.internalFocusObj.satisfyingResult(entity, presentSymbol)
            satisied = true
        }
        else {
            var chainTailed = false
            var linkInstance = headInstance

            while (!chainTailed) {
                instancesChain.add(linkInstance)

                val nextLinkInstance = InternalFocusInstance(linkInstance.internalFocusObj.satisfyingStrategies.first(), absentSymbolInstance, 1f)

                if (nextLinkInstance.internalFocusObj.satisfyingCondition(presentSymbol)) {
                    val topStrategy = InternalFocusInstance(linkInstance.internalFocusObj.satisfyingStrategies.first(), absentSymbolInstance, 1f)
                    instancesChain.add(topStrategy)
                    chainTailed = true
                } else {
                    linkInstance = nextLinkInstance
                }
            }
        }

        initSize = instancesChain.size

        processChain(internalSymbolDisplay)
    }*/
/*
    fun processChain(internalSymbolDisplay: InternalSymbolDisplay) {

        val presentSymbol = closestPresentSymbol(internalSymbolDisplay)

        var linkSatisfied = true
        var revIdx = instancesChain.size - 1
        var focusPlanSizeDelta = 0

        while (revIdx >= 0 && linkSatisfied) {
            if (instancesChain[revIdx].internalFocusObj.satisfyingCondition(presentSymbol)) {
                instancesChain[revIdx].internalFocusObj.satisfyingResult(entity, presentSymbol)

                instancesChain.removeAt(instancesChain.size - 1)

                focusPlanSizeDelta--
                revIdx--
            } else linkSatisfied = false
        }

        if ((focusPlanSizeDelta == 0) || (revIdx < 0) )
            addLink(FocusMessage(satisfierFocus, presentSymbol))
    }
*/
    fun addLink(focusMessage: FocusMessage) {

        if ( (focusMessage.satisfierFocus != null) && (focusMessage.satisfierFocus == this.satisfierFocus)
                && (focusMessage.absentSymbolInstance != null) && (focusMessage.absentSymbolInstance == this.absentSymbolInstance)
                && (focusMessage.chainStrategy != null) && !this.instancesChain.contains(focusMessage.chainStrategy)) {
            this.instancesChain.add(focusMessage.chainStrategy!!)
        }
    }

    fun removeLink(focusMessage: FocusMessage) {
        if ( (focusMessage.satisfierFocus != null) && (focusMessage.satisfierFocus == this.satisfierFocus)
                && (focusMessage.absentSymbolInstance != null) && (focusMessage.absentSymbolInstance == this.absentSymbolInstance)
                && (focusMessage.chainStrategy != null) && (this.instancesChain.last() == focusMessage.chainStrategy)) {
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