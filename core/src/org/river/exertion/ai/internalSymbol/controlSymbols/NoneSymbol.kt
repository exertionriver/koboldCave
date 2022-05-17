package org.river.exertion.ai.internalSymbol.controlSymbols

import org.river.exertion.ai.internalSymbol.core.IInternalSymbol
import org.river.exertion.ai.internalSymbol.core.SymbolType

object NoneSymbol : IInternalSymbol {

    override var tag = "none"
    override var type = SymbolType.NONE
}
