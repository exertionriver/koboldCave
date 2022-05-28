package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalFocus.internalFocuses.ConsumeFocus
import org.river.exertion.ai.internalSymbol.core.*

object PossessSymbol : IPerceivedSymbol {

    override var tag = "possess"
    override var type = SymbolType.ORNAMENT
    override var targetMagnetism = SymbolMagnetism.NONE
    override var cycle = SymbolCycle.NONE

    override var presentModifiers = mutableSetOf<PresentSymbolModifier>()
    override var absentModifiers = mutableSetOf<AbsentSymbolModifier>()

    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()
    override var spawnsAbsent = mutableSetOf<SymbolSpawn>()
    override var despawnsAbsent = mutableSetOf<SymbolSpawn>()

    override var satisfiers : MutableSet<IInternalFocus> = mutableSetOf(ConsumeFocus)

    override fun spawnPresent() = PresentSymbolInstance()
    override fun spawnAbsent() = AbsentSymbolInstance()

}
