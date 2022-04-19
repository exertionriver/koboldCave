package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.SymbolType

data class WantInstance(override var targetSymbolType : SymbolType = SymbolType.NONE
                        , override var triggerSymbolType : SymbolType = SymbolType.NONE
                        , override var satisfactionSymbolType : SymbolType = SymbolType.NONE
                        , override var urgency : Float = 0f) : IWant {

    override var impactors = mutableSetOf<InternalFocusImpactor>()

    fun expressedIn(targets : MutableSet<TargetInstance>) = targets.any { it.expresses(this) }
}