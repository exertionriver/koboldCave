package org.river.exertion.ai.symbol.symbols

import org.river.exertion.ai.symbol.*

object HungerSymbol : ISymbol {

    override var tag = "hunger"
    override var targetMagnetism = SymbolMagnetism.REPEL
    override var cycle = SymbolCycle.SINGLE

    override var presentModifiers = mutableSetOf(
        SymbolModifier(FoodSymbol, SymbolMagnetism.REPEL, SymbolModifierType.CYCLE_COUNT, .1f),
        SymbolModifier(TimeElapseSymbol, SymbolMagnetism.ATTRACT, SymbolModifierType.CYCLE_POSITION, .0001f)
    )
    override var spawnsPresent = mutableSetOf(
        SymbolSpawn(StarveSymbol, SymbolThresholdType.LESS_THAN, .2f),
    )
    override var despawnsPresent = mutableSetOf(
        SymbolSpawn(StarveSymbol, SymbolThresholdType.GREATER_THAN, .3f),
    )
    override var absentImpactors = mutableSetOf<SymbolImpactor>()

    override fun spawnPresent() = mutableSetOf(PresentSymbolInstance(HungerSymbol, 1f))
    override fun spawnAbsent() = mutableSetOf(AbsentSymbolInstance(FoodSymbol, 0f))
}
