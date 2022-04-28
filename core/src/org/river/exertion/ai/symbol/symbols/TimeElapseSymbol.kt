package org.river.exertion.ai.symbol.symbols

import org.river.exertion.ai.symbol.*

object TimeElapseSymbol : ISymbol {

    override var tag = "time elapse"
    override var targetMagnetism = SymbolMagnetism.ATTRACT
    override var cycle = SymbolCycle.MULTIPLE

    override var presentModifiers = mutableSetOf<SymbolModifier>()
    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()
    override var absentImpactors = mutableSetOf<SymbolImpactor>()

    override fun spawnPresent() = mutableSetOf(PresentSymbolInstance(TimeElapseSymbol, 1f))
    override fun spawnAbsent() = mutableSetOf<AbsentSymbolInstance>()
}
