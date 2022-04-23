package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.SymbolInstance

data class BeliefInstance(override var symbol : SymbolInstance = SymbolInstance(position = 0f), override var conviction : Float = 0f) : IBelief {

    override var impactors = mutableSetOf<InternalFocusImpactor>()

}