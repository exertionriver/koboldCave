package org.river.exertion.ai.symbol.symbols

import org.river.exertion.ai.symbol.*

object TimeElapseSymbol : ISymbol {

    override var tag = "time elapse"
    override var targetMagnetism = SymbolMagnetism.ATTRACT
    override var cycle = SymbolCycle.MULTIPLE

    override var modifiers = mutableSetOf<SymbolModifier>()
    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()

    override fun spawnPresent() = mutableSetOf(SymbolInstance(TimeElapseSymbol, 1f))
    override fun spawnAbsent() = mutableSetOf<SymbolInstance>()
}
