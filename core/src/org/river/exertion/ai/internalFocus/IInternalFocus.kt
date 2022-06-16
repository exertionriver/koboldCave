package org.river.exertion.ai.internalFocus

import com.badlogic.gdx.ai.msg.MessageManager
import com.badlogic.gdx.ai.msg.Telegraph
import org.river.exertion.ai.internalSymbol.core.SymbolDisplayType
import org.river.exertion.ai.internalSymbol.core.SymbolInstance
import org.river.exertion.ai.messaging.FocusMessage
import org.river.exertion.ai.messaging.MessageChannel
import org.river.exertion.ai.messaging.SymbolMessage
import org.river.exertion.logDebug

interface IInternalFocus {

    var tag : String
    var satisfyingStrategies : MutableList<IInternalFocus>

    fun satisfyingCondition(targetPresentSymbol : SymbolInstance) : Boolean

    fun satisfyingResult(entity: Telegraph, targetPresentSymbol: SymbolInstance)

    fun evaluate(entity: Telegraph, focusMessage : FocusMessage) {
        if (focusMessage.presentSymbolInstance != null)
            if (satisfyingCondition(focusMessage.presentSymbolInstance!!) ) {
                satisfyingResult(entity, focusMessage.presentSymbolInstance!!)
//                logDebug("evaluate", "${this.tag} REMOVE : ${this@IInternalFocus.tag}")
                MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_REMOVE_FOCUS_CHAIN_LINK.id(), focusMessage.apply { this.chainStrategy = this@IInternalFocus })
            } else {
                satisfyingStrategies.forEach {
                    if (!satisfyingCondition(focusMessage.presentSymbolInstance!!) ) {
//                        logDebug("evaluate", "${this.tag} ADD : ${it.tag}")
                        MessageManager.getInstance().dispatchMessage(entity, MessageChannel.INT_ADD_FOCUS_CHAIN_LINK.id(), focusMessage.apply { this.chainStrategy = it })
                    }
                }
            }

    }
}