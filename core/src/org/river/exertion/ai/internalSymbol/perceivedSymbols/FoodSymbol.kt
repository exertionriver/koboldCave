package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.ConsumeFocus
import org.river.exertion.ai.internalSymbol.core.*

object FoodSymbol : IPerceivedSymbol {

    override var tag = "food"
    override var type = SymbolType.NEED
    override var targetMagnetism = SymbolMagnetism.ATTRACT_CONSUME
    override var cycle = SymbolCycle.MULTIPLE

    override var presentModifiers = mutableSetOf<SymbolModifier>()
    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()
    override var spawnsAbsent = mutableSetOf<SymbolSpawn>()
    override var despawnsAbsent = mutableSetOf<SymbolSpawn>()
    override var absentImpactors = mutableSetOf(
            SymbolImpactor(HungerSymbol, SymbolMagnetism.REPEL_LIMINAL, SymbolModifierType.CYCLE_POSITION, .1f,1f),
            SymbolImpactor(StarveSymbol, SymbolMagnetism.REPEL_LIMINAL, SymbolModifierType.CYCLE_POSITION, 0f, 3f)
    )

    override var satisfiers : MutableSet<IInternalFocus> = mutableSetOf(ConsumeFocus)

    override fun spawnPresent() = mutableSetOf(PresentSymbolInstance(FoodSymbol, position = 1f))
    override fun spawnAbsent() = mutableSetOf(AbsentSymbolInstance(FoodSymbol, position = 0f))

}
