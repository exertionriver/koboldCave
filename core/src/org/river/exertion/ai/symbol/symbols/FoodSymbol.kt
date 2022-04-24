package org.river.exertion.ai.symbol.symbols

import org.river.exertion.ai.symbol.*

object FoodSymbol : ISymbol {

    override var tag = "food"
    override var targetMagnetism = SymbolMagnetism.ATTRACT
    override var cycle = SymbolCycle.MULTIPLE

    override var modifiers = mutableSetOf<SymbolModifier>()
    override var spawns = mutableSetOf<SymbolSpawn>()
    override var despawns = mutableSetOf<SymbolSpawn>()

    override fun spawn() = SymbolInstance(FoodSymbol, 1f)

}
