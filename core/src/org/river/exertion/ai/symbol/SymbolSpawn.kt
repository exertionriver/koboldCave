package org.river.exertion.ai.symbol

import org.river.exertion.ai.symbol.symbols.NoneSymbol

data class SymbolSpawn(var spawnSymbol : ISymbol = NoneSymbol, var thresholdType: SymbolThresholdType = SymbolThresholdType.NONE, var position : Float = 0f)
