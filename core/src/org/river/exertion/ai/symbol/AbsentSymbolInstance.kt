package org.river.exertion.ai.symbol

import org.river.exertion.ai.symbol.symbols.NoneSymbol

data class AbsentSymbolInstance (var symbolObj : ISymbol = NoneSymbol, var position : Float = 0f, var impact : Float = 0f) {

}