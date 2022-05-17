package org.river.exertion.ai.internalSymbol.controlSymbols

import org.river.exertion.ai.internalSymbol.core.IControlSymbol
import org.river.exertion.ai.internalSymbol.core.SymbolType

object SpawnSymbol : IControlSymbol {

    override var tag = "spawn"
    override var type = SymbolType.SPAWN
}
