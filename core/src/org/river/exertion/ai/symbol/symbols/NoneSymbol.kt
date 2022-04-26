package org.river.exertion.ai.symbol.symbols

import org.river.exertion.ai.symbol.*

object NoneSymbol : ISymbol {

    override var tag = "none"
    override var targetMagnetism = SymbolMagnetism.NONE
    override var cycle = SymbolCycle.NONE

    override var modifiers = mutableSetOf<SymbolModifier>()
    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()

    override fun spawnPresent() = mutableSetOf(SymbolInstance(NoneSymbol, 0f))
    override fun spawnAbsent() = mutableSetOf<SymbolInstance>()
}
