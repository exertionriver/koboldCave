package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.*

object HungerSymbol : IPerceivedSymbol {

    override var tag = "hunger"
    override var type = SymbolType.DRIVE
    override var targetMagnetism = SymbolMagnetism.REPEL_LIMINAL
    override var cycle = SymbolCycle.SINGLE

    override var presentModifiers = mutableSetOf(
        PresentSymbolModifier(FoodSymbol,.1f),
        PresentSymbolModifier(MomentElapseSymbol, -.001f)
    )
    override var absentModifiers = mutableSetOf<AbsentSymbolModifier>()

    override var spawnsPresent = mutableSetOf(
        SymbolSpawn(StarveSymbol, SymbolThresholdType.LESS_THAN, .2f),
    )
    override var despawnsPresent = mutableSetOf(
        SymbolSpawn(StarveSymbol, SymbolThresholdType.GREATER_THAN, .3f),
    )
    override var spawnsAbsent = mutableSetOf(
        SymbolSpawn(FoodSymbol, SymbolThresholdType.LESS_THAN, .5f),
    )
    override var despawnsAbsent = mutableSetOf(
        SymbolSpawn(FoodSymbol, SymbolThresholdType.GREATER_THAN, .8f),
    )

    override var satisfiers = mutableSetOf<IInternalFocus>()

    override fun spawnPresent() = PresentSymbolInstance(HungerSymbol, position = 1f)
    override fun spawnAbsent() = AbsentSymbolInstance()
}
