package org.river.exertion.ai.symbol.controlSymbols

import org.river.exertion.ai.symbol.IControlSymbol
import org.river.exertion.ai.symbol.IPerceivedSymbol
import org.river.exertion.ai.symbol.ISymbol
import org.river.exertion.ai.symbol.SymbolType
import org.river.exertion.ai.symbol.perceivedSymbols.NonePerceivedSymbol

object SpawnSymbol : IControlSymbol {

    override var tag = "spawn"
    override var type = SymbolType.SPAWN
}
