package org.river.exertion.ai.symbol

interface ISymbol {

    var tag : String
    var targetMagnetism : SymbolMagnetism
    var cycle : SymbolCycle

    var modifiers : MutableSet<SymbolModifier>
    var spawnsPresent : MutableSet<SymbolSpawn>
    var despawnsPresent : MutableSet<SymbolSpawn>

    fun spawnPresent() : MutableSet<SymbolInstance>
    fun spawnAbsent() : MutableSet<SymbolInstance>
}