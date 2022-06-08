package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.internalSymbol.core.SymbolInstance

interface IBelief : IInternalFocusTypeInstance {

    var symbol : SymbolInstance
    var conviction : Float
}