package org.river.exertion.ai.internalFocus

import org.river.exertion.ai.symbol.SymbolType

data class TargetInstance(override var targetSymbolType : SymbolType = SymbolType.NONE, override var satisfactionSymbolType: SymbolType = SymbolType.NONE, override var urgency : Float = 0f) : ITarget {

    override var impactors = mutableSetOf<InternalFocusImpactor>()

    fun expresses(need : NeedInstance) = (targetSymbolType == need.targetSymbolType) && (satisfactionSymbolType == need.satisfactionSymbolType)
    fun expresses(want : WantInstance) = (targetSymbolType == want.targetSymbolType) && (satisfactionSymbolType == want.satisfactionSymbolType)
}