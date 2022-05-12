package org.river.exertion.ai.symbol

import org.river.exertion.ai.symbol.perceivedSymbols.NonePerceivedSymbol

data class PresentSymbolInstance (var symbolObj : IPerceivedSymbol = NonePerceivedSymbol, var units : Float = 0f, var position : Float = 0f) {

    var consumeCapacity = 0f
    var handleCapacity = 0f
    var possessCapacity = 0f

    var ornaments = mutableSetOf<ControlSymbolInstance>()

}