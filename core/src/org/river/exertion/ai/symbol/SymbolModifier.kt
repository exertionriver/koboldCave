package org.river.exertion.ai.symbol

import org.river.exertion.ai.symbol.symbols.NoneSymbol

data class SymbolModifier(var modifyingSymbol : ISymbol = NoneSymbol, var modifyingMagnetism : SymbolMagnetism = SymbolMagnetism.NONE, var modifyingType : SymbolModifierType = SymbolModifierType.NONE, var modifierRatio : Float = 0f)
