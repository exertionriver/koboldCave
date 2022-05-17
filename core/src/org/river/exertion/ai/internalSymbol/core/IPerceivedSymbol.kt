package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalFocus.IInternalFocus

interface IPerceivedSymbol : IInternalSymbol {

    override var tag : String
    override var type : SymbolType
    var targetMagnetism : SymbolMagnetism
    var cycle : SymbolCycle

    var presentModifiers : MutableSet<SymbolModifier>
    var spawnsPresent : MutableSet<SymbolSpawn>
    var despawnsPresent : MutableSet<SymbolSpawn>
    var spawnsAbsent : MutableSet<SymbolSpawn>
    var despawnsAbsent : MutableSet<SymbolSpawn>
    var absentImpactors : MutableSet<SymbolImpactor>
    var satisfiers : MutableSet<IInternalFocus>

    fun spawnPresent() : MutableSet<PresentSymbolInstance>
    fun spawnAbsent() : MutableSet<AbsentSymbolInstance>
}