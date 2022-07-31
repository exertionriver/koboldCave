package org.river.exertion.ai.internalFocus

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
                    MessageChannel.INT_REMOVE_FOCUS_CHAIN_LINK.send(entity, focusMessage.apply { this.chainStrategyInstance = this@IInternalFocus.instantiate() })
                    return true
                } else {
                    focusMessage.chainStrategyInstance!!.momentCounter += momentDelta
                    return true
                }
            } else {
                var addedStrategy = false
                var strategyIdx = 0

                while ((!addedStrategy) && (strategyIdx < satisfyingStrategies.size) ) {

                    if (!satisfyingStrategies[strategyIdx].satisfyingCondition(focusMessage.presentSymbolInstance!!) ) {
//                        logDebug("evaluate", "${this.tag} ADD : ${it.tag}")
                        MessageChannel.INT_ADD_FOCUS_CHAIN_LINK.send(entity, focusMessage.apply { this.chainStrategyInstance = satisfyingStrategies[strategyIdx].instantiate() })
                        addedStrategy = true
                    } else {
                        satisfyingStrategies[strategyIdx].satisfyingResult(entity, focusMessage.presentSymbolInstance!!)
                    }
                    strategyIdx++
                }
            }
        return false
    }

    fun instantiate() : InternalFocusInstance = InternalFocusInstance(this, this.momentMinimum)

}