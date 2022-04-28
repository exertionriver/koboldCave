package org.river.exertion.ai.symbol.symbols

import org.river.exertion.ai.symbol.*

object NoneSymbol : ISymbol {

    override var tag = "none"
    override var targetMagnetism = SymbolMagnetism.NONE
    override var cycle = SymbolCycle.NONE

    override var presentModifiers = mutableSetOf<SymbolModifier>()
    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()
    override var absentImpactors = mutableSetOf<SymbolImpactor>()

    override fun spawnPresent() = mutableSetOf(PresentSymbolInstance(NoneSymbol, 0f))
    override fun spawnAbsent() = mutableSetOf<AbsentSymbolInstance>()
}
