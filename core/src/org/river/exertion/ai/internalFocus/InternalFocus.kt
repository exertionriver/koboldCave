package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.SymbolType

data class InternalFocus(override var type : InternalFocusType, override var targetSymbolType : SymbolType, var conviction : Float, var accomplishment : Float) : IInternalFocus