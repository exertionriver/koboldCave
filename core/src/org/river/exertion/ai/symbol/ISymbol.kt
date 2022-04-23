package org.river.exertion.ai.symbol

interface ISymbol {

    var tag : String
    var targetMagnetism : SymbolMagnetism
    var cycle : SymbolCycle

    var modifiers : MutableSet<SymbolModifier>
    var spawns : MutableSet<SymbolSpawn>

}