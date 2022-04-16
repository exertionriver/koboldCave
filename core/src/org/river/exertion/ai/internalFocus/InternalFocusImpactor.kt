package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.SymbolType
import org.river.exertion.ai.symbol.Vision

data class InternalFocusImpactor(override var type : InternalFocusType, override var targetSymbolType : SymbolType, val impactingSymbolType : SymbolType, var convictionImpactor : Float, var accomplishmentImpactor : Float) : IInternalFocus