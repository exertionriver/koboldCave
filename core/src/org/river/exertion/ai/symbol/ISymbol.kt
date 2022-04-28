package org.river.exertion.ai.symbol

interface ISymbol {

    var tag : String
    var targetMagnetism : SymbolMagnetism
    var cycle : SymbolCycle

    var presentModifiers : MutableSet<SymbolModifier>
    var spawnsPresent : MutableSet<SymbolSpawn>
    var despawnsPresent : MutableSet<SymbolSpawn>
    var absentImpactors : MutableSet<SymbolImpactor>

    fun spawnPresent() : MutableSet<PresentSymbolInstance>
    fun spawnAbsent() : MutableSet<AbsentSymbolInstance>
}