package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.PresentSymbolInstance

interface IBelief : IInternalFocusTypeInstance {

    var symbol : PresentSymbolInstance
    var conviction : Float
}