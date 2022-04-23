package org.river.exertion.ai.symbol.symbols

import org.river.exertion.ai.symbol.*

object StarveSymbol : ISymbol {

    override var tag = "starving"
    override var targetMagnetism = SymbolMagnetism.REPEL
    override var cycle = SymbolCycle.SINGLE

    override var modifiers = mutableSetOf(
        SymbolModifier(TimeElapseSymbol, SymbolMagnetism.ATTRACT, SymbolModifierType.CYCLE_POSITION, .001f)
    )

    override var spawns = mutableSetOf<SymbolSpawn>()
}
