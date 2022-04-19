package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.SymbolType

interface INeed : IInternalFocusTypeInstance {

    var targetSymbolType : SymbolType
    var triggerSymbolType : SymbolType
    var satisfactionSymbolType : SymbolType

    var urgency : Float
}