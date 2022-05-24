package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalSymbol.perceivedSymbols.NonePerceivedSymbol

data class SymbolModifier(var modifyingSymbol : IPerceivedSymbol = NonePerceivedSymbol, var modifyingMagnetism : SymbolMagnetism = SymbolMagnetism.NONE, var modifyingType : SymbolModifierType = SymbolModifierType.NONE, var modifierRatio : Float = 0f)