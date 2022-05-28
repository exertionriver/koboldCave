package org.river.exertion.ai.internalSymbol.core

import org.river.exertion.ai.internalSymbol.perceivedSymbols.NonePerceivedSymbol

data class AbsentSymbolModifier(var modifyingSymbol : IPerceivedSymbol = NonePerceivedSymbol, var symbolModifierType: SymbolModifierType, var modifierRatio : Float = 0f, var impactorRatio : Float = 0f)
