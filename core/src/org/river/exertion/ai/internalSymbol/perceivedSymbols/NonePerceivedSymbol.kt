package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.*

object NonePerceivedSymbol : IPerceivedSymbol {

    override var tag = "none perceived"
    override var type = SymbolType.NONE
    override var targetMagnetism = SymbolMagnetism.NONE
    override var cycle = SymbolCycle.NONE

    override var presentModifiers = mutableSetOf<PresentSymbolModifier>()
    override var absentModifiers = mutableSetOf<AbsentSymbolModifier>()

    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()
    override var spawnsAbsent = mutableSetOf<SymbolSpawn>()
    override var despawnsAbsent = mutableSetOf<SymbolSpawn>()

    override var satisfiers = mutableSetOf<IInternalFocus>()

    override fun spawnPresent() = PresentSymbolInstance()
    override fun spawnAbsent() = AbsentSymbolInstance()
}
