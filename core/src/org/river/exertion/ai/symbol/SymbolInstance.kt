package org.river.exertion.ai.symbol

data class SymbolInstance(override var type : SymbolType = SymbolType.NONE, override var referent: ReferentType = SymbolType.NONE, override var presence: Float = 0f) : ISymbol
