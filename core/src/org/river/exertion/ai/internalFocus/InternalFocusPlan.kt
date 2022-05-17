package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.internalSymbol.core.AbsentSymbolInstance
import org.river.exertion.ai.internalSymbol.core.PresentSymbolInstance

class InternalFocusPlan(var absentSymbolInstance: AbsentSymbolInstance) {

    var instancesChain = mutableListOf<InternalFocusInstance>()
    var initSize = 0
    var position = 1f
    var satisied = false

    fun init(symbolsPresent : MutableSet<PresentSymbolInstance>) {
        var presentSymbol = symbolsPresent.firstOrNull { it.symbolObj == absentSymbolInstance.symbolObj }
        val satisfier = absentSymbolInstance.symbolObj.satisfiers.first()
        val headInstance = InternalFocusInstance(satisfier, absentSymbolInstance, 1f)

        if (presentSymbol == null) presentSymbol = PresentSymbolInstance(absentSymbolInstance.symbolObj, 1.01f)

        if (headInstance.internalFocusObj.satisfyingCondition(presentSymbol) ) {
            position = 0f
            satisied = true
        }
        else {
            var chainTailed = false
            var linkInstance = headInstance

            while (!chainTailed) {
                instancesChain.add(linkInstance)

                val nextLinkInstance = InternalFocusInstance(linkInstance.internalFocusObj.dependsUpon.first(), absentSymbolInstance, 1f)

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
    }

    fun update(symbolsPresent : MutableSet<PresentSymbolInstance>) {

        if ( instancesChain.isNotEmpty() ) {
            var presentSymbol = symbolsPresent.firstOrNull { it.symbolObj == absentSymbolInstance.symbolObj }

            var chainPosition = 0
            var satisfiedPosition = -1

            if (presentSymbol == null) presentSymbol = PresentSymbolInstance(absentSymbolInstance.symbolObj, 1.01f)

            while ( (satisfiedPosition < 0) && (chainPosition < instancesChain.size) ) {
                val internalFocusInstance = instancesChain[chainPosition]

                if (internalFocusInstance.internalFocusObj.satisfyingCondition(presentSymbol)) {
                    satisfiedPosition = chainPosition
                }

                chainPosition++
            }

            if (satisfiedPosition >= 0) {
                (instancesChain.size - 1 downTo satisfiedPosition).forEach { instancesChain.removeAt(it) }

                if (satisfiedPosition == 0) {
                    position = 0f
                    satisied = true
                }

                if (satisfiedPosition > 0) {

                    val topStrategy = instancesChain[satisfiedPosition - 1].internalFocusObj.satisfyingStrategies.firstOrNull()

                    if (topStrategy != null) {
                        val topStrategyInstance = InternalFocusInstance(topStrategy, absentSymbolInstance, 1f)
                        instancesChain.add(topStrategyInstance)
                    }

                    position = (instancesChain.size / initSize).toFloat()
                }
            }
        }
    }
}