package org.river.exertion.ai.symbol

import org.river.exertion.ai.internalFocus.IInternalFocus

interface IControlSymbol : ISymbol {

    override var tag : String
    override var type : SymbolType

}