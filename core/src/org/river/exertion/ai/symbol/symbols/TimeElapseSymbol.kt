package org.river.exertion.ai.symbol.symbols

import org.river.exertion.ai.symbol.*

object TimeElapseSymbol : ISymbol {

    override var tag = "time elapse"
    override var targetMagnetism = SymbolMagnetism.ATTRACT
    override var cycle = SymbolCycle.MULTIPLE

    override var modifiers = mutableSetOf<SymbolModifier>()
    override var spawns = mutableSetOf<SymbolSpawn>()
    override var despawns = mutableSetOf<SymbolSpawn>()

    override fun spawn() = SymbolInstance(TimeElapseSymbol, 1f)
}
