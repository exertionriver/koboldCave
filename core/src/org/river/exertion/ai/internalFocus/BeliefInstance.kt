package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.PresentSymbolInstance

data class BeliefInstance(override var symbol : PresentSymbolInstance = PresentSymbolInstance(position = 0f), override var conviction : Float = 0f) : IBelief {

    override var impactors = mutableSetOf<InternalFocusImpactor>()

}