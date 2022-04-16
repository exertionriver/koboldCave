package org.river.exertion.ai.symbol

import org.river.exertion.ai.internalFocus.InternalFocusType

data class BeliefImpactor(var type: SymbolType, var impactingSymbol: SymbolType, val convictionImpactor : Float) {

}