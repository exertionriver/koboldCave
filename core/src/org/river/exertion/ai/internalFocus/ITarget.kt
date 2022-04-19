package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.SymbolType

interface ITarget : IInternalFocusTypeInstance {

    var targetSymbolType : SymbolType
    var satisfactionSymbolType : SymbolType
    var urgency : Float

}

