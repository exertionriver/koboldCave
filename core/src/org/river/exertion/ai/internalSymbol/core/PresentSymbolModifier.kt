package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalSymbol.perceivedSymbols.NonePerceivedSymbol

data class PresentSymbolModifier(var modifyingSymbol : IPerceivedSymbol = NonePerceivedSymbol, var modifierRatio : Float = 0f)
