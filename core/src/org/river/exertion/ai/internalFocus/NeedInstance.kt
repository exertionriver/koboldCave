package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.SymbolType

data class NeedInstance(override var targetSymbolType : SymbolType = SymbolType.NONE
                        , override var triggerSymbolType : SymbolType = SymbolType.NONE
                        , override var satisfactionSymbolType : SymbolType = SymbolType.NONE
                        , override var urgency : Float = 0f) : INeed {

    override var impactors = mutableSetOf<InternalFocusImpactor>()

    fun expressedIn(targets : Set<TargetInstance>) = targets.any { it.expresses(this) }

    fun expressedAsTargetInstance() = TargetInstance(
        targetSymbolType = this.targetSymbolType,
        satisfactionSymbolType = this.satisfactionSymbolType,
        urgency = this.urgency
    )
}