package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.ConsumeFocus
import org.river.exertion.ai.internalSymbol.core.*

object FoodSymbol : IPerceivedSymbol {

    override var tag = "food"
    override var type = SymbolType.NEED
    override var targetMagnetism = SymbolMagnetism.ATTRACT_CONSUME
    override var cycle = SymbolCycle.MULTIPLE

    override var presentModifiers = mutableSetOf<PresentSymbolModifier>()
    override var absentModifiers = mutableSetOf(
            AbsentSymbolModifier(HungerSymbol, SymbolModifierType.CYCLE_TO_POSITION, .1f,1f),
            AbsentSymbolModifier(StarveSymbol, SymbolModifierType.CYCLE_TO_POSITION, 0f, 3f)
    )

    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()
    override var spawnsAbsent = mutableSetOf<SymbolSpawn>()
    override var despawnsAbsent = mutableSetOf<SymbolSpawn>()

    override var satisfiers : MutableSet<IInternalFocus> = mutableSetOf(ConsumeFocus)

    override fun spawnPresent() = PresentSymbolInstance(FoodSymbol, position = 1f)
    override fun spawnAbsent() = AbsentSymbolInstance(FoodSymbol, position = 0f)

}
