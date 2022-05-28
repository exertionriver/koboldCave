package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalSymbol.controlSymbols.DespawnSymbol
import org.river.exertion.ai.internalSymbol.controlSymbols.SpawnSymbol

class InternalSymbolDisplay {

    var symbolsPresent = mutableSetOf<PresentSymbolInstance>()
    var symbolsAbsent = mutableSetOf<AbsentSymbolInstance>()

}