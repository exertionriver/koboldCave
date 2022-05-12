package org.river.exertion.ai.symbol

import org.river.exertion.ai.symbol.perceivedSymbols.NonePerceivedSymbol

data class SymbolModifier(var modifyingSymbol : IPerceivedSymbol = NonePerceivedSymbol, var modifyingMagnetism : SymbolMagnetism = SymbolMagnetism.NONE, var modifyingType : SymbolModifierType = SymbolModifierType.NONE, var modifierRatio : Float = 0f)
