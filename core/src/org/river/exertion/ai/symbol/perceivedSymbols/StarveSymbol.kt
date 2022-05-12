package org.river.exertion.ai.symbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.symbol.*

object StarveSymbol : IPerceivedSymbol {

    override var tag = "starving"
    override var type = SymbolType.DRIVE
    override var targetMagnetism = SymbolMagnetism.REPEL_LIMINAL
    override var cycle = SymbolCycle.SINGLE

    override var presentModifiers = mutableSetOf(
        SymbolModifier(MomentElapseSymbol, SymbolMagnetism.ATTRACT_CONSUME, SymbolModifierType.CYCLE_POSITION, .0005f)
    )
    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()
    override var spawnsAbsent = mutableSetOf<SymbolSpawn>()
    override var despawnsAbsent = mutableSetOf<SymbolSpawn>()
    override var absentImpactors = mutableSetOf<SymbolImpactor>()
    override var satisfiers = mutableSetOf<IInternalFocus>()

    override fun spawnPresent() = mutableSetOf(PresentSymbolInstance(StarveSymbol, position = 1f))
    override fun spawnAbsent() = mutableSetOf<AbsentSymbolInstance>()
}
