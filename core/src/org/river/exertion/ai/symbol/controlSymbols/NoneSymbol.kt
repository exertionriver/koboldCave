package org.river.exertion.ai.symbol.controlSymbols

import org.river.exertion.ai.symbol.ISymbol
import org.river.exertion.ai.symbol.SymbolType

object NoneSymbol : ISymbol {

    override var tag = "none"
    override var type = SymbolType.NONE
}
