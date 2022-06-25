package org.river.exertion.ai.internalFocus

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.messaging.FocusMessage
import org.river.exertion.ai.messaging.MessageChannel

interface IInternalFocus {

    var tag : String
    var type : InternalFocusType
    var momentMinimum : Float

    var satisfyingStrategies : MutableList<IInternalFocus>

    fun satisfyingCondition(targetPresentSymbol : SymbolInstance) : Boolean

    fun satisfyingResult(entity: Telegraph, targetPresentSymbol: SymbolInstance)

//    fun execute()

    fun evaluate(entity: Telegraph, focusMessage : FocusMessage, momentDelta : Float) : Boolean {
        if (focusMessage.presentSymbolInstance != null)
            if (satisfyingCondition(focusMessage.presentSymbolInstance!!) ) {
                if (focusMessage.chainStrategyInstance!!.momentCounter <= 0) {
                    satisfyingResult(entity, focusMessage.presentSymbolInstance!!)
//                logDebug("evaluate", "${this.tag} REMOVE : ${this@IInternalFocus.tag}")
                    MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_REMOVE_FOCUS_CHAIN_LINK.id(), focusMessage.apply { this.chainStrategyInstance = this@IInternalFocus.instantiate() })
                    return true
                } else {
                    focusMessage.chainStrategyInstance!!.momentCounter += momentDelta
                    return true
                }
            } else {
                satisfyingStrategies.forEach {
                    if (!satisfyingCondition(focusMessage.presentSymbolInstance!!) ) {
//                        logDebug("evaluate", "${this.tag} ADD : ${it.tag}")
                        MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_ADD_FOCUS_CHAIN_LINK.id(), focusMessage.apply { this.chainStrategyInstance = it.instantiate() })
                    }
                }
            }
        return false
    }

    fun instantiate() : InternalFocusInstance = InternalFocusInstance(this, this.momentMinimum)

}