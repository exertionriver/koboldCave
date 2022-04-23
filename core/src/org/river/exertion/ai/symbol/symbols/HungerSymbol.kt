package org.river.exertion.ai.symbol.symbols

import org.river.exertion.ai.symbol.*

object HungerSymbol : ISymbol {

    override var tag = "hunger"
    override var targetMagnetism = SymbolMagnetism.REPEL
    override var cycle = SymbolCycle.SINGLE

    override var modifiers = mutableSetOf(
        SymbolModifier(FoodSymbol, SymbolMagnetism.REPEL, SymbolModifierType.CYCLE_COUNT, .1f),
        SymbolModifier(TimeElapseSymbol, SymbolMagnetism.ATTRACT, SymbolModifierType.CYCLE_POSITION, .0001f)
    )

    override var spawns = mutableSetOf(
        SymbolSpawn(StarveSymbol, 0f,.1f),
    )
}
