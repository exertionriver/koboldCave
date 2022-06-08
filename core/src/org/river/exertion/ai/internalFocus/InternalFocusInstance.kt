package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.internalFocus.internalFocuses.NoneFocus
import org.river.exertion.ai.internalSymbol.core.SymbolInstance

data class InternalFocusInstance (var internalFocusObj : IInternalFocus = NoneFocus, var targetSymbol : SymbolInstance, var position : Float = 0f)
