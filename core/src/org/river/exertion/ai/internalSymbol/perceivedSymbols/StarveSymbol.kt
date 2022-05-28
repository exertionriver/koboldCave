package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.*

object StarveSymbol : IPerceivedSymbol {

    override var tag = "starving"
    override var type = SymbolType.DRIVE
    override var targetMagnetism = SymbolMagnetism.REPEL_LIMINAL
    override var cycle = SymbolCycle.SINGLE

    override var presentModifiers = mutableSetOf(
        PresentSymbolModifier(MomentElapseSymbol, -.005f)
    )
    override var absentModifiers = mutableSetOf<AbsentSymbolModifier>()

    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()
    override var spawnsAbsent = mutableSetOf<SymbolSpawn>()
    override var despawnsAbsent = mutableSetOf<SymbolSpawn>()

    override var satisfiers = mutableSetOf<IInternalFocus>()

    override fun spawnPresent() = PresentSymbolInstance(StarveSymbol, position = 1f)
    override fun spawnAbsent() = AbsentSymbolInstance()
}
