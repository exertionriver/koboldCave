package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.SymbolType

interface IVision : IInternalFocusTypeInstance {

    var targetSymbolTypes : MutableList<SymbolType>
    var urgency : Float
    var accomplishment : Float
}
