package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.internalFocus.internalFocuses.NoneFocus
import org.river.exertion.ai.symbol.AbsentSymbolInstance

data class InternalFocusInstance (var internalFocusObj : IInternalFocus = NoneFocus, var targetSymbol : AbsentSymbolInstance, var position : Float = 0f)
