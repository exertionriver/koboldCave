package org.river.exertion.ai.symbol

import org.river.exertion.ai.internalFocus.InternalFocusType

data class VisionImpactor(var type: SymbolType, var impactingSymbol: SymbolType, val convictionImpactor : Float, val accomplismentImpactor : Float) {

    companion object {
        fun BeliefImpactor.visionImpactor(accomplishmentImpactor: Float) = VisionImpactor(this.type, this.impactingSymbol, this.convictionImpactor, accomplishmentImpactor)
    }
}