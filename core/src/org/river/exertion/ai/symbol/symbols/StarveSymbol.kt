package org.river.exertion.ai.symbol.symbols

import org.river.exertion.ai.symbol.*

object StarveSymbol : ISymbol {

    override var tag = "starving"
    override var targetMagnetism = SymbolMagnetism.REPEL
    override var cycle = SymbolCycle.SINGLE

    override var presentModifiers = mutableSetOf(
        SymbolModifier(TimeElapseSymbol, SymbolMagnetism.ATTRACT, SymbolModifierType.CYCLE_POSITION, .0005f)
    )
    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()
    override var absentImpactors = mutableSetOf<SymbolImpactor>()

    override fun spawnPresent() = mutableSetOf(PresentSymbolInstance(StarveSymbol, 1f))
    override fun spawnAbsent() = mutableSetOf<AbsentSymbolInstance>()
}
