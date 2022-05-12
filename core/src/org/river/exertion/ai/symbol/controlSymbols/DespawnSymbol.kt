package org.river.exertion.ai.symbol.controlSymbols

import org.river.exertion.ai.symbol.IControlSymbol
import org.river.exertion.ai.symbol.ISymbol
import org.river.exertion.ai.symbol.SymbolType

object DespawnSymbol : IControlSymbol {

    override var tag = "despawn"
    override var type = SymbolType.DESPAWN
}
