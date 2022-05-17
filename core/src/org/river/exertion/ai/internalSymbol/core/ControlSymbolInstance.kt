package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalSymbol.controlSymbols.NoneSymbol
import org.river.exertion.ai.internalSymbol.perceivedSymbols.NonePerceivedSymbol

data class ControlSymbolInstance (var symbolObj : IInternalSymbol = NoneSymbol, var units : Float = 0f, var position : Float = 0f) {

    var target : IPerceivedSymbol = NonePerceivedSymbol
}