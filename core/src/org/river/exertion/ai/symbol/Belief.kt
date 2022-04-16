package org.river.exertion.ai.symbol

//conviction == 1f, complete conviction; conviction == 0f, loss of conviction
data class Belief(override var type: SymbolType, override var referent: Any, var conviction : Float) : ISymbol