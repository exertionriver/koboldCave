package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalFocus.IInternalFocus

interface IPerceivedSymbol : IInternalSymbol {

    override var tag : String
    override var type : SymbolType
    var targetMagnetism : SymbolMagnetism
    var cycle : SymbolCycle

    var presentModifiers : MutableSet<PresentSymbolModifier>
    var absentModifiers : MutableSet<AbsentSymbolModifier>

    var spawnsPresent : MutableSet<SymbolSpawn>
    var despawnsPresent : MutableSet<SymbolSpawn>
    var spawnsAbsent : MutableSet<SymbolSpawn>
    var despawnsAbsent : MutableSet<SymbolSpawn>

    var satisfiers : MutableSet<IInternalFocus>

    fun spawnPresent() : PresentSymbolInstance
    fun spawnAbsent() : AbsentSymbolInstance
}