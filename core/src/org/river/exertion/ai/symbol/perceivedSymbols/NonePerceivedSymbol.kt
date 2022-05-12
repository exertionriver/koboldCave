package org.river.exertion.ai.symbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.symbol.*

object NonePerceivedSymbol : IPerceivedSymbol {

    override var tag = "none perceived"
    override var type = SymbolType.NONE
    override var targetMagnetism = SymbolMagnetism.NONE
    override var cycle = SymbolCycle.NONE

    override var presentModifiers = mutableSetOf<SymbolModifier>()
    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()
    override var spawnsAbsent = mutableSetOf<SymbolSpawn>()
    override var despawnsAbsent = mutableSetOf<SymbolSpawn>()
    override var absentImpactors = mutableSetOf<SymbolImpactor>()

    override var satisfiers = mutableSetOf<IInternalFocus>()

    override fun spawnPresent() = mutableSetOf<PresentSymbolInstance>()
    override fun spawnAbsent() = mutableSetOf<AbsentSymbolInstance>()
}
