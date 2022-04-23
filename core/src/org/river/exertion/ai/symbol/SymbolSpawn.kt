package org.river.exertion.ai.symbol

import org.river.exertion.ai.symbol.symbols.NoneSymbol

data class SymbolSpawn(var modifyingSymbol : ISymbol = NoneSymbol, var cycle : Float = 0f, var position : Float = 0f)
