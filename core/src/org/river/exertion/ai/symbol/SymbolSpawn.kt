package org.river.exertion.ai.symbol

import org.river.exertion.ai.symbol.perceivedSymbols.NonePerceivedSymbol

data class SymbolSpawn(var spawnSymbol : IPerceivedSymbol = NonePerceivedSymbol, var thresholdType: SymbolThresholdType = SymbolThresholdType.NONE, var position : Float = 0f)
