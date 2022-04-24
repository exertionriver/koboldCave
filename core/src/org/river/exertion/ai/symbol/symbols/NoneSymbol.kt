package org.river.exertion.ai.symbol.symbols

import org.river.exertion.ai.symbol.*

object NoneSymbol : ISymbol {

    override var tag = "none"
    override var targetMagnetism = SymbolMagnetism.NONE
    override var cycle = SymbolCycle.NONE

    override var modifiers = mutableSetOf<SymbolModifier>()
    override var spawns = mutableSetOf<SymbolSpawn>()
    override var despawns = mutableSetOf<SymbolSpawn>()

    override fun spawn() = SymbolInstance(NoneSymbol, 0f)

}
