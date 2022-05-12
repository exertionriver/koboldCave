package org.river.exertion.ai.symbol

import org.river.exertion.ai.symbol.perceivedSymbols.NonePerceivedSymbol

data class AbsentSymbolInstance (var symbolObj : IPerceivedSymbol = NonePerceivedSymbol, var position : Float = 0f, var impact : Float = 0f) {

    var ornaments = mutableSetOf<AbsentSymbolInstance>()
}