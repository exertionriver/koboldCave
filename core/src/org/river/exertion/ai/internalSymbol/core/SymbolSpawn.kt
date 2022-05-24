package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalSymbol.perceivedSymbols.NonePerceivedSymbol

data class SymbolSpawn(var spawnSymbol : IPerceivedSymbol = NonePerceivedSymbol, var thresholdType: SymbolThresholdType = SymbolThresholdType.NONE, var position : Float = 0f)