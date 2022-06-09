package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.internalSymbol.core.SymbolInstance

class InternalFocusPlan(var absentSymbolInstance: SymbolInstance, var satisfier: IInternalFocus) {

    var instancesChain = mutableListOf<InternalFocusInstance>()
    var initSize = 0
    var position = 1f
    var satisied = false

    fun buildChain(symbolsPresent : MutableSet<SymbolInstance>) {
        var presentSymbol = symbolsPresent.firstOrNull { it.symbolObj == absentSymbolInstance.symbolObj }
        val headInstance = InternalFocusInstance(satisfier, absentSymbolInstance, 1f)

        if (presentSymbol == null) presentSymbol = SymbolInstance(absentSymbolInstance.symbolObj, position = 1.01f)

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

    fun addLink() {
        if (instancesChain.size > 0) {
            val topStrategy = instancesChain.last().internalFocusObj.satisfyingStrategies.firstOrNull()
            if (topStrategy != null) {
                val topStrategyInstance = InternalFocusInstance(topStrategy, absentSymbolInstance, 1f)
                instancesChain.add(topStrategyInstance)
            }
        } else {
            //to do: recalc / rename satisfier
//            val satisfier = absentSymbolInstance.symbolObj.mitigators.first().mitigatingFocus
//            val headInstance = InternalFocusInstance(satisfier, absentSymbolInstance, 1f)
            val headInstance = InternalFocusInstance(satisfier, absentSymbolInstance, 1f)

            instancesChain.add(headInstance)
        }

    }

    //todo: implement, updating stats in class
    fun removeLink() {

    }
}