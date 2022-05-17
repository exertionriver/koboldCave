package org.river.exertion.ai.internalSymbol.controlSymbols

import org.river.exertion.ai.internalSymbol.core.IControlSymbol
import org.river.exertion.ai.internalSymbol.core.SymbolType

object DespawnSymbol : IControlSymbol {

    override var tag = "despawn"
    override var type = SymbolType.DESPAWN
}
