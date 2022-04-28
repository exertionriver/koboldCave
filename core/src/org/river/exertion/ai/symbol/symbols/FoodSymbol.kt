package org.river.exertion.ai.symbol.symbols

import org.river.exertion.ai.symbol.*

object FoodSymbol : ISymbol {

    override var tag = "food"
    override var targetMagnetism = SymbolMagnetism.ATTRACT
    override var cycle = SymbolCycle.MULTIPLE

    override var presentModifiers = mutableSetOf<SymbolModifier>()
    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()
    override var absentImpactors = mutableSetOf(
            SymbolImpactor(HungerSymbol, SymbolMagnetism.REPEL, SymbolModifierType.CYCLE_POSITION, .1f,1f),
            SymbolImpactor(StarveSymbol, SymbolMagnetism.REPEL, SymbolModifierType.CYCLE_POSITION, 0f, 3f)
    )

    override fun spawnPresent() = mutableSetOf(PresentSymbolInstance(FoodSymbol, 1f))
    override fun spawnAbsent() = mutableSetOf<AbsentSymbolInstance>()

}
