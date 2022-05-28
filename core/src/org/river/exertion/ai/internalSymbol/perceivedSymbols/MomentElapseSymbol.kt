package org.river.exertion.ai.internalSymbol.perceivedSymbols

import org.river.exertion.ai.internalFocus.IInternalFocus
import org.river.exertion.ai.internalSymbol.core.*

object MomentElapseSymbol : IPerceivedSymbol {

    override var tag = "moment elapse"
    override var type = SymbolType.TIME
    override var targetMagnetism = SymbolMagnetism.ATTRACT_CONSUME
    override var cycle = SymbolCycle.MULTIPLE

    override var presentModifiers = mutableSetOf<PresentSymbolModifier>()
    override var absentModifiers = mutableSetOf<AbsentSymbolModifier>()

    override var spawnsPresent = mutableSetOf<SymbolSpawn>()
    override var despawnsPresent = mutableSetOf<SymbolSpawn>()
    override var spawnsAbsent = mutableSetOf<SymbolSpawn>()
    override var despawnsAbsent = mutableSetOf<SymbolSpawn>()

    override var satisfiers = mutableSetOf<IInternalFocus>()

    override fun spawnPresent() = PresentSymbolInstance(MomentElapseSymbol, position = 1f)
    override fun spawnAbsent() = AbsentSymbolInstance()
}
