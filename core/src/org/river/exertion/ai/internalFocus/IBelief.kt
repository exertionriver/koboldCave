package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.SymbolInstance

interface IBelief : IInternalFocusTypeInstance {

    var symbol : SymbolInstance
    var conviction : Float
}