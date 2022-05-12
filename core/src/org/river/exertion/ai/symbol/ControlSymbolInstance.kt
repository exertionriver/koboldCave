package org.river.exertion.ai.symbol

import org.river.exertion.ai.symbol.controlSymbols.NoneSymbol
import org.river.exertion.ai.symbol.perceivedSymbols.NonePerceivedSymbol

data class ControlSymbolInstance (var symbolObj : ISymbol = NoneSymbol, var units : Float = 0f, var position : Float = 0f) {

    var target : IPerceivedSymbol = NonePerceivedSymbol
}