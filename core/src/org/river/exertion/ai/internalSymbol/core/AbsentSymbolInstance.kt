package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalSymbol.perceivedSymbols.NonePerceivedSymbol

data class AbsentSymbolInstance (var symbolObj : IPerceivedSymbol = NonePerceivedSymbol, var position : Float = 0f, var impact : Float = 0f) {

    var ornaments = mutableSetOf<AbsentSymbolInstance>()
}